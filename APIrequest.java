import java.net.URLConnection;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.io.BufferedInputStream;  
import java.io.DataInputStream; 
import java.io.IOException;
import java.util.Scanner;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.lang.String.*;
import com.google.gson.reflect.TypeToken;

public class APIrequest2 {

	public static void main(String[] args) {
		
		try {

			URLConnection connection = new URL("http://newsapi.org/v2/top-headlines?country=ca&apiKey=a7a1a6a0dbe94b1784fd741c57ff0196").openConnection();
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream response = connection.getInputStream();
			//System.out.println(response.toString()); 			//This needs to be decoded

			try (Scanner scanner = new Scanner(response)) {
			    String responseBody = scanner.useDelimiter("\\A").next();
			    //System.out.println(responseBody);
			    JsonObject convertedObject = new Gson().fromJson(responseBody, JsonObject.class);
			    //System.out.println(convertedObject);
			    System.out.println("\n\n\n\n\n\n");
			    //System.out.println(convertedObject.get("articles"));
			    JsonArray jsonObjList = convertedObject.getAsJsonArray("articles");

			    String ass = jsonObjList.toString();

			    System.out.println(ass);

			    String[] sourceList = ass.split("source");	//This will create a String Array that broke the String at each occurence of "source". This len is how many articles were returned.
			    //System.out.println(sourceList.length);
			    //System.out.println(sourceList[1]);
			    String firstSourceTitle = sourceList[1];	//This will create a String Array that broke the String at each occurence of "source". This len is how many articles were returned.
			    System.out.println(firstSourceTitle);
			    
			    firstSourceTitle = firstSourceTitle.substring(3).split("title")[1];	//This series of splits and trims captures the Title from the API call
			    firstSourceTitle = firstSourceTitle.substring(3).split("description")[0];
			    firstSourceTitle = firstSourceTitle.substring(0,firstSourceTitle.length() -3);
			    System.out.println(firstSourceTitle);
			    // Should probably push these in to an array or something - the top 5 stories or however many
			    
			    for (int x = 1; x < 6; x++) {		
			    	String story = sourceList[x];
			    	story = story.substring(3).split("title")[1];	//This series of splits and trims captures the Title from the API call
				    story = story.substring(3).split("description")[0];
				    story = story.substring(0,story.length() -3);
				    System.out.println(story + "\n");
			    }
			    
			}

		} catch(Exception e) {
			System.out.println(e);

			System.out.println("We have problems");
		}
	}

}
