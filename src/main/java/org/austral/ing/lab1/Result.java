package org.austral.ing.lab1;

import org.austral.ing.lab1.model.BookedLesson;

import java.util.Optional;

public class Result {
    private String message;
    private BookedLesson bookedLesson;

    public Result(String message) {
        this.message = message;
    }

    public Result(BookedLesson bookedLesson){
        this.bookedLesson = bookedLesson;
    }

    public Optional<String> getMessage() {
        if (message == null) {
            return Optional.empty();
        }
        return Optional.of(message);
    }

    public Optional<BookedLesson> getBooking() {
        if (bookedLesson == null) {
            return Optional.empty();
        }
        return Optional.of(bookedLesson);
    }

}
