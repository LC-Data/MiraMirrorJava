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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.UnsupportedEncodingException;



public class GmailInboxServlet extends HttpServlet {

    public void init() throws ServletException {
      // Do required initialization
    }


    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      
      // Set response content type
            response.setContentType("text/html");
            response.setIntHeader("Refresh", 20);   //This forces the servlet page to refresh every 10 seconds

            PrintWriter out = response.getWriter();

      // Actual logic goes here.
            GmailInboxServlet gmail = new GmailInboxServlet();
            ArrayList<String> miraMessages = gmail.read();
            //out.println("<h1>" + this.message + "</h1>");
            //The following line of JavaScript implements a realtime clock with AM/PM string, and a trigger to refresh the page ever 60 seconds.
            out.println("<!DOCTYPE html><html><head><title>MiraMirror Java Edition v0.1</title><script>var ampm='';function startTime(){var e=new Date,D=e.getDate(),t=e.getHours(),a=e.getMinutes(),n=e.getSeconds(),c=e.getDay(),m=e.getFullYear(),u=e.getMonth();checkAMPM(),t=checkTime(t),a=checkTime(a),n=checkTime(n),0==c?c='Sunday':1==c?c='Monday':2==c?c='Tuesday':3==c?c='Wednesday':4==c?c='Thursday':5==c?c='Friday':6==c&&(c='Saturday'),0==u?u='Jan':1==u?u='Feb':2==u?u='Mar':3==u?u='Apr':4==u?u='May':5==u?u='Jun':6==u?u='Jul':7==u?u='Aug':8==u?u='Sept':9==u?u='Oct':10==u?u='Nov':11==u&&(u='Dec'),document.getElementById('clock').innerHTML=t+':'+a+':'+n+' '+ampm,document.getElementById('date').innerHTML=c+' '+u+' '+D+' '+m;setTimeout(startTime,500)}function checkTime(e){return e<10&&(e='0'+e),e}function checkAMPM(){var e=(new Date).getHours();e>11?ampm='PM':e<12&&(ampm='AM')}</script></head><body onload=\"startTime()\" style=\"text-align:center;background-color:black;color:white;\">");
            
            //out.println("<a class=\"weatherwidget-io\" href=\"https://forecast7.com/en/45d42n75d70/ottawa/\" data-label_1=\"OTTAWA\" data-label_2=\"Current Weather\" data-font=\"Ubuntu\" data-icons=\"Climacons Animated\" data-days=\"3\" data-theme=\"dark\" data-suncolor=\"#dfda2c\" data-raincolor=\"#93c8eb\" >OTTAWA Current Weather</a>");
            //out.println("<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src='https://weatherwidget.io/js/widget.min.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','weatherwidget-io-js');</script>");

            out.println("<div id=\"clock\"></div>");

            out.println("<div id=\"date\"></div>");
            //out.println("<h2>" + miraMessages + "</h2><br>");
            out.println("<h2>" + miraMessages.get(0) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(1) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(2) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(3) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(4) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(5) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(6) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(7) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(8) + "</h2><br>");
            out.println("<h2>" + miraMessages.get(9) + "</h2><br>");
            out.println("</body></html>");
                   
    }


    public ArrayList<String> read() {
        Properties props = new Properties();
        ArrayList<String> finalMessages = new ArrayList<String>();
        try {
            //props.load(new FileInputStream(new File("./smtp.properties")));
           Session session = Session.getDefaultInstance(props, null);

           Store store = session.getStore("imaps");
           store.connect("smtp.gmail.com", "16fontainemira@gmail.com","16rueFontaine");

           Folder inbox = store.getFolder("inbox");
           inbox.open(Folder.READ_ONLY);
           int messageCount = inbox.getMessageCount();

           System.out.println("Total Messages:- " + messageCount);

           Message[] messages = inbox.getMessages();

           for(int i=0; i<messages.length/2; i++){
                Message temp = messages[i];
                messages[i] = messages[messages.length -i -1];
                messages[messages.length -i -1] = temp;
            }


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
                    if (result.contains("<")|result.contains(">")|result.contains("alert")){    //This is shit, garbage, this should be a proper XSS regex sanitizer
                        result = sanitize(result);
                    }
                break; // without break same text appears twice in my tests
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
                if (result.contains("<")|result.contains(">")|result.contains("alert")){
                    result = sanitize(result);
                }

            } else {
                result = result + " COULD NOT PROPERLY PARSE...\n";
                result = sanitize(result);
            }
        }
        return result;
    }

    public static String sanitize(String string) {
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex.getCause());
        }
    }


    public void destroy() {
        // do nothing.
    }

}
