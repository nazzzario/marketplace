package com.teamchallenge.marketplace.common.email.service;

public interface EmailService {

    void sendPasswordResetToken(String email, String passwordResetToken);
}
