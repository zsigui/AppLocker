package com.noahmob.AppLocker.Utils;

import android.util.Log;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class EmailUtils {
    private static String EMAIL_CONTENT_AFTER = "\n\nThank you for using App Locker!\nSincerily,\nApp Locker Team";
    private static String EMAIL_CONTENT_BEFORE = "Hi Dear,\nYour password resetting code is:\n\n";
    private static String EMAIL_TITLE = "Password resetting code from App Locker";
    private static String HOST = "smtp.qq.com";
    private static String MAIL_FROM_NAME = "noahapp@noahmob.com";
    private static String MAIL_FROM_PSW = "nm789456";
    private static String PORT = "25";

    public static void sendEmail(String to, String subject, String content) throws Exception, MessagingException {
        String host = HOST;
        String address = MAIL_FROM_NAME;
        String from = MAIL_FROM_NAME;
        String password = MAIL_FROM_PSW;
        if ("".equals(to) || to == null) {
            to = "147181878@qq.com";
        }
        SendEmail(host, address, from, password, to, PORT, EMAIL_TITLE, EMAIL_CONTENT_BEFORE + content + EMAIL_CONTENT_AFTER);
    }

    public static void SendEmail(String host, String address, String from, String password, String to, String port, String subject, String content) throws Exception {
        Properties props = System.getProperties();
//        props.put("mail.smtp.starttls.enable", ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        props.put("mail.smtp.starttls.enable", "false");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.user", address);
        props.put("mail.smtp.password", password);
        props.put("mail.smtp.port", port);
//        props.put("mail.smtp.auth", ServerProtocol.DIALOG_RETURN_SCOPES_TRUE);
        props.put("mail.smtp.auth", "false");
        Log.i("Check", "done pops");
        Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource("".getBytes(), "text/plain"));
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        Log.i("Check", "done sessions");
        Multipart multiPart = new MimeMultipart();
        message.addRecipient(RecipientType.TO, new InternetAddress(to));
        Log.i("Check", "added recipient");
        message.setSubject(subject);
        message.setContent(multiPart);
        message.setText(content);
        Log.i("check", "transport");
        Transport transport = session.getTransport("smtp");
        Log.i("check", "connecting");
        transport.connect(host, address, password);
        Log.i("check", "wana send");
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
        Log.i("check", "sent");
    }
}
