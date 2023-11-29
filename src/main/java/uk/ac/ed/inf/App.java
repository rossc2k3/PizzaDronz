package uk.ac.ed.inf;

import org.javatuples.Pair;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.google.gson.*;

public class App 
{
    public static void main(String[] args) throws IOException {

        /**
         * Critique
         * - No packages - suggested
         * - Wasted
         * - Inconistent naming conventions = capitaise
         *  - Writer
         *  - aStar
         *  - FlightPath
         *  - Rename FlightPathCreator
         *  - INVALID Method in aStar
         *  HELL NO BIG BOO BOO NO NO NO DONT CALL A* FOR EVERY ORDER
         * jsonParse
         * G
         */
        ArgsValidator.argsValidate(args);
        String date = args[0];
        String site = args[1];

        //pulls needed data from the rest server (orders, restaurants, no-fly zones,
        //central region location

        List<Order> orders = jsonParse.parseOrder(site, date);
        List<Restaurant> restaurants = jsonParse.parseRestaurant(site);
        List<NamedRegion> blockedRegions = jsonParse.parseNoFly(site);
        NamedRegion central = jsonParse.parseCentralRegion(site);

        //Fix name
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

        /**
         * Go find a better way to do
         *
         */
        jsonStringDeliveries = jsonStringDeliveries.replaceAll("},", "},\n");

        //flightpath serialiser

        Gson flightGson = new GsonBuilder().registerTypeAdapter(FlightPath.class,
                new FlightTypeAdapter()).create();
        String jsonStringFlightPath = flightGson.toJson(fullPath);

        //no
        jsonStringFlightPath = jsonStringFlightPath.replaceAll("},", "},\n");

      //  writer mywriter = new writer(date);


        //file creation
        writer.fileWriter("drone", date, geoJsonStringDrone);
        writer.fileWriter("deliveries", date, jsonStringDeliveries);
        writer.fileWriter("flightpath", date, jsonStringFlightPath);
    }
}
