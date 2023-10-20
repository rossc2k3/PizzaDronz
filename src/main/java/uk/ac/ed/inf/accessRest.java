package uk.ac.ed.inf;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class accessRest
{
    public static String accessURL(URL url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        if (conn.getResponseCode() != 200)
        {
            throw new RuntimeException("HttpResponseCode: " + conn.getResponseCode());
        }
        else
        {
            String inline = "";
            Scanner scanner = new Scanner(url.openStream());

            //Write all the JSON data into a string using a scanner
            while (scanner.hasNext())
            {
                inline += scanner.nextLine();
            }

            //Close the scanner
            scanner.close();
            return inline;
        }
    }
}
