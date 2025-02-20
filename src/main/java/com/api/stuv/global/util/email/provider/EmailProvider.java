package com.api.stuv.global.util.email.provider;

import com.api.stuv.global.service.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class EmailProvider {

    private final JavaMailSender mailSender;
    private final String SUBJECT ="[STUV] 인증메일입니다.";
    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    public boolean sendMail(String email, String verificationCode) {
        try{
            redisService.save(email, verificationCode, Duration.ofMinutes(10));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            String htmlContent = getCertificationMessage(verificationCode);

            helper.setTo(email);
            helper.setSubject(SUBJECT);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);

        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }

        return true;
    }

    private String getCertificationMessage(String verificationCode) {
        String certificationMessage = "";

        certificationMessage += "<h1 style='text-align: center;'>[STUV] 인증메일 </h1>";
        certificationMessage += "<h2 style='text-align: center;'>인증 코드</h2>";
        certificationMessage += "<h3 style='text-align: center;'>" + verificationCode + "</h3>";
        certificationMessage += "<h3 style='text-align: center;'> 인증코드를 입력해주세요 </h3>";
        return certificationMessage;

    }

}
