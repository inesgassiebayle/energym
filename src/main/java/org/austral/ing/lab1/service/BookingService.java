package org.austral.ing.lab1.service;

import org.austral.ing.lab1.controller.EmailSender;
import org.austral.ing.lab1.controller.ReminderService;
import org.austral.ing.lab1.model.BookedLesson;
import org.austral.ing.lab1.model.Lesson;
import org.austral.ing.lab1.model.Student;
import org.austral.ing.lab1.queries.LessonBookings;

import java.util.concurrent.CompletableFuture;

public class BookingService {
  private final EmailSender emailSender;
  private final ReminderService reminderService;
  private final LessonBookings lessonBookings;

  public BookingService(EmailSender emailSender, ReminderService reminderService) {
    this.emailSender = emailSender;
    this.reminderService = reminderService;
    this.lessonBookings = new LessonBookings();
  }



  public void deleteBooking(BookedLesson bookedLesson) {
    if (bookedLesson.state()) {
      bookedLesson.deactivate();
      lessonBookings.persist(bookedLesson);
      Lesson lesson = bookedLesson.getLesson();
      reminderService.cancelReminder(bookedLesson.getId());
      Student student = bookedLesson.getStudent();
      CompletableFuture.runAsync(()-> emailSender.sendEmail(student.getUser().getEmail(), "Booking cancellation", "You have successfully cancelled the booking of the " + lesson.getName() + " class with " + lesson.getProfessor().getUser().getUsername() + " on " + lesson.getStartDate() + " at " + lesson.getTime()));
    }
  }
}
