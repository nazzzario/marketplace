package com.teamchallenge.marketplace.common.email.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.SendEmailRequest;
import com.teamchallenge.marketplace.common.email.service.EmailService;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
@RequiredArgsConstructor
public class ResendEmailServiceImpl implements EmailService {

    private final Resend resend;

    // TODO: 11/12/23 add html page
    @Override
    @Async
    public void sendPasswordResetToken(String email, String passwordResetToken) {
        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                .from("marketplace@resend.dev")
                .to(email)
                .subject("Password reset")
                .html("<p>This is your password reset token <strong>" + passwordResetToken + "</strong></p>")
                .build();
        try {
            resend.emails().send(sendEmailRequest);
        } catch (ResendException e) {
            throw new ClientBackendException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }
}
