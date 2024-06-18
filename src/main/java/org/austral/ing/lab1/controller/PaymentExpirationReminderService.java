package org.austral.ing.lab1.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

public class PaymentExpirationReminderService {
  private EmailSender emailSender;
  private Timer timer;
  private Map<Long, TimerTask> taskMap;

  public PaymentExpirationReminderService(EmailSender emailSender) {
    this.emailSender = emailSender;
    this.timer = new Timer();
    this.taskMap = new HashMap<>();
  }

  public void scheduleReminder(Long membershipId, LocalDate eventDate, String email) {
    ZoneId zone = ZoneId.systemDefault();
    Date combinedDateTime = Date.from(LocalDate.now().atTime(LocalTime.now()).atZone(zone).toInstant());
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(combinedDateTime);
    calendar.add(Calendar.DAY_OF_MONTH, -1);

    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        emailSender.sendEmail(email, "Don't forget to renew your 'Energym' membership", "This is a reminder to pay for the membership at 'Energym' to continue enjoying our services. Your current membership will expire tomorrow.");
        System.out.println("Email reminder to " + email);
        taskMap.remove(membershipId);
      }
    };

    timer.schedule(task, calendar.getTime());
    taskMap.put(membershipId, task);
  }


  public void cancelReminder(Long membershipId) {
    TimerTask task = taskMap.get(membershipId);
    if (task != null) {
      task.cancel();
      timer.purge();
      taskMap.remove(membershipId);
    }
  }
}
