package com.teamchallenge.marketplace.common.security.bean;

import com.teamchallenge.marketplace.user.persisit.entity.UserEntity;
import com.teamchallenge.marketplace.user.persisit.entity.enums.RoleEnum;
import com.teamchallenge.marketplace.user.persisit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RootUserInitializer implements CommandLineRunner {
    @Value("${user.root.credentials.username}")
    private String rootUsername;
    @Value("${user.root.credentials.email}")
    private String rootEmail;
    @Value("${user.root.credentials.password}")
    private String rootPassword;
    @Value("${user.root.credentials.phone}")
    private String rootPhone;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.countByRole(RoleEnum.ROOT) == 0){
            var user = new UserEntity();
            user.setUsername(rootUsername);
            user.setEmail(rootEmail);
            user.setPhoneNumber(rootPhone);
            user.setPassword(passwordEncoder.encode(rootPassword));
            user.setRole(RoleEnum.ROOT);
            user.setNonLocked(Boolean.TRUE);
            userRepository.save(user);
        }
    }
}