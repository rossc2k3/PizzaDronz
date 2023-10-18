package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


public class OrderValidator implements OrderValidation
{


    /**
     * checks that the card number provided is *strictly* 16 digit numeric.
     * @param order the order to be checked
     * @return true if 16 digit numeric, false otherwise
     */
    private boolean cardNumValid(Order order)
    {
        if(order.getCreditCardInformation().getCreditCardNumber() == null)
        {
            return false;
        }
        return order.getCreditCardInformation().getCreditCardNumber().matches("^[0-9]{16}$");
    }

    /**
     * checks that the cvv provided is *strictly* 3 digit numeric.
     * @param order the order to be checked
     * @return true if 3 digit numeric, false otherwise
     */
    private boolean cardCvvValid(Order order)
    {
        if(order.getCreditCardInformation().getCvv() == null)
        {
            return false;
        }
        return order.getCreditCardInformation().getCvv().matches("^[0-9]{3}$");
    }

    /**
     * checks that:
     *      the date given is in format mm/yy.
     *      the month is between 01 and 12, and the year is between 00 and 99.
     *      the expiry is after the current date.
     * @param order the order to be checked
     * @return true if abides by above params, false otherwise
     */

    private boolean cardDateValid(Order order)
    {
        if(order.getCreditCardInformation().getCreditCardExpiry() == null)
        {
            return false;
        }
        DateFormat dateformat = new SimpleDateFormat("MM/yy");
        String expiry = order.getCreditCardInformation().getCreditCardExpiry();
        Date formattedExpiry;
        try
        {
            formattedExpiry = dateformat.parse(expiry);
        }
        catch (ParseException e)
        {
            return false;
        }

        //need to format to LocalDate to be able to compare to "current date" (order date).
        LocalDate formattedExpiryLocal = formattedExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = order.getOrderDate();

        return order.getCreditCardInformation().getCreditCardExpiry().matches("^([1][0-2]|[0][1-9])/([0-9][0-9])$")
                && !formattedExpiryLocal.isBefore(currentDate);
    }

    /**
     * checks that there is atleast a pizza in the order and no more than the system max.
     * @param order the order to be checked
     * @return true is pizza in range, false otherwise
     */

    private boolean pizzasInRange(Order order)
    {
        return order.getPizzasInOrder().length <= SystemConstants.MAX_PIZZAS_PER_ORDER && order.getPizzasInOrder().length >= 1;
    }

    /**
     * checks that all the pizzas given in the order have valid names, i.e. can be found in a menu in a restaurant.
     * @param order the order containing the pizzas to be checked
     * @param restaurants the list of restaurants to look for the pizzas in
     * @return true if all pizzas are found, false otherwise.
     */

    private boolean pizzasExist(Order order, Restaurant[] restaurants)
    {
        Set<String> pizzaNames = Arrays.stream(order.getPizzasInOrder()).map(Pizza::name).collect(Collectors.toSet());
        Set<String> restaurantPizzas = Arrays.stream(restaurants).flatMap(restaurant-> Arrays.stream(restaurant.menu()))
                .map(Pizza::name).collect(Collectors.toSet());
        return restaurantPizzas.containsAll(pizzaNames);
    }


    /**
     * checks that all the pizzas are from the same restaurant. assumes all pizzas exist.
     * @param order the order containing the pizzas to be checked
     * @param restaurants the list of restaurants to look for the pizzas in.
     * @return true if all pizzas match the location given in the first index, false otherwise.
     */
    private boolean sameRestaurant(Order order, Restaurant[] restaurants)
    {

        int[] pizzaLocations = new int[order.getPizzasInOrder().length];
        Arrays.fill(pizzaLocations, 0);
        for(int z = 0; z < order.getPizzasInOrder().length; z++)
        {
            for (int x = 0; x < restaurants.length; x++)
            {
                for (int y = 0; y < restaurants[x].menu().length; y++)
                {

                    if (order.getPizzasInOrder()[z].name().equals(restaurants[x].menu()[y].name()))
                    {
                        pizzaLocations[z] = x;
                        break;
                    }
                }
            }
        }
        return Arrays.stream(pizzaLocations).allMatch(num -> num == pizzaLocations[0]);
    }

    /**
     * checks for each pizza that the price of the pizza in the order is the same as given in the restaurant.
     * robust in the sense that it checks the price of each pizza individually as opposed to just checking the total
     * price matches
     * @param order the order in which the price is to be checked
     * @param restaurants the list of restaurants to look for the pizzas in.
     * @return true if all pizzas have matching price (and total == the total price of all pizzas + delivery charge),
     * false otherwise
     */

    private boolean correctTotal(Order order, Restaurant[] restaurants)
    {
        int orderTotal = Arrays.stream(order.getPizzasInOrder())
                .flatMap(orderPizza ->
                        Arrays.stream(restaurants)
                                .flatMap(restaurant -> Arrays.stream(restaurant.menu())
                                        .filter(menuPizza -> orderPizza.name().equals(menuPizza.name())
                                                && orderPizza.priceInPence() == menuPizza.priceInPence())))
                .mapToInt(Pizza::priceInPence)
                .sum();

        return order.getPriceTotalInPence() == orderTotal + SystemConstants.ORDER_CHARGE_IN_PENCE;

    }


    /**
     * checks that the restaurant is open. makes the assumption that the pizzas are all coming from the same
     * restaurant - not necessarily as robust as it could be but works for our purposes
     * @param order the order to be checked
     * @param restaurants the list of restaurants to look for the pizzas in.
     * @return true if restaurant is open on given order date, false otherwise
     */
    private boolean restaurantOpen(Order order, Restaurant[] restaurants)
    {
        // we can assume all pizzas come from the same restaurant, so we only have to consider the first one of any
        // given order
        DayOfWeek orderDate = order.getOrderDate().getDayOfWeek();
        return Arrays.stream(restaurants)
                .filter(restaurant -> Arrays.stream(restaurant.menu())
                        .anyMatch(menuPizza -> menuPizza.name().equals(order.getPizzasInOrder()[0].name())))
                .anyMatch(restaurant -> Arrays.asList(restaurant.openingDays()).contains(orderDate));
    }

    /**
     * performs all checks as written above:
     *  cardNumValid
     *  cardCvvValid
     *  cardDateValid
     *  pizzasInRange
     *  pizzasExist
     *  correctTotal
     *  sameRestaurant
     *  restaurantOpen
     * if all checks passed, set order status to valid but not delivered and
     * set order validation code as no error. if invalid, set to invalid and corresponding error code.
     * @param orderToValidate    the order to perform validation on
     * @param definedRestaurants the restaurants passed in from the rest service
     * @return the order, regardless of validity
     */
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants)
    {

        if(!cardNumValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!cardCvvValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!cardDateValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!pizzasInRange(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!pizzasExist(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!correctTotal(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!sameRestaurant(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }
        if(!restaurantOpen(orderToValidate, definedRestaurants))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
            return orderToValidate;
        }

        // if all checks are passed, we return the order as valid

        orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        return orderToValidate;
    }


}


