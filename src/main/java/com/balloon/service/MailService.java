package com.balloon.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

/**
 * Created by adam on 2017.03.12..
 */
@Service
@Slf4j
@EnableAsync
public class MailService {

    @Value("${app.email.from}")
    private String emailFrom;

    @Value("${app.email.to}")
    private String emailTo;

    @Value("${app.email.subject}")
    private String subject;

    @Autowired
    JavaMailSender mailSender;

    @Async
    public void sendMail(String content) {
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setFrom(emailFrom);
            messageHelper.setTo(emailTo);
            messageHelper.setSubject(subject);
            messageHelper.setText(content, true);
        };
        try {
            mailSender.send(messagePreparator);
        } catch (MailException e) {
            log.error("Error during alert e-mail sending: ", e);
        }
    }
}
