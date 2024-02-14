package com.teamchallenge.marketplace.common.email.service;

public interface EmailService {

    void sendPasswordResetToken(String email, String passwordResetToken);

    /**
     * Sends an email from the specified email address.
     *
     * @param to      The email address of the recipient.
     * @param message The message text to be sent in the email.
     * @param subject The subject of the email.
     */
    void sendEmail(String to, String message, String subject);

    /**
     * Builds a message for a user.
     *
     * @param path    The path of html document
     * @param message The message text to be sent in the email.
     * @return A formatted html-message string for user.
     */
    String buildMsgForUser(String path, String message);
}
