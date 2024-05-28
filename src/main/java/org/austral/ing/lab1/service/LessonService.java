package org.austral.ing.lab1.service;

import org.austral.ing.lab1.Result;
import org.austral.ing.lab1.ServiceResult;
import org.austral.ing.lab1.controller.EmailSender;
import org.austral.ing.lab1.controller.ReminderService;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.LessonBookings;
import org.austral.ing.lab1.queries.Lessons;
import org.checkerframework.checker.units.qual.A;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LessonService {
  private final EmailSender emailSender;
  private final ReminderService reminderService;
  private final BookingService bookingService;
  private final Lessons lessons;

  public LessonService(EmailSender emailSender, ReminderService reminderService) {
    this.emailSender = emailSender;
    this.reminderService = reminderService;
    this.bookingService = new BookingService(emailSender, reminderService);
    this.lessons = new Lessons();
  }


  public ServiceResult deleteLesson (Lesson lesson) {
    if (lesson == null) {
      return new ServiceResult(false, "Lesson not found");
    }
    if (!lesson.getState()) {
      return new ServiceResult(false, "Lesson not found");
    }
    lesson.deactivate();
    lessons.persist(lesson);
    Professor professor = lesson.getProfessor();
    CompletableFuture.runAsync(()-> emailSender.sendEmail(professor.getUser().getEmail(), "Cancelled lesson", "Your lesson :'"+ lesson.getName() + "' scheduled on the " + lesson.getStartDate().getDayOfWeek().toString()+"'s'" + "in between" + lesson.getStartDate().toString() + " at " + lesson.getTime().toString() + " in room " + lesson.getRoom().getName() + " for the activity " + lesson.getActivity().getName() + "has been canceled."));
    Set<BookedLesson> bookings = lesson.getBookings();
    for (BookedLesson bookedLesson: bookings) {
      bookingService.deleteBooking(bookedLesson);
    }
    return new ServiceResult(true, lesson.asJson());
  }

  public ServiceResult addLesson (String name, LocalDate date, LocalTime time, Room room, Activity activity, Professor professor) {
    if(name == null || date == null || time == null){
      return new ServiceResult(false, "Invalid input");
    }
    Lesson lesson = new Lesson(name, time, date);
    if (date.isBefore(LocalDate.now())) {
      return new ServiceResult(false, "Invalid date");
    }
    if (date.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
      return new ServiceResult(false, "Invalid time");
    }
    //get activity
    if (activity != null) {
      if(activity.state()){
        lesson.setActivity(activity);
      }
      else{
        return new ServiceResult(false, "Activity not found");
      }
    } else {
      // Handle case where activity is not found
      return new ServiceResult(false, "Activity not found");
    }
    //get professor
    if (professor != null) {
      if(professor.getUser().state()){
        lesson.setProfessor(professor);
      }
      else{
        // Handle case where professor is not found
        return new ServiceResult(false, "Professor not found");
      }
    } else {
      // Handle case where professor is not found
      return new ServiceResult(false, "Professor not found");
    }
    //get room
    if (room != null) {
      if(room.state()){
        lesson.setRoom(room);
      }
      else{
        // Handle case where professor is not found
        return new ServiceResult(false, "Room not found");
      }
    } else {
      // Handle case where professor is not found
      return new ServiceResult(false, "Room not found");
    }
    //fijarme que la activity de pueda realizar en el room especificado
    if (!room.getActivities().contains(activity)) {
      return new ServiceResult(false, "Activity not supported in the selected room");
    }
    //fijarme que el profesor no este ocupado en el horiario definido
    if (!isProfessorAvailable(professor, time, date)) {
      return new ServiceResult(false, "Professor is not available at the specified time");
    }
    //fijarme que el room no este en uso por otra actividad a la hora de la lesson
    if (!isRoomAvailable(room, time, date)) {
      return new ServiceResult(false, "Room is not available at the specified time");
    }
    EmailSender emailSender = new EmailSender();
    CompletableFuture.runAsync(()-> emailSender.sendEmail(professor.getUser().getEmail(), "New scheduled lesson", "You have a new lesson :'"+ lesson.getName()+ "' scheduled for " + date.toString() + " at " + time.toString() + " in room " + room.getName() + " for the activity " + activity.getName() + "."));
    lessons.persist(lesson);
    return new ServiceResult(true, lesson.asJson());
  }

  public boolean isProfessorAvailable(Professor professor, LocalTime time, LocalDate date) {
    List<Lesson> conflictingLessons = lessons.findLessonsByProfessorDateAndTime(professor.getUser().getUsername(), time, date);
    if (conflictingLessons == null) return true;
    List<Lesson> aliveLessons = new ArrayList<>();
    for(Lesson lesson: conflictingLessons){
      if(lesson.getState()){
        aliveLessons.add(lesson);
      }
    }
    return aliveLessons.isEmpty();
  }

  public boolean isRoomAvailable(Room room, LocalTime time, LocalDate date) {
    List<Lesson> conflictingLessons = lessons.findLessonsByRoomAndTime(room.getName(), time, date);
    if (conflictingLessons == null) return true;
    List<Lesson> aliveLessons = new ArrayList<>();
    for(Lesson lesson: conflictingLessons){
      if(lesson.getState()){
        aliveLessons.add(lesson);
      }
    }
    return aliveLessons.isEmpty();
  }

  public ServiceResult addConcurrentLessons (LocalDate startDate, LocalDate endDate, LocalTime time, String name, Professor professor, Room room, Activity activity) {
    if (activity == null) {
      return new ServiceResult(false, "Activity not found");
    }
    if(!activity.state()){
      return new ServiceResult(false, "Activity not found");
    }
    // Fetch and set Professor
    if (professor == null) {
      return new ServiceResult(false, "Professor not found");
    }
    if(!professor.getUser().state()){
      return new ServiceResult(false, "Professor not found");
    }
    //get room
    if (room == null) {
      return new ServiceResult(false, "Room not found");
    }
    if(!room.state()){
      return new ServiceResult(false, "Room not found");
    }
    if (!room.getActivities().contains(activity)) {
      return new ServiceResult(false, "Activity not supported in the selected room");
    }
    Set<Lesson> lessonsToAdd = new HashSet<>();
    final LocalDate finalStartDate = startDate;
    while (!startDate.isAfter(endDate)) {
      if (startDate.isBefore(LocalDate.now())) {
        return new ServiceResult(false, "Cannot create a class in a past date");
      }
      if (startDate.equals(LocalDate.now()) && time.isBefore(LocalTime.now())) {
        return new ServiceResult(false, "Cannot create a class in a past date");
      }
      if (!isProfessorAvailable(professor, time, startDate)) {
        return new ServiceResult(false, "Professor is not available at " + startDate.toString());
      }
      if (!isRoomAvailable(room, time, startDate)) {
        return new ServiceResult(false, "Room is not available at " + startDate.toString());
      }
      Lesson lesson = new Lesson(name, time, startDate);
      // Set the fetched Activity and Professor to each Lesson
      lesson.setActivity(activity);
      lesson.setProfessor(professor);
      lesson.setRoom(room);
      lessonsToAdd.add(lesson);
      startDate = startDate.plusWeeks(1);
    }
    // Persist all the lessons
    lessonsToAdd.forEach(lesson -> lessons.persist(lesson));
    EmailSender emailSender = new EmailSender();
    CompletableFuture.runAsync(()-> emailSender.sendEmail(professor.getUser().getEmail(), "New scheduled lesson", "You have a new lesson :'"+ name + "' scheduled on the " + finalStartDate.getDayOfWeek().toString() +"'s'" + "in between" + finalStartDate.toString() + " and "+ endDate + " at " + time.toString() + " in room " + room.getName() + " for the activity " + activity.getName() + "."));
    return new ServiceResult(true, "You have a new lesson :'"+ name + "' scheduled on the " + finalStartDate.getDayOfWeek().toString()+"'s'" + "in between" + finalStartDate.toString() + " and "+ endDate + " at " + time.toString() + " in room " + room.getName() + " for the activity " + activity.getName() + ".");
  }

  public ServiceResult modifyLesson (Lesson oldLesson, Activity newActivity, Room newRoom, Professor newProfessor, String newName, LocalTime newTime, LocalDate newDate) {
    final String oldActivity = oldLesson.getActivity().getName();
    final String oldRoom = oldLesson.getRoom().getName();
    final String oldProfessor = oldLesson.getProfessor().getUser().getUsername();
    final String oldDate = oldLesson.getStartDate().toString();
    final String oldName = oldLesson.getName();

    if(oldLesson == null){
      return new ServiceResult(false, "Lesson not found");
    }
    if(!oldLesson.getState()){
      return new ServiceResult(false, "Lesson not found");
    }
    if(newActivity == null || newRoom == null || newProfessor == null){
      return new ServiceResult(false, "Activity, Professor or Room not found");
    }
    if(!newActivity.state() || !newRoom.state() || !newProfessor.getUser().state()){
      return new ServiceResult(false, "Activity, Professor or Room not found");
    }
    boolean professorChanged = !oldLesson.getProfessor().equals(newProfessor);
    boolean roomChanged = !oldLesson.getRoom().equals(newRoom);
    boolean activityChanged = !oldLesson.getActivity().equals(newActivity);
    boolean dateChanged = !oldLesson.getStartDate().equals(newDate);
    boolean timeChanged = !oldLesson.getTime().equals(newTime);
    if (professorChanged && !isProfessorAvailable(newProfessor, newTime, newDate)) {
      return new ServiceResult(false, "Professor is not available");
    }
    if (roomChanged && (!isRoomAvailable(newRoom, newTime, newDate) ||
      !newRoom.getActivities().contains(newActivity))) {
      return new ServiceResult(false, "Room is not available or does not support the activity");
    }
    if (activityChanged && !newRoom.getActivities().contains(newActivity)) {
      return new ServiceResult(false, "New activity not supported in the selected room");
    }
    if (dateChanged || timeChanged) {
      // Si el profesor no cambió pero la fecha/hora sí, verificar la disponibilidad del profesor en el nuevo horario
      if (!professorChanged && !isProfessorAvailable(oldLesson.getProfessor(), newTime, newDate)) {
        return new ServiceResult(false, "Professor is not available at the new time/date");
      }
      // Si la sala no cambió pero la fecha/hora sí, verificar la disponibilidad de la sala en el nuevo horario
      if (!roomChanged && !isRoomAvailable(oldLesson.getRoom(), newTime, newDate)) {
        return new ServiceResult(false, "Professor is not available at the new time/date");
      }
      // Si el profesor cambió, y también la fecha/hora, verificar la disponibilidad del nuevo profesor
      if (professorChanged && !isProfessorAvailable(newProfessor, newTime, newDate)) {
        return new ServiceResult(false, "New professor is not available at the new time/date");
      }
      // Si la sala cambió, y también la fecha/hora, verificar la disponibilidad de la nueva sala
      if (roomChanged && (!isRoomAvailable(newRoom, newTime, newDate) || !newRoom.getActivities().contains(newActivity))) {
        return new ServiceResult(false, "New room is not available or does not support the activity at the new time/date");
      }
    }
    oldLesson.setName(newName);
    oldLesson.setRoom(newRoom);
    oldLesson.setActivity(newActivity);
    oldLesson.setProfessor(newProfessor);
    oldLesson.setTime(newTime);
    oldLesson.setStartDate(newDate);
    lessons.persist(oldLesson);
    Set<BookedLesson> bookedLessons = oldLesson.getBookings();
    for (BookedLesson bookedLesson: bookedLessons) {
      if (bookedLesson.state()) {
        reminderService.cancelReminder(oldLesson.getId());
        reminderService.scheduleReminder(oldLesson.getId(), oldLesson.getStartDate(), oldLesson.getTime(), bookedLesson.getStudent().getUser().getEmail(), "Class reminder", "You have a class with " + oldLesson.getProfessor().getUser().getUsername() + " on " + oldLesson.getStartDate() + " at " + oldLesson.getTime() + " in room " + oldLesson.getRoom().getName());
        CompletableFuture.runAsync(()-> emailSender.sendEmail(bookedLesson.getStudent().getUser().getEmail(), "Lesson change", "Your booking for " + oldName + " with " + oldProfessor + " for " + oldActivity + " in " + oldRoom + " on " + oldDate +" has changed, new lesson on " + oldLesson.getName() + " class with " + oldLesson.getProfessor().getUser().getUsername() + " on " + oldLesson.getStartDate() + " at " + oldLesson.getTime()));
      }
    }
    return new ServiceResult(true, oldLesson.asJson());
  }
}
