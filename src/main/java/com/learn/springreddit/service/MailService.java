package com.learn.springreddit.service;

import com.learn.springreddit.entity.NotificationEmail;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendEmail(NotificationEmail notificationEmail){
        MimeMessagePreparator mimeMessagePreparator= mimeMessage -> {
            MimeMessageHelper messageHelper =  new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom("springreddit@mail.com");
            messageHelper.setTo(notificationEmail.getRecipient());
            messageHelper.setSubject(notificationEmail.getSubject());
            messageHelper.setText(notificationEmail.getMessage());
        };
        try {
            mailSender.send(mimeMessagePreparator);
            log.info("Activation email sent!");
        } catch (Exception e){
            log.error("Exception while sending activation email to "+ notificationEmail.getRecipient()+ e);
        }
    }
}
