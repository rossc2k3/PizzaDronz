package uk.ac.ed.inf;

import org.javatuples.Pair;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.util.List;

import com.google.gson.*;

public class App 
{
    public static void main(String[] args) throws IOException {

        ArgsValidator.argsValidate(args);
        String date = args[0];
        String site = args[1];

        //pulls needed data from the rest server (orders, restaurants, no-fly zones,
        //central region location

        List<Order> orders = jsonParse.parseOrder(site, date);
        List<Restaurant> restaurants = jsonParse.parseRestaurant(site);
        List<NamedRegion> blockedRegions = jsonParse.parseNoFly(site);
        NamedRegion central = jsonParse.parseCentralRegion(site);
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

        Pair<List<List<LngLat>>, List<FlightPath>> pathData = FlightPathCreator.createFlightPath(
                validOrders, restaurantsArr, blockedRegions, central);

        List<List<LngLat>> flightPaths = pathData.getValue0();
        List<FlightPath> fullPath = pathData.getValue1();

        //writes flightpaths to geojson

        String geoJsonStringDrone = GeoJsonCreator.geoCreate(flightPaths);

        //order serialiser

        Gson orderGson = new GsonBuilder().registerTypeAdapter(Order.class,
                new OrderTypeAdapter()).create();
        String jsonStringDeliveries = orderGson.toJson(validOrders);
        jsonStringDeliveries = jsonStringDeliveries.replaceAll("},", "},\n");

        //flightpath serialiser

        Gson flightGson = new GsonBuilder().registerTypeAdapter(FlightPath.class,
                new FlightTypeAdapter()).create();
        String jsonStringFlightPath = flightGson.toJson(fullPath);
        jsonStringFlightPath = jsonStringFlightPath.replaceAll("},", "},\n");


        //file creation
        writer.fileWriter("drone", date, geoJsonStringDrone);
        writer.fileWriter("deliveries", date, jsonStringDeliveries);
        writer.fileWriter("flightpath", date, jsonStringFlightPath);
    }
}
