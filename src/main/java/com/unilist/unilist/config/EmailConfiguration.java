package com.unilist.unilist.config;

import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import com.resend.*;
import java.util.Properties;

@Configuration
public class EmailConfiguration {
    @Value("${spring.mail.username}")

    private String emailUsername;

    @Value("${spring.mail.password}")
    private String emailPassword;

//    @Value("${RESEND_API_KEY}")
//    private static String resendApiKey;


//    public static void sendSimpleEmail() throws ResendException {
//
//        Resend resend = new Resend(resendApiKey);
//
//        CreateEmailOptions params = CreateEmailOptions.builder()
//                .from("Acme <onboarding@resend.dev>")
//                .to("delivered@resend.dev")
//                .subject("Hello World")
//                .html("<p>It works!</p>")
//                .build();
//
//        CreateEmailResponse response = resend.emails().send(params);
//        System.out.println("Email sent with ID: " + response.getId());
//
//    }
}
