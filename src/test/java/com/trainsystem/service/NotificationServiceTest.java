package com.trainsystem.service;

import com.trainsystem.TestEntityFactory;
import com.trainsystem.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock private JavaMailSender mailSender;

    @InjectMocks private NotificationService notificationService;

    private Booking booking;

    @BeforeEach
    void setUp() {
        Train train = TestEntityFactory.train(1L, "IR 1581", 200);
        Route route = TestEntityFactory.route(1L, "Bucharest - Brasov");
        Schedule schedule = TestEntityFactory.schedule(1L, train, route,
                LocalDateTime.of(2026, 6, 15, 8, 30));
        booking = TestEntityFactory.booking(10L, schedule, "Andrei", "andrei@test.com", 2);
    }

    @Test
    void SendBookingConfirmation_SendsEmailWithCorrectFields() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.sendBookingConfirmation(booking);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertThat(sent.getTo()).containsExactly("andrei@test.com");
        assertThat(sent.getSubject()).contains("Booking Confirmation");
        assertThat(sent.getText()).contains("Andrei", "IR 1581", "Bucharest - Brasov");
    }

    @Test
    void SendBookingConfirmation_MailFailure_LogsWarningInsteadOfThrowing() {
        doThrow(new RuntimeException("SMTP down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatCode(() -> notificationService.sendBookingConfirmation(booking))
                .doesNotThrowAnyException();
    }

    @Test
    void SendDelayNotification_SendsEmailWithDelayInfo() {
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        notificationService.sendDelayNotification(booking, 45);

        verify(mailSender).send(captor.capture());
        SimpleMailMessage sent = captor.getValue();

        assertThat(sent.getTo()).containsExactly("andrei@test.com");
        assertThat(sent.getSubject()).contains("Delay");
        assertThat(sent.getText()).contains("45 minute(s)", "Andrei");
    }

    @Test
    void SendDelayNotification_MailFailure_LogsWarningInsteadOfThrowing() {
        doThrow(new RuntimeException("SMTP down"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        assertThatCode(() -> notificationService.sendDelayNotification(booking, 30))
                .doesNotThrowAnyException();
    }
}
