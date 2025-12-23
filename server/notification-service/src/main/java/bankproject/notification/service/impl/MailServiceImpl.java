package bankproject.notification.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import bankproject.notification.entity.Mail;
import bankproject.notification.service.MailService;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public void send(Mail theMail) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("dummybankprojectemail@gmail.com");
        message.setSubject(theMail.getSubject());
        message.setText(
                theMail.getBody() + "\n\nFrom: " + theMail.getEmail() + "\n\nSent Date: " + theMail.getSentDate());
        javaMailSender.send(message);
    }

    @Override
    public void transactionMail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        javaMailSender.send(message);
    }

    @Override
    public void sendMail(String email, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setText(link);
        message.setSubject("Reset Password");
        javaMailSender.send(message);
    }
}
