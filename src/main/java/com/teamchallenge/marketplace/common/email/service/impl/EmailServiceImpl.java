package com.teamchallenge.marketplace.common.email.service.impl;

import com.teamchallenge.marketplace.common.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

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
}
