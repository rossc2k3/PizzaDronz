package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.*;

public class App 
{
    static final LngLat APPLETON = new LngLat(-3.1870091,55.9443771);
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
        aStar2 router = new aStar2();
        List<LngLat> flightPath;
        List<LngLat> reverse;

        for(Order order : validOrders)
        {
            //flight to restaurant
            Restaurant orderRestaurant = validator.getRestaurant(order, restaurantsArr);
            flightPath = router.aStarRouter(APPLETON, orderRestaurant.location(), blockedRegions);
            if(flightPath != null)
            {
                flightPaths.add(flightPath);

                //now do the reverse flight
                reverse = new ArrayList<>(flightPath);
                Collections.reverse(reverse);
                flightPaths.add(reverse);
            }
        }

        //writes flightpaths to geojson

        String geoJsonStringDrone = GeoJsonCreator.geoCreate(flightPaths);

        //order serialiser

        Gson orderGson = new GsonBuilder().registerTypeAdapter(Order.class,
                new OrderTypeAdapter()).create();

        String jsonStringDeliveries = orderGson.toJson(validOrders);
        jsonStringDeliveries = jsonStringDeliveries.replaceAll("},", "},\n");

        //file creation
        writer.fileWriter("drone", date, geoJsonStringDrone);
        writer.fileWriter("deliveries", date, jsonStringDeliveries);
        //writer.fileWriter("flightpath", date, jsonStringFlightPath
    }
}
