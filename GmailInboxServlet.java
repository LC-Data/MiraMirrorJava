import java.io.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.ArrayList;

public class GmailInboxServlet extends HttpServlet {

    public void init() throws ServletException {
      // Do required initialization
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      
      // Set response content type
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

      // Actual logic goes here.
            GmailInboxServlet gmail = new GmailInboxServlet();
            ArrayList<String> miraMessages = gmail.read();
            //out.println("<h1>" + this.message + "</h1>");
            out.println("<!DOCTYPE html><html><head><title>MiraMirror Java Edition v0.1</title></head><body>");
            out.println("<h2>" + miraMessages + "</h2>");
            out.println("</body></html>");
                   
    }


    public ArrayList<String> read() {
        Properties props = new Properties();
        ArrayList<String> finalMessages = new ArrayList<String>();
        try {
            //props.load(new FileInputStream(new File("./smtp.properties")));
           Session session = Session.getDefaultInstance(props, null);

           Store store = session.getStore("imaps");
           store.connect("smtp.gmail.com", "xxxxxxxxxxxxxxxxx","xxxxxxxxxxxx");

           Folder inbox = store.getFolder("inbox");
           inbox.open(Folder.READ_ONLY);
           int messageCount = inbox.getMessageCount();

           System.out.println("Total Messages:- " + messageCount);

           Message[] messages = inbox.getMessages();
           System.out.println("------------------------------");
           finalMessages = miraMessage(messages);
           inbox.close(true);
           store.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

	return finalMessages;
    }


    private ArrayList<String> miraMessage(Message[] messagesToFilter) {

        ArrayList<String> mira_messages_in_inbox = new ArrayList<String>();

        for (int il = 0; il < messagesToFilter.length; il++) {
            try {
                if (messagesToFilter[il].getSubject().toLowerCase().contains("mira")) {
                    System.out.println("Mail Text:- " + getTextFromMessage(messagesToFilter[il]));
                    System.out.println("That was a Mira Message");
                    mira_messages_in_inbox.add(getTextFromMessage(messagesToFilter[il]));
                    if (mira_messages_in_inbox.get(9) == null) {    //when the array is filled up (I'm sure there's a more memory-efficient way to achieve this)
                        mira_messages_in_inbox.add(getTextFromMessage(messagesToFilter[il]));
                    } else {
                        break;
                    }
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
	return mira_messages_in_inbox;
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


    public void destroy() {
        // do nothing.
    }

}
