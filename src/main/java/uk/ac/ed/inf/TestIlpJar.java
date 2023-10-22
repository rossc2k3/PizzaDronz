package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

public class TestIlpJar
{
    public static void main(String[] args)
    {
        //validating args; mainly concerned with the formatting of the date with some limiting for
        //a date actually existing (valid month). doesn't account for putting in the 31st date of
        //a month with only 30, this will get picked up by the URL not existing later on
        if(!args[0].matches("^[0-9]{4}-([1][0-2]|[0][1-9])-(0[1-9]|[12][0-9]|3[01])$"))
        {
            System.err.println("Error: The first argument must be a date in YYYY-mm-dd format.");
            System.exit(1);
        }
        try
        {
            new URL(args[1]);
        }
        catch(MalformedURLException e)
        {
            System.err.println("Error: The second argument must be a valid URL.");
            System.exit(1);
        }
        if(args.length != 2)
        {
            System.err.println("Error: Please provide a YYYY-mm-dd date and a URl as arguments.");
            System.exit(1);
        }



        System.out.println("ILP Test Application using the IlpDataObjects.jar file");

        var order = new Order();
        order.setOrderNo(String.format("%08X", ThreadLocalRandom.current().nextInt(1, Integer.MAX_VALUE)));
        order.setOrderDate(LocalDate.of(2023, 9, 1));

        order.setCreditCardInformation
                (
                new CreditCardInformation
                        (
                        "1111111111111111",
                        String.format("%02d/%02d", ThreadLocalRandom.current().nextInt(1, 12),
                                ThreadLocalRandom.current().nextInt(43, 44)),
                        "991"
                        )
                );

        // every order has the defined outcome
        order.setOrderStatus(OrderStatus.UNDEFINED);
        order.setOrderValidationCode(OrderValidationCode.UNDEFINED);

        // get a random restaurant

        // and load the order items plus the price

        order.setPizzasInOrder
                (
                        new Pizza[]
                        {
                                new Pizza("A", 1111),
                                new Pizza("A", 1111)
                        }

                );

        order.setPriceTotalInPence(2222 + SystemConstants.ORDER_CHARGE_IN_PENCE);

        var restaurants = new Restaurant[]
                {
                        new Restaurant("myRestaurant",
                                new LngLat(55.945535152517735, -3.1912869215011597),
                                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                                new Pizza[]{new Pizza("A", 1111)}),

                        new Restaurant("restaurantTest",
                                new LngLat(55, -3),
                                new DayOfWeek[]{DayOfWeek.MONDAY, DayOfWeek.FRIDAY},
                                new Pizza[]{new Pizza("B", 1111)})


                };

        var validatedOrder =
                new OrderValidator().validateOrder(order,
                                restaurants);
        System.out.println("order validation resulted in status: " + validatedOrder.getOrderStatus() +
                " and validation code: " + validatedOrder.getOrderValidationCode());
    }
}
