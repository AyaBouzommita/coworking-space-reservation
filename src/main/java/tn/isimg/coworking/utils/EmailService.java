package tn.isimg.coworking.utils;

import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import tn.isimg.coworking.model.Reservation;
import tn.isimg.coworking.model.User;

public class EmailService {

    // PLACEHOLDERS - To be replaced by User/Env variables
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USER = "aya.bouzommita@isimg.tn"; 
    private static final String EMAIL_PASS = "iccc uhqs aeth leum"; 

    private Session getSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USER, EMAIL_PASS);
            }
        });
    }

    public void sendReservationConfirmation(User user, Reservation reservation) {
        String subject = "Confirmation de réservation - Cospace";
        String content = "Bonjour " + user.getFullName() + ",\n\n"
                + "Votre réservation a été confirmée avec succès.\n"
                + "Salle : " + getRoomNameSafe(reservation) + "\n"
                + "Début : " + reservation.getStartDateTime().toString().replace("T", " ") + "\n"
                + "Fin : " + reservation.getEndDateTime().toString().replace("T", " ") + "\n"
                + "Prix Total : " + reservation.getTotalPrice() + " DT\n\n"
                + "Merci de votre confiance.\n"
                + "L'équipe Cospace.";

        sendEmail(user.getEmail(), subject, content);
    }

    public void sendReservationCancellation(User user, Reservation reservation) {
        String subject = "Annulation de réservation - Cospace";
        String content = "Bonjour " + user.getFullName() + ",\n\n"
                + "Votre réservation a été annulée.\n"
                + "Salle : " + getRoomNameSafe(reservation) + "\n"
                + "Date : " + reservation.getStartDateTime().toString().replace("T", " ") + "\n\n"
                + "Si vous n'êtes pas à l'origine de cette action, veuillez nous contacter.\n"
                + "L'équipe Cospace.";

        sendEmail(user.getEmail(), subject, content);
    }

    // Helper to avoid null pointer if room name isn't populated
    private String getRoomNameSafe(Reservation r) {
        return (r.getRoomName() != null) ? r.getRoomName() : "Salle ID " + r.getRoomId();
    }

    private void sendEmail(String toEmail, String subject, String body) {
        // Run in a separate thread to not block the user response
        new Thread(() -> {
            try {
                Session session = getSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(EMAIL_USER));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(body);

                Transport.send(message);
                System.out.println("Email sent successfully to " + toEmail);
            } catch (MessagingException e) {
                e.printStackTrace();
                System.err.println("Failed to send email to " + toEmail + ": " + e.getMessage());
            }
        }).start();
    }
}
