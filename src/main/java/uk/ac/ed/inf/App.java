package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main(String[] args) throws IOException {
        //validating args; mainly concerned with the formatting of the date with some limiting for
        //a date actually existing (valid month). doesn't account for putting in the 31st date of
        //a month with only 30, this will get picked up by the URL not existing later on
        String date = args[0];
        String site = args[1];
        if(!date.matches("^[0-9]{4}-([1][0-2]|[0][1-9])-(0[1-9]|[12][0-9]|3[01])$"))
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

        List<Order> orders = jsonParse.parseOrder(site, date);
        List<Restaurant> restaurants = jsonParse.parseRestaurant(site);
        Restaurant[] restaurantsArr = restaurants.toArray(new Restaurant[0]);

        List<Order> validOrders = orders.stream()
                        .filter(order -> new OrderValidator().validateOrder(order, restaurantsArr).getOrderStatus() !=
                                OrderStatus.VALID_BUT_NOT_DELIVERED)
                                .toList();

        System.out.println( "Hello World!" );
    }
}
