package bankproject.notification.service;

import bankproject.notification.entity.Mail;

public interface MailService {
    public void send(Mail theMail);

    public void transactionMail(String to, String subject, String body);

    public void sendMail(String email, String link);
}
