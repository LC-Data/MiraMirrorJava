import java.net.URLConnection;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.net.URL;
import java.io.BufferedInputStream;  
import java.io.DataInputStream; 
import java.io.IOException;
import java.util.Scanner;


public class APIrequest {

	public static void main(String[] args) {
		
		try {

			URLConnection connection = new URL("https://google.com").openConnection();
			connection.setRequestProperty("Accept-Charset", "UTF-8");
			InputStream response = connection.getInputStream();
			//System.out.println(response.toString()); 			//This needs to be decoded

			try (Scanner scanner = new Scanner(response)) {
			    String responseBody = scanner.useDelimiter("\\A").next();
			    System.out.println(responseBody);
			}

		} catch(Exception e) {
			System.out.println(e);

			System.out.println("We have problems");
		}
	}

}
