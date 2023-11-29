package uk.ac.ed.inf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

public class writer
{
    //private final LocalDateTime date;
  /*  public writer(LocalDateTime orderDate){
        date = orderDate;
    } */
    public static void fileWriter(String fileName, String date, String geoJsonToWrite)
    {
        String directory = "resultfiles";
        String file;
        if(Objects.equals(fileName, "drone"))
        {
            file = fileName + "-" + date + ".geojson";
        }
        else
        {
            file = fileName + "-" + date + ".json";
        }
        try
        {
            Files.createDirectories(Paths.get(directory));
            Path filePath = Paths.get(directory, file);
            Files.write(filePath, geoJsonToWrite.getBytes(), StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            System.out.println("File written successfully.");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
}
