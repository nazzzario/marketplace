package com.teamchallenge.marketplace.common.email.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    @Value("${mail.template.path.user}")
    private String userTemplatePath;

    private final JavaMailSender javaMailSender;

    @Override
    @Async
    public void sendPasswordResetToken(String email, String passwordResetToken) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("komirka.dev@ukr.net");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Reset forgotten password");
        simpleMailMessage.setText(passwordResetToken);

        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendEmail(final String to, final String message, final String subject) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(message, true);
            helper.setTo(to);
            helper.setFrom("komirka.dev@ukr.net", "Komirka");
            helper.setSubject(subject);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new ClientBackendException(ErrorCode.EMAIL_NOT_EXISTS);
        }
    }

    @Override
    public String buildMsgForUser(String message) {
        String emailTemplate = readHtmlTemplateFromFile(userTemplatePath);

        emailTemplate = emailTemplate.replace("{{message}}", message);
        return emailTemplate;
    }

    private String readHtmlTemplateFromFile(final String filePath) {
        ClassPathResource resource = new ClassPathResource(filePath);

        byte[] byteArray;
        try {
            byteArray = FileCopyUtils.copyToByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.EMAIL_NOT_EXISTS);
        }
        return new String(byteArray, StandardCharsets.UTF_8);
    }
}
