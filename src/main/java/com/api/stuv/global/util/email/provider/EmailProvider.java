package com.api.stuv.global.util.email.provider;

import com.api.stuv.global.service.RedisService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final RedisService redisService;

    public void sendMail(String email, String verificationCode) {
        try{
            redisService.save(email, verificationCode, Duration.ofMinutes(10));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariable("code", verificationCode);
            context.setVariable("logoCid", "stuvLogo");
            String htmlContent = templateEngine.process("email-template", context);

            String subject = "[STUV] 인증메일입니다.";

            helper.setTo(email);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            ClassPathResource logo = new ClassPathResource("static/STUV.png");
            helper.addInline("stuvLogo", logo);

            mailSender.send(message);

        } catch (Exception exception) {
            log.error(exception.getMessage());
        }
    }
}
