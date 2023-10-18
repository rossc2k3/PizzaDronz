package uk.ac.ed.inf;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public class jsonParse
{
    public static List<Order> parseOrder()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Order> parsedOrders = null;

        try
        {
            String jsonOrders = accessRest.accessURL(new URL("https://ilp-rest.azurewebsites.net/orders"));
            parsedOrders = mapper.readValue(jsonOrders, mapper.getTypeFactory().constructCollectionType(List.class, Order.class));
            //parsedOrders = mapper.readValue(jsonOrders, new TypeReference<List<Order>>() {});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return parsedOrders;
    }

    public static List<Restaurant> parseRestaurant()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        List<Restaurant> parsedRestaurants = null;

        try
        {
            String jsonRestaurants = accessRest.accessURL(new URL("https://ilp-rest.azurewebsites.net/restaurants"));
            parsedRestaurants = mapper.readValue(jsonRestaurants, new TypeReference<List<Restaurant>>() {});

        } catch (IOException e) {
            e.printStackTrace();
        }

        return parsedRestaurants;
    }
}
