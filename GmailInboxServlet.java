import java.io.*;
import java.io.IOException;
import java.io.DataInputStream; 
import java.io.BufferedInputStream;  
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.lang.reflect.Type;
import java.lang.String.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;



public class GmailInboxServlet extends HttpServlet {

    String[] topNews = new String[5];
    String[] the_weather = new String[4];

    public void init() throws ServletException {
      // Do required initialization
        //APIrequest2 getNews = new APIrequest2();
        getNews();
        getWeather();

    }


    public void getWeather() {

        try {

            URLConnection connection = new URL("https://api.openweathermap.org/data/2.5/weather?id=6094817&appid=ce1444209a162927bb8f5819f2112188&units=metric").openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();
            //System.out.println(response.toString());          //This needs to be decoded

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                //System.out.println(responseBody);
                JsonObject convertedObject = new Gson().fromJson(responseBody, JsonObject.class);
                System.out.println(convertedObject);
                System.out.println("\n\n\n\n\n\n");

                String responseAsString = convertedObject.toString();

                String nameAsString = responseAsString.split("name")[1].substring(3).split("\"")[0];//This gets the name
                System.out.println(nameAsString);
                this.the_weather[0] = nameAsString;

                String weatherAsString = responseAsString.split("description")[1].substring(3).split("\"")[0];//This gets the weather "scattered clouds, etc"
                System.out.println(weatherAsString);
                this.the_weather[1] = weatherAsString;

                //String tempAsString = responseAsString.split("temp")[1].substring(2).split(",")[0];//This gets the current temperature
                //tempAsString = tempAsString + " C";
                //System.out.println(tempAsString);

                String feels_likeAsString = responseAsString.split("feels_like")[1].substring(2).split(",")[0];//This gets the current temperature
                feels_likeAsString = "Feels like: " + feels_likeAsString + " C";
                System.out.println(feels_likeAsString);
                this.the_weather[2] = feels_likeAsString;

                String windAsString = responseAsString.split("speed")[1].substring(2).split(",")[0];//This gets the current temperature
                windAsString = "Wind: " + windAsString + " km/h";
                System.out.println(windAsString);
                this.the_weather[3] = windAsString;
                
                //return this.topNews;
            }

        } catch(Exception e) {
            System.out.println(e);

            System.out.println("We have problems");
        }

        //System.out.println(this.the_weather[0]);
        //return this.the_weather;
    }



    public void getNews(){

        try {

            URLConnection connection = new URL("http://newsapi.org/v2/top-headlines?country=ca&apiKey=a7a1a6a0dbe94b1784fd741c57ff0196").openConnection();
            connection.setRequestProperty("Accept-Charset", "UTF-8");
            InputStream response = connection.getInputStream();
            //System.out.println(response.toString());          //This needs to be decoded

            try (Scanner scanner = new Scanner(response)) {
                String responseBody = scanner.useDelimiter("\\A").next();
                //System.out.println(responseBody);
                JsonObject convertedObject = new Gson().fromJson(responseBody, JsonObject.class);
                //System.out.println(convertedObject);
                System.out.println("\n\n\n\n\n\n");
                //System.out.println(convertedObject.get("articles"));
                JsonArray jsonObjList = convertedObject.getAsJsonArray("articles");

                String newsResponse = jsonObjList.toString();

                System.out.println(newsResponse);

                String[] sourceList = newsResponse.split("source"); //This will create a String Array that broke the String at each occurence of "source". This len is how many articles were returned.
                //System.out.println(sourceList.length);
                //System.out.println(sourceList[1]);
                String firstSourceTitle = sourceList[1];    //This is the first source onward, since the split includes the pre-cursor to the split in element 0
                // Should probably push these in to an array or something - the top 5 stories or however many
                //String[] topNews = new String[5];
                for (int x = 1; x < 6; x++) {       
                    String story = sourceList[x];
                    story = story.substring(3).split("title")[1];   //This series of splits and trims captures the Title from the API call
                    story = story.substring(3).split("description")[0];
                    story = story.substring(0,story.length() -3);
                    System.out.println(story + "\n" + " " + x);
                    this.topNews[x - 1] = story;
                }
                System.out.println(Arrays.toString(this.topNews));
                
                //return this.topNews;
            }

        } catch(Exception e) {
            System.out.println(e);

            System.out.println("We have problems with the news");
        }

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
            out.println("<h2>" + this.topNews[0] + "</h2><br>");
            out.println("<h2>" + this.topNews[1] + "</h2><br>");
            out.println("<h2>" + this.topNews[2] + "</h2><br>");
            out.println("<h2>" + this.topNews[3] + "</h2><br>");
            if (this.topNews[4] != null ){
                out.println("<h2>" + this.topNews[4] + "</h2><br>");
            }
            
            out.println("<h2>" + this.the_weather[0] + "</h2><br>");
            out.println("<h2>" + this.the_weather[1] + "</h2><br>");
            out.println("<h2>" + this.the_weather[2] + "</h2><br>");
            out.println("<h2>" + this.the_weather[3] + "</h2><br>");
            //out.println("<h2>" + this.topNews[4] + "</h2><br>");
            
            out.println("<div id='news' style='display:inline-block;background: white;color: pink;border-radius: 7px;border: solid 2px grey;padding: 10px;width: 20%;'>");
            out.println("<p>" + this.topNews[0] + "</p><br>");
            out.println("<p>" + this.topNews[1] + "</p><br>");
            out.println("<p>" + this.topNews[2] + "</p><br>");
            out.println("<p>" + this.topNews[3] + "</p><br>");
            out.println("<p>" + this.topNews[4] + "</p><br>");
            out.println("</div>");

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
