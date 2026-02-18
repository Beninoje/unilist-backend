package com.unilist.unilist.services;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    public void sendVerificationEmail(String to, String subject, String text) throws ResendException {

        Resend resend = new Resend(resendApiKey);

        try {
            CreateEmailOptions params = CreateEmailOptions.builder()
                    .from("Campora <noreply@campora.ca>")
                    .to(to)
                    .subject(subject)
                    .html(text)
                    .build();

            CreateEmailResponse response = resend.emails().send(params);
            System.out.println("Email sent with ID: " + response.getId());
        }catch (ResendException e){
            e.printStackTrace();
        }

    }
}
