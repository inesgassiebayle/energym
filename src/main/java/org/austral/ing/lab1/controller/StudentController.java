package org.austral.ing.lab1.controller;

import com.google.gson.Gson;
import org.austral.ing.lab1.Result;
import org.austral.ing.lab1.dto.BookingDto;
import org.austral.ing.lab1.dto.ConcurrentBookingDto;
import org.austral.ing.lab1.dto.ProfessorDateTimeDto;
import org.austral.ing.lab1.model.*;
import org.austral.ing.lab1.queries.*;
import spark.Request;
import spark.Response;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class StudentController {
    private final Users users;
    private final Students students;
    private final Lessons lessons;
    private final Professors professors;
    private final LessonBookings lessonBookings;
    private final Reviews reviews;
    private final Gson gson = new Gson();
    private final EmailSender emailSender = new EmailSender();
    private final ReminderService reminderService = new ReminderService(emailSender);

    public StudentController() {
        this.users = new Users();
        this.students = new Students();
        this.lessons = new Lessons();
        this.professors = new Professors();
        this.lessonBookings = new LessonBookings();
        this.reviews = new Reviews();
    }

    public String bookClass(Request req, Response res) {
        BookingDto dto = gson.fromJson(req.body(), BookingDto.class);
        String professor = dto.getProfessor();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        LocalTime time = dto.getTime();
        String student = dto.getStudent();

        if (student == null) {
            res.status(400);
            return "Invalid input";
        }
        Student student1 = students.findStudentByUsername(student);
        if (student1 == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student1.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        if(professor == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(endDate==null || startDate == null|| time == null){
            res.status(400);
            return "Invalid date or time";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if(professor1 == null){
            res.status(400);
            return "Invalid input";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        if (endDate.equals(startDate)) {
            Result booking = bookSingleClass(professor1, startDate, time, student1);
            if (booking.getBooking().isEmpty()) {
                res.status(400);
                return "Invalid input";
            }
            lessonBookings.persist(booking.getBooking().get());
            reminderService.scheduleReminder(booking.getBooking().get().getId(), booking.getBooking().get().getLesson().getStartDate(), booking.getBooking().get().getLesson().getTime(), student1.getUser().getEmail(), "Class reminder", "You have a class with " + professor1.getUser().getUsername() + " on " + booking.getBooking().get().getLesson().getStartDate() + " at " + booking.getBooking().get().getLesson().getTime() + " in room " + booking.getBooking().get().getLesson().getRoom().getName());
            emailSender.sendEmail(student1.getUser().getEmail(), "Booking confirmation", "You have successfully booked a " + booking.getBooking().get().getLesson().getName() + " class with " + professor1.getUser().getUsername() + " on " + startDate + " at " + time);
            return booking.getBooking().get().asJson();
        }
        else {
            if(endDate.isBefore(startDate)) {
                res.status(400);
                return "Invalid input";
            }
            List<BookedLesson> bookings = new ArrayList<>();
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(7)) {
                Result booking = bookSingleClass(professor1, date, time, student1);
                if (booking.getBooking().isEmpty()) {
                    res.status(400);
                    return booking.getMessage().get();
                }
                bookings.add(booking.getBooking().get());
            }
            for (BookedLesson booking: bookings) {
                reminderService.scheduleReminder(booking.getId(), booking.getLesson().getStartDate(), booking.getLesson().getTime(), student1.getUser().getEmail(), "Class reminder", "You have a class with " + professor1.getUser().getUsername() + " on " + booking.getLesson().getStartDate() + " at " + booking.getLesson().getTime() + " in room " + booking.getLesson().getRoom().getName());
                lessonBookings.persist(booking);
            }
            Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, time, startDate).get(0);
            emailSender.sendEmail(student1.getUser().getEmail(), "Booking confirmation", "You have successfully booked the" + lesson.getName() +  "classes with " + professor1.getUser().getUsername() + " between " + startDate + " and " + endDate + " at " + time);
            return "Succesfull bookings";
        }
    }


    public Result bookSingleClass(Professor professor, LocalDate date, LocalTime time, Student student) {
        if (!correctDate(date, time)) {
            return new Result("Invalid date");
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor.getUser().getUsername(), time, date).get(0);
        if(lesson == null){
            return new Result("Lesson does not exist");
        }
        if(!lesson.getState()) {
            return new Result("Lesson does not exist");
        }
        List<BookedLesson> booking = lessonBookings.findBookingByDateAndTimeAndStudent(date, time, student);
        if (booking == null) {
            return new Result(new BookedLesson(student, lesson));
        }
        boolean deactivated = false;
        BookedLesson b = null;
        for (BookedLesson bookedLesson: booking) {
            if (bookedLesson.state()) {
                if (bookedLesson.getLesson().getProfessor().equals(professor)) {
                    return new Result(bookedLesson);
                }
                else {
                    return new Result("Lesson already booked");
                }
            }
            else {
                if (bookedLesson.getLesson().equals(lesson)) {
                    deactivated = true;
                    b = bookedLesson;
                }
            }
        }
        if (deactivated) {
            b.activate();
            return new Result(b);
        }
        return new Result(new BookedLesson(student, lesson));
    }

    public boolean correctDate(LocalDate date, LocalTime time) {
        LocalDate nowDate = LocalDate.now();
        if (date.isBefore(nowDate)) {
            return false;
        }
        else if (date.isAfter(nowDate)) {
            return true;
        }
        else {
            LocalTime nowTime = LocalTime.now();

            if ((nowTime.equals(time) || nowTime.isAfter(time)) && nowTime.isBefore(time.plusHours(1))) {
                return false;
            }

            else if (nowTime.isBefore(time)) {
                return true;
            }
            return false;
        }
    }

    public String classifyLessons(Request req, Response res) {
        String username = req.queryParams("username");
        String date = req.queryParams("date");
        String time = req.queryParams("time");
        String professor = req.queryParams("professor");
        if (date == null || date.isBlank() || time == null || time.isBlank()|| professor == null || professor.isBlank() || username == null || username.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        Student student = students.findStudentByUsername(username);
        if (student == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, date, time);
        Professor professor1 = professors.findProfessorByUsername(professor);
        if (professor1 == null) {
            res.status(400);
            return "Professor not found";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, dto.getTime(), dto.getDate()).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson not found";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson not active";
        }
        if (correctDate(lesson.getStartDate(), lesson.getTime())) {
            List<BookedLesson> bookings =new ArrayList<>();
            for (BookedLesson booking: lesson.getBookings()) {
                if(booking.state()) {
                    if (booking.getStudent().equals(student)) {
                        return "Future class booked";
                    }
                    bookings.add(booking);
                }
            }
            if (lesson.getRoom().getCapacity() <= bookings.size()) {
                return "Future class full";
            }
            return "Future class available";
        }
        else {
            List<BookedLesson> bookings =new ArrayList<>();
            for (BookedLesson booking: lesson.getBookings()) {
                if(booking.state()) {
                    if (booking.getStudent().equals(student)) {
                        Review review = reviews.findReviewByLessonAndStudent(lesson, student);
                        if(review != null) {
                            return "Past class booked and reviewed";
                        }
                        return "Past class booked";
                    }
                    bookings.add(booking);
                }
            }
            return "Past class not booked";
        }
    }

    public String deleteBooking(Request req, Response res) {
        BookingDto dto = new BookingDto(req.queryParams("professor"), req.queryParams("student"), req.queryParams("startDate"), req.queryParams("endDate"), req.queryParams("time"));
        String professor = dto.getProfessor();
        LocalDate startDate = dto.getStartDate();
        LocalDate endDate = dto.getEndDate();
        LocalTime time = dto.getTime();
        String student = dto.getStudent();

        if (student == null) {
            res.status(400);
            return "Invalid input";
        }
        Student student1 = students.findStudentByUsername(student);
        if (student1 == null) {
            res.status(400);
            return "Student not found";
        }
        if (!student1.getUser().state()) {
            res.status(400);
            return "Student not active";
        }
        if(professor == null){
            res.status(400);
            return "Invalid lesson name";
        }
        if(startDate==null || time == null){
            res.status(400);
            return "Invalid date or time";
        }

        Professor professor1 = professors.findProfessorByUsername(professor);
        if(professor1 == null){
            res.status(400);
            return "Invalid input";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        if(startDate.equals(endDate)) {
            Result booking = deleteSingleBooking(student1, professor1, startDate, time);
            if (booking.getBooking().isEmpty()) {
                res.status(400);
                return booking.getMessage().get();
            }
            lessonBookings.persist(booking.getBooking().get());
            reminderService.cancelReminder(booking.getBooking().get().getId());
            emailSender.sendEmail(student1.getUser().getEmail(), "Booking cancellation", "You have successfully cancelled the booking of the " + booking.getBooking().get().getLesson().getName() + " class with " + professor1.getUser().getUsername() + " on " + startDate + " at " + time);
            return booking.getBooking().get().asJson();
        }
        else {
            if(endDate.isBefore(startDate)) {
                res.status(400);
                return "Invalid input";
            }
            List<BookedLesson> bookings = new ArrayList<>();
            for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(7)) {
                Result booking = deleteSingleBooking(student1, professor1, date, time);
                if (booking.getBooking().isEmpty()) {
                    res.status(400);
                    return booking.getMessage().get();
                }
                bookings.add(booking.getBooking().get());
            }
            for (BookedLesson booking: bookings) {
                reminderService.cancelReminder(booking.getId());
                emailSender.sendEmail(student1.getUser().getEmail(), "Booking cancellation", "You have successfully cancelled the booking of the " + booking.getLesson().getName() + " class with " + professor1.getUser().getUsername() + " on " + startDate + " at " + time);
                lessonBookings.persist(booking);
            }
            return "Succesfull bookings";
        }

    }

    public Result deleteSingleBooking(Student student, Professor professor, LocalDate date, LocalTime time){
        if (!correctDate(date, time)) {
           return new Result("Invalid date or time");
        }

        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor.getUser().getUsername(), time, date).get(0);

        if(lesson == null){
            return new Result("Lesson does not exist");
        }

        if(!lesson.getState()) {
            return new Result("Lesson does not exist");
        }
        BookedLesson booking = lessonBookings.findBookingByLessonAndStudent(lesson, student);
        if (booking == null) {
            return new Result("Booking does not exist");
        }
        if (!booking.state()) {
            return new Result("Booking does not exist");
        }
        booking.deactivate();
        return new Result(booking);

    }


    public String checkConcurrency(Request req, Response res) {
        String professor = req.queryParams("professor");
        String stringDate = req.queryParams("date");
        String stringTime = req.queryParams("time");
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, stringDate, stringTime);
        if (professor == null || stringDate == null || stringTime == null) {
            res.status(400);
            return "Invalid input";
        }
        if (professor.isBlank() || stringDate.isBlank() || stringTime.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if (professor1 == null) {
            res.status(400);
            return "Professor not found";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, dto.getTime(), dto.getDate()).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson not found";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson not active";
        }

        List<Lesson> lessons2 = lessons.findConcurrentLessons(dto.getTime(), professor, lesson.getName(), lesson.getRoom().getName(), lesson.getActivity().getName(), dto.getDate().getDayOfWeek());
        List<Lesson> concurrentLessons = filter(lessons2, dto.getDate());
        if (concurrentLessons.size() == 1) {
            return "Not concurrent lessons";
        }
        else {
            LocalDate minDate = getMinimumDate(concurrentLessons);
            LocalDate maxDate = getMaximumDate(concurrentLessons);
            ConcurrentBookingDto concurrentBookingDto = new ConcurrentBookingDto(lesson.getStartDate().getDayOfWeek().toString(), minDate, maxDate);
            return gson.toJson(concurrentBookingDto);
        }
    }

    public List<Lesson> filter(List<Lesson> lessons, LocalDate date) {
        lessons.sort(Comparator.comparing(Lesson::getStartDate));

        List<Lesson> lessonsBeforeOrOnDate = new ArrayList<>();
        List<Lesson> lessonsAfterDate = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.getStartDate().isBefore(date) || lesson.getStartDate().isEqual(date)) {
                lessonsBeforeOrOnDate.add(lesson);
            } else {
                lessonsAfterDate.add(lesson);
            }
        }

        List<Lesson> filteredLessons = new ArrayList<>();

        filteredLessons.add(lessonsBeforeOrOnDate.get(lessonsBeforeOrOnDate.size()-1));

        for (int i = lessonsBeforeOrOnDate.size()-2; i >= 0 ; i--) {
            Lesson currentLesson = lessonsBeforeOrOnDate.get(i);
            Lesson lastLessonInFiltered = filteredLessons.get(filteredLessons.size() - 1);
            boolean sevenDaysBetween = lastLessonInFiltered.getStartDate().equals(currentLesson.getStartDate().plusDays(7));
            if (sevenDaysBetween) {
                filteredLessons.add(currentLesson);
            }
        }

        filteredLessons.sort(Comparator.comparing(Lesson::getStartDate));

        for (int i = 0; i < lessonsAfterDate.size() ; i++) {
            Lesson currentLesson = lessonsAfterDate.get(i);
            Lesson lastLessonInFiltered = filteredLessons.get(filteredLessons.size() - 1);
            boolean sevenDaysBetween = lastLessonInFiltered.getStartDate().equals(currentLesson.getStartDate().minusDays(7));
            if (sevenDaysBetween) {
                filteredLessons.add(currentLesson);
            }
        }
        return filteredLessons;
    }

    private LocalDate getMaximumDate(List<Lesson> lessons) {
        LocalDate max = lessons.get(0).getStartDate();
        for (Lesson lesson: lessons) {
            if (lesson.getState() && lesson.getStartDate().isAfter(max)) {
                max = lesson.getStartDate();
            }
        }
        return max;
    }

    private LocalDate getMinimumDate(List<Lesson> lessons) {
        LocalDate min = lessons.get(0).getStartDate();
        for (Lesson lesson: lessons) {
            if (lesson.getState() && lesson.getStartDate().isBefore(min)) {
                min = lesson.getStartDate();
            }
        }
        return min;
    }

    public String checkConcurrentBookings(Request req, Response res){
        String professor = req.queryParams("professor");
        String stringDate = req.queryParams("date");
        String stringTime = req.queryParams("time");
        String studentUsername = req.queryParams("student");
        ProfessorDateTimeDto dto = new ProfessorDateTimeDto(professor, stringDate, stringTime);
        if (professor == null || stringDate == null || stringTime == null || studentUsername == null) {
            res.status(400);
            return "Invalid input";
        }
        if (professor.isBlank() || stringDate.isBlank() || stringTime.isBlank() || studentUsername.isBlank()) {
            res.status(400);
            return "Invalid input";
        }
        Professor professor1 = professors.findProfessorByUsername(professor);
        if (professor1 == null) {
            res.status(400);
            return "Professor not found";
        }
        if (!professor1.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        Student student = students.findStudentByUsername(studentUsername);
        if (student == null) {
            res.status(400);
            return "Professor not found";
        }
        if (!student.getUser().state()) {
            res.status(400);
            return "Professor not active";
        }
        Lesson lesson = lessons.findLessonsByProfessorDateAndTime(professor, dto.getTime(), dto.getDate()).get(0);
        if (lesson == null) {
            res.status(400);
            return "Lesson not found";
        }
        if (!lesson.getState()) {
            res.status(400);
            return "Lesson not active";
        }

        Set<BookedLesson> bookings = student.getBookings();
        List<BookedLesson> activeBookings = new ArrayList<>();
        for (BookedLesson booking: bookings) {
            if (booking.state()) {
                activeBookings.add(booking);
            }
        }
        boolean notBooked = true;
        for (BookedLesson booking: activeBookings) {
            if (booking.getLesson().equals(lesson)) {
                notBooked = false;
            }
        }
        if (notBooked) {
            res.status(400);
            return "Lesson not booked";
        }
        List<BookedLesson> correspondingBookings = lessonBookings.findConcurrentBookings(dto.getTime(), professor, lesson.getName(), lesson.getRoom().getName(), lesson.getActivity().getName(), dto.getDate().getDayOfWeek(), student);
        List<BookedLesson> concurrentBookings = filterBookings(correspondingBookings, dto.getDate());
        if (concurrentBookings.size() == 1) {
            return "Not concurrent lessons";
        }
        else {
            LocalDate minDate = getMinimumBookingDate(concurrentBookings);
            LocalDate maxDate = getMaximumBookingDate(concurrentBookings);
            ConcurrentBookingDto concurrentBookingDto = new ConcurrentBookingDto(lesson.getStartDate().getDayOfWeek().toString(), minDate, maxDate);
            return gson.toJson(concurrentBookingDto);
        }
    }

    private LocalDate getMaximumBookingDate(List<BookedLesson> concurrentBookings) {
        LocalDate max = concurrentBookings.get(0).getLesson().getStartDate();
        for (BookedLesson bookedLesson: concurrentBookings) {
            if (bookedLesson.state() && bookedLesson.getLesson().getStartDate().isAfter(max)) {
                max = bookedLesson.getLesson().getStartDate();
            }
        }
        return max;
    }

    private LocalDate getMinimumBookingDate(List<BookedLesson> concurrentBookings) {
        LocalDate min = concurrentBookings.get(0).getLesson().getStartDate();
        for (BookedLesson booking: concurrentBookings) {
            if (booking.state() && booking.getLesson().getStartDate().isBefore(min)) {
                min = booking.getLesson().getStartDate();
            }
        }
        return min;
    }

    private List<BookedLesson> filterBookings(List<BookedLesson> bookings, LocalDate date) {
        bookings.sort(Comparator.comparing(booking -> booking.getLesson().getStartDate()));

        List<BookedLesson> bookingsBeforeOrOnDate = new ArrayList<>();
        List<BookedLesson> bookingsAfterDate = new ArrayList<>();

        for (BookedLesson booking : bookings) {
            if (booking.getLesson().getStartDate().isBefore(date) || booking.getLesson().getStartDate().isEqual(date)) {
                bookingsBeforeOrOnDate.add(booking);
            } else {
                bookingsAfterDate.add(booking);
            }
        }

        List<BookedLesson> filteredBookings = new ArrayList<>();

        filteredBookings.add(bookingsBeforeOrOnDate.get(bookingsBeforeOrOnDate.size()-1));

        for (int i = bookingsBeforeOrOnDate.size()-2; i >= 0 ; i--) {
            BookedLesson currentBooking = bookingsBeforeOrOnDate.get(i);
            BookedLesson lastBookingInFiltered = filteredBookings.get(filteredBookings.size() - 1);
            boolean sevenDaysBetween = lastBookingInFiltered.getLesson().getStartDate().equals(currentBooking.getLesson().getStartDate().plusDays(7));
            if (sevenDaysBetween) {
                filteredBookings.add(currentBooking);
            }
        }

        filteredBookings.sort(Comparator.comparing(booking -> booking.getLesson().getStartDate()));

        for (int i = 0; i < bookingsAfterDate.size() ; i++) {
            BookedLesson currentBooking = bookingsAfterDate.get(i);
            BookedLesson lastBookingInFiltered = filteredBookings.get(filteredBookings.size() - 1);
            boolean sevenDaysBetween = lastBookingInFiltered.getLesson().getStartDate().equals(currentBooking.getLesson().getStartDate().minusDays(7));
            if (sevenDaysBetween) {
                filteredBookings.add(currentBooking);
            }
        }
        return filteredBookings;
    }
}