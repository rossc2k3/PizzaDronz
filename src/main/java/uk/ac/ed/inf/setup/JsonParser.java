package uk.ac.ed.inf.setup;


import com.google.gson.*;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class JsonParser
{
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateDeserializer()).create();

    public static List<Order> parseOrder(String url, String date) throws IOException
    {
        String jsonOrders = AccessRest.accessURL(new URL(url + "/orders/" + date));
        Type listOrder = new TypeToken<ArrayList<Order>>(){}.getType();
        List<Order> parsedOrders = gson.fromJson(jsonOrders, listOrder);
        if(parsedOrders.isEmpty())
        {
            //will happen if date given has no orders
            System.err.println("There are no orders found in this list!");
            System.exit(1);
        }
        return parsedOrders;
    }

    public static List<Restaurant> parseRestaurant(String url) throws IOException
    {
        String jsonRestaurants = AccessRest.accessURL(new URL(url + "/restaurants"));
        Type listRestaurant = new TypeToken<ArrayList<Restaurant>>(){}.getType();
        return gson.fromJson(jsonRestaurants, listRestaurant);
    }

    public static NamedRegion parseCentralRegion(String url) throws IOException
    {
        String jsonCentral = AccessRest.accessURL(new URL(url + "/centralArea"));
        Type centralRegion = new TypeToken<NamedRegion>(){}.getType();
        return gson.fromJson(jsonCentral, centralRegion);
    }

    public static List<NamedRegion> parseNoFly(String url) throws IOException
    {
        String jsonNoFly = AccessRest.accessURL(new URL(url + "/noFlyZones"));
        Type listNoFly = new TypeToken<ArrayList<NamedRegion>>(){}.getType();
        return gson.fromJson(jsonNoFly, listNoFly);
    }

}
