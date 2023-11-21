package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;

public class App 
{
    static LngLat APPLETON = new LngLat(-3.1870091,55.9443771);
    public static void main(String[] args) throws IOException {

        ArgsValidator.argsValidate(args);
        String date = args[0];
        String site = args[1];

        //pulls needed data from the rest server (orders, restaurants, no-fly zones,
        //central region location

        List<Order> orders = jsonParse.parseOrder(site, date);
        List<Restaurant> restaurants = jsonParse.parseRestaurant(site);
        List<NamedRegion> blockedRegions = jsonParse.parseNoFly(site);
        Restaurant[] restaurantsArr = restaurants.toArray(new Restaurant[0]);

        //create order validator object, then validates orders

        OrderValidator validator = new OrderValidator();
        List<Order> validOrders = orders.stream()
                        .filter(order -> validator.validateOrder(order, restaurantsArr).getOrderStatus() ==
                                OrderStatus.VALID_BUT_NOT_DELIVERED)
                                .toList();

        /*
        creates object to store all flight paths, creates an a* router object,
        routes the path for all valid orders. if order was delivered, mark as delivered
        */

        List<List<LngLat>> flightPaths = new ArrayList<>();
        aStar router = new aStar();
        int flightCount = 0;
        List<LngLat> flightPath;
        List<LngLat> reverse;

        for(Order order : validOrders)
        {
            //flight to restaurant
            Restaurant orderRestaurant = validator.getRestaurant(order, restaurantsArr);
            flightPath = router.aStarSearch(APPLETON, orderRestaurant.location(), blockedRegions);
            if(flightPath != null)
            {
                flightPaths.add(flightCount, flightPath);
                flightCount += 1;

                //now do the reverse flight
                reverse = new ArrayList<>(flightPath);
                Collections.reverse(reverse);
                flightPaths.add(flightCount, reverse);
                flightCount += 1;

                System.out.println(reverse.equals(flightPath));
                System.out.println(flightPath);
                System.out.println(reverse);
            }
        }

        /*Restaurant orderRestaurant = validator.getRestaurant(validOrders.get(0), restaurantsArr);
        flightPath = router.aStarSearch(APPLETON, orderRestaurant.location(), blockedRegions);
        flightPaths.add(0, flightPath);
        reverse = new ArrayList<>(flightPath);
        Collections.reverse(reverse);
        flightPaths.add(1, reverse);*/


        //writes flightpaths to geojson

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

        for (List<LngLat> path : flightPaths)
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

        String geoJsonStringDrone = gson.toJson(geoJson);

        //order serialiser

        Gson orderGson = new GsonBuilder().registerTypeAdapter(Order.class,
                new OrderTypeAdapter()).create();

        String jsonStringDeliveries = orderGson.toJson(validOrders);
        jsonStringDeliveries = jsonStringDeliveries.replaceAll("},", "},\n");

        //file creation
        writer.fileWriter("drone", date, geoJsonStringDrone);
        writer.fileWriter("deliveries", date, jsonStringDeliveries);
        //writer.fileWriter("flightpath", date, jsonStringFlightPath

        System.out.println( "Hello World!" );
    }
}
