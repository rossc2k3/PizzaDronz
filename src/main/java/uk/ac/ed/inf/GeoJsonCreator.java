package uk.ac.ed.inf;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.util.List;

public class GeoJsonCreator
{
    public static String geoCreate(List<List<LngLat>> paths)
    {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "FeatureCollection");

        JsonArray featuresArray = new JsonArray();

        //create a Feature object for each flightpath
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "Feature");

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type", "LineString");

        JsonArray coordinatesArray = new JsonArray();

        for (List<LngLat> path : paths)
        {
            for (LngLat lngLat : path)
            {
                JsonArray point = new JsonArray();
                point.add(lngLat.lng());
                point.add(lngLat.lat());
                coordinatesArray.add(point);
            }
        }
        geometry.add("coordinates", coordinatesArray);
        feature.add("properties", new JsonObject());
        feature.add("geometry", geometry);
        geoJson.add("features", featuresArray);

        featuresArray.add(feature);
        geoJson.add("features", featuresArray);

        return gson.toJson(geoJson);
    }
}
