package uk.ac.ed.inf;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Scanner;

public class accessRest
{
    public static String accessURL(URL url) throws IOException
    {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try
        {
            conn.setRequestMethod("GET");
            conn.connect();
        }
        catch (UnknownHostException unknownURL)
        {
            System.err.println("Unknown URL, please check and try again.");
            System.exit(1);
        }
        int connCode = conn.getResponseCode();
        if (connCode != 200)
        {
            System.err.println("Error code" + connCode + ", please check URL and try again.");
            System.exit(1);
            throw new RuntimeException("HttpResponseCode: " + connCode);
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
