import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class GmailInbox {

    public static void main(String[] args) {
        GmailInbox gmail = new GmailInbox();
        gmail.read();
    }

    public void read() {
        Properties props = new Properties();
        
        try {
            //props.load(new FileInputStream(new File("./smtp.properties")));
           Session session = Session.getDefaultInstance(props, null);

           Store store = session.getStore("imaps");
           store.connect("smtp.gmail.com", "YOUR+EMAIL+ADDRESS+HERE@gmail.com","YOUR+PASSWORD+HERE");

           Folder inbox = store.getFolder("inbox");
           inbox.open(Folder.READ_ONLY);
           int messageCount = inbox.getMessageCount();

           System.out.println("Total Messages:- " + messageCount);

           Message[] messages = inbox.getMessages();
           System.out.println("------------------------------");
           miraMessage(messages);
           inbox.close(true);
           store.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void miraMessage(Message[] messagesToFilter) {

        for (int il = 0; il < messagesToFilter.length; il++) {
            try {
                if (messagesToFilter[il].getSubject().toLowerCase().contains("mira")) {
                    System.out.println("Mail Text:- " + getTextFromMessage(messagesToFilter[il]));
                    System.out.println("That was a Mira Message");
                } else {
                    try {
                        System.out.println("Mail Text:- " + getTextFromMessage(messagesToFilter[il]));
                        System.out.println("Not sure what that shit is");
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }

        }
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            } else {
                result = result + " COULD NOT PROPERLY PARSE...\n";
            }
        }
        return result;
    }

}
