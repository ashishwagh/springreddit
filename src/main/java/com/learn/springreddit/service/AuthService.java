package com.learn.springreddit.service;

import com.learn.springreddit.dto.RegisterRequest;
import com.learn.springreddit.entity.NotificationEmail;
import com.learn.springreddit.entity.User;
import com.learn.springreddit.entity.VerificationToken;
import com.learn.springreddit.exception.SpringRedditException;
import com.learn.springreddit.repository.UserRepository;
import com.learn.springreddit.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;


    public void signUp(RegisterRequest registerRequest) {

        log.info("Registering the user: " + registerRequest.getUsername());
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(registerRequest.getPassword());
        user.setEmailId(registerRequest.getEmail());
        user.setEnabled(false);
        user.setCreatedOn(Instant.now());
        userRepository.save(user);

        String token = getAuthToken(user);

        mailService.sendEmail(new NotificationEmail("Please Activate your Account",
                user.getEmailId(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
    }


    private String getAuthToken(User user) {
        log.info("Storing authentication token for user: " + user.getUsername());
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepository.save(verificationToken);

        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
        fetchUserAndEnable(verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token")));
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String username = verificationToken.getUser().getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new SpringRedditException("User not found with name - " + username));
        user.setEnabled(true);
        userRepository.save(user);
    }
}
