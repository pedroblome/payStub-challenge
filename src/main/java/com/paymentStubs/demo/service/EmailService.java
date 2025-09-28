package com.paymentStubs.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an email with a PDF attachment.
     * @param to The recipient's email address.
     * @param subject The email subject.
     * @param body The email body text.
     * @param attachment The PDF content as a byte array.
     * @param attachmentName The desired filename for the attachment (e.g., "paystub.pdf").
     * @throws MessagingException If there's an error creating or sending the message.
     */
    public void sendEmailWithAttachment(String to, String subject, String body, byte[] attachment, String attachmentName) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body);

        helper.addAttachment(attachmentName, new ByteArrayResource(attachment));

        mailSender.send(mimeMessage);
    }
}
