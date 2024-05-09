package org.austral.ing.lab1.controller;

import java.util.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

public class ReminderService {
    private EmailSender emailSender;
    private Timer timer;
    private Map<Long, TimerTask> taskMap;

    public ReminderService(EmailSender emailSender) {
        this.emailSender = emailSender;
        this.timer = new Timer();
        this.taskMap = new HashMap<>();
    }

    public void scheduleReminder(Long bookingId, LocalDate eventDate, LocalTime time, String email, String subject, String message) {
        ZoneId zone = ZoneId.systemDefault();
        Date combinedDateTime = Date.from(eventDate.atTime(time).atZone(zone).toInstant());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(combinedDateTime);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                emailSender.sendEmail(email, subject, message);
            }
        };
        timer.schedule(task, calendar.getTime());
        taskMap.put(bookingId, task);
    }

    public void cancelReminder(Long bookingId) {
        TimerTask task = taskMap.get(bookingId);
        if (task != null) {
            task.cancel();
            timer.purge();
            taskMap.remove(bookingId);
        }
    }
}
