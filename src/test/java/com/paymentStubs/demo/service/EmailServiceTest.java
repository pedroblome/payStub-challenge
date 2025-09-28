package com.paymentStubs.demo.service;

import jakarta.mail.Address;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    @DisplayName("Should correctly construct and send an email with an attachment")
    void sendEmailWithAttachment_ShouldConstructAndSendMessage() throws Exception {
        String to = "recipient@example.com";
        String subject = "Test Subject";
        String body = "Test Body";
        byte[] attachment = "test-attachment-content".getBytes();
        String attachmentName = "test.pdf";

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.sendEmailWithAttachment(to, subject, body, attachment, attachmentName);

        verify(mailSender, times(1)).send(any(MimeMessage.class));

        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subjectCaptor = ArgumentCaptor.forClass(String.class);

        ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(mimeMessageCaptor.capture());

        when(mimeMessage.getRecipients(MimeMessage.RecipientType.TO))
                .thenReturn(
                        new jakarta.mail.internet.InternetAddress[] { new jakarta.mail.internet.InternetAddress(to) });
        when(mimeMessage.getSubject()).thenReturn(subject);

        MimeMessage sentMessage = mimeMessageCaptor.getValue();
        assertEquals(to, sentMessage.getRecipients(MimeMessage.RecipientType.TO)[0].toString());
        assertEquals(subject, sentMessage.getSubject());
    }
}