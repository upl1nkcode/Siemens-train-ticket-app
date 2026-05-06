package com.trainsystem.service;

import com.trainsystem.model.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingConfirmation(Booking booking) {
        String subject = "Booking Confirmation — #" + booking.getId();
        String body = String.format(
                "Dear %s,%n%n"
                + "Your booking has been confirmed!%n%n"
                + "Booking ID: %d%n"
                + "Train: %s%n"
                + "Route: %s%n"
                + "Departure: %s%n"
                + "Seats: %d%n%n"
                + "Thank you for choosing our service!",
                booking.getPassengerName(),
                booking.getId(),
                booking.getSchedule().getTrain().getName(),
                booking.getSchedule().getRoute().getName(),
                booking.getSchedule().getDepartureTime(),
                booking.getSeatsBooked()
        );

        sendEmail(booking.getPassengerEmail(), subject, body);
    }

    public void sendDelayNotification(Booking booking, int delayMinutes) {
        String subject = "Train Delay Notice — " + booking.getSchedule().getTrain().getName();
        String body = String.format(
                "Dear %s,%n%n"
                + "We regret to inform you that your train has been delayed.%n%n"
                + "Booking ID: %d%n"
                + "Train: %s%n"
                + "Route: %s%n"
                + "Original Departure: %s%n"
                + "Delay: %d minute(s)%n%n"
                + "We apologize for the inconvenience.",
                booking.getPassengerName(),
                booking.getId(),
                booking.getSchedule().getTrain().getName(),
                booking.getSchedule().getRoute().getName(),
                booking.getSchedule().getDepartureTime(),
                delayMinutes
        );

        sendEmail(booking.getPassengerEmail(), subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.warn("Failed to send email to {} — {}", to, e.getMessage());
        }
    }
}
