package com.teamchallenge.marketplace.common.config.email;

import com.resend.Resend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class EmailConfig {

    @Value("${spring.mail.password}")
    private String apiKey;

    @Bean
    public Resend resendConfig(){
        return new Resend(apiKey);
    }
}
