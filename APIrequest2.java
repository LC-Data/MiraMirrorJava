import java.net.URLConnection;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
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

	String[] the_weather = new String[4];

	public static void main(String[] args) {

		APIrequest2 getNews = new APIrequest2();
		getNews.makeRequest();
		//return this.topNews;
	}
	
	public void makeRequest(){

		try {

			URLConnection connection = new URL("https://api.openweathermap.org/data/2.5/weather?id=6094817&appid=ce1444209a162927bb8f5819f2112188&units=metric").openConnection();
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream response = connection.getInputStream();
			//System.out.println(response.toString()); 			//This needs to be decoded

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
		

}
