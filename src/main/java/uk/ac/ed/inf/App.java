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
    public static void main(String[] args) throws IOException {
        //validating args; mainly concerned with the formatting of the date with some limiting for
        //a date actually existing (valid month). doesn't account for putting in the 31st date of
        //a month with only 30, this will get picked up by the URL not existing later on
        String date = args[0];
        String site = args[1];

        if(!date.matches("^[0-9]{4}-(1[0-2]|0[1-9])-(0[1-9]|[12][0-9]|3[01])$"))
        {
            System.err.println("Error: The first argument must be a date in YYYY-mm-dd format.");
            System.exit(1);
        }
        try
        {
            new URL(site);
        }
        catch(MalformedURLException e)
        {
            System.err.println("Error: The second argument must be a valid URL.");
            System.exit(1);
        }
        if(args.length != 2)
        {
            System.err.println("Error: Please provide only a YYYY-mm-dd date and a URl as arguments.");
            System.exit(1);
        }

        //pulls needed data from the rest server (orders, restaurants, no-fly zones,
        //central region location

        List<Order> orders = jsonParse.parseOrder(site, date);
        List<Restaurant> restaurants = jsonParse.parseRestaurant(site);
        List<NamedRegion> blockedRegions = jsonParse.parseNoFly(site);
        LngLat APPLETON = new LngLat(-3.1870091,55.9443771);
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
        List<LngLat> flightPath = new ArrayList<>();

        for(Order order : validOrders)
        {
            //flight to restaurant
            Restaurant orderRestaurant = validator.getRestaurant(order, restaurantsArr);
            flightPath = router.aStarSearch(APPLETON, orderRestaurant.location(), blockedRegions);
            if(flightPath != null)
            {
                flightPaths.add(flightCount, flightPath);
                flightCount += 1;
            }
            //flight back from restaurant. may need to actually route with a*
            //instead of just reversing the forward path - come back to this
            //flightPath = router.aStarSearch(orderRestaurant.location(), APPLETON, blockedRegions);
            if(flightPath != null)
            {
                Collections.reverse(flightPaths);
                flightPaths.add(flightCount, flightPath);
                order.setOrderStatus(OrderStatus.DELIVERED);
                flightCount += 1;
            }
        }

        //writes flightpaths to geojson

        Gson gson = new Gson();
        JsonObject geoJson = new JsonObject();
        geoJson.addProperty("type", "FeatureCollection");
        JsonArray featuresArray = new JsonArray();

        for (List<LngLat> path : flightPaths)
        {
            //create a Feature object for each flightpath
            JsonObject feature = new JsonObject();
            feature.addProperty("type", "Feature");

            JsonObject geometry = new JsonObject();
            geometry.addProperty("type", "LineString");

            JsonArray coordinatesArray = new JsonArray();

            for (LngLat lngLat : path)
            {
                JsonArray point = new JsonArray();
                point.add(lngLat.lng());
                point.add(lngLat.lat());
                coordinatesArray.add(point);
            }
            geometry.add("coordinates", coordinatesArray);
            feature.add("geometry", geometry);

            feature.add("properties", new JsonObject());

            featuresArray.add(feature);
        }

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
