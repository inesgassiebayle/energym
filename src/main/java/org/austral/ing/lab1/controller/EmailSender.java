package org.austral.ing.lab1.controller;

import com.google.gson.Gson;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailSender {
    Gson gson = new Gson();
    private Properties props;
    private String username;
    private String password;
    public EmailSender(){
        this.props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        this.username = "energym355@gmail.com";
        this.password= "lbee vhzj bqrq wgeh";

    }
    public String sendEmail(String email, String subject, String message1) {
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject(subject);
            message.setText(message1);
            Transport.send(message);
            return "Email sent succesfully";
        } catch (MessagingException e) {
            return "Error sending email";
        }
    }
}
