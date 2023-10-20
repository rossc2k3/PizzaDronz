package uk.ac.ed.inf;


import com.google.gson.*;
import java.lang.reflect.Type;

import com.google.gson.reflect.TypeToken;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.gsonUtils.LocalDateDeserializer;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class jsonParse
{
     private Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateDeserializer())
            .create();

    public List<Order> parseOrder() throws IOException
    {

        String jsonOrders = accessRest.accessURL(new URL("https://ilp-rest.azurewebsites.net/orders"));
        Type listOrder = new TypeToken<ArrayList<Order>>(){}.getType();
        return gson.fromJson(jsonOrders, listOrder);
    }

    public List<Restaurant> parseRestaurant() throws IOException
    {
        String jsonRestaurants = accessRest.accessURL(new URL("https://ilp-rest.azurewebsites.net/restaurants"));
        Type listRestaurant = new TypeToken<ArrayList<Restaurant>>(){}.getType();
        return gson.fromJson(jsonRestaurants, listRestaurant);
    }
}
