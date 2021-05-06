package com.assignup.ie.email;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailSender{

	private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    @Autowired	
    private  JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to,String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage(); 
		message.setFrom("rlusppy@gmail.com");
		message.setTo(to); 
		message.setSubject(subject); 
		message.setText(text);
		mailSender.send(message);
		System.out.println("Mail has been sent");
    }
	
}
