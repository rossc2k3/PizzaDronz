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
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class OrderValidator implements OrderValidation
{


    /**
     * will likely be refactored into seperate functions for cw2.
     *
     * checks that the order fulfills the following parameters:
     * card number is valid (16 digits numerical)
     * cvv is valid (3 digits numerical)
     * expiry date is valid (in date, formatted as MM/yy)
     * correct price total, including delivery charge
     * pizza is defined in a restaurant
     * order doesn't exceed max pizza count (given in system constants)
     * order is only from one restaurant
     * restaurant is open
     * @param orderToValidate       the order we want to check is valid
     * @param definedRestaurants    list of restaurants to check the pizzas against
     * @return                      the order. the function will also set its status (valid, invalid) and provide an
     *                              error code where applicable
     */

    public boolean cardNumValid(Order order)
    {
        if(!order.getCreditCardInformation().getCreditCardNumber().matches("^[0-9]{16}$"))
        {
            return false;
        }
        return true;
    }

    public boolean cardCvvValid(Order order)
    {
        if(!order.getCreditCardInformation().getCvv().matches("^[0-9]{3}$"))
        {
            return false;
        }
        return true;
    }

    public boolean cardDateValid(Order order)
    {
        DateFormat dateformat = new SimpleDateFormat("MM/yy");
        String expiry = order.getCreditCardInformation().getCreditCardExpiry();
        Date formattedExpiry;
        try
        {
            formattedExpiry = dateformat.parse(expiry);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }

        //need to format to LocalDate to be able to compare to "current date" (order date).
        LocalDate formattedExpiryLocal = formattedExpiry.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate currentDate = order.getOrderDate();

        if(!order.getCreditCardInformation().getCreditCardExpiry().matches("^([1][0-2]|[0][1-9])/([0-9][0-9])$")
                || formattedExpiryLocal.isBefore(currentDate))
        {
            return false;
        }
        return true;
    }

    public boolean pizzasInRange(Order order)
    {
        if(!(order.getPizzasInOrder().length <= SystemConstants.MAX_PIZZAS_PER_ORDER))
        {
            return false;
        }
        return true;
    }

    public boolean correctTotal(Order order, Restaurant[] restaurants)
    {
        int totalPrice = Arrays.stream(order.getPizzasInOrder()).mapToInt(Pizza::priceInPence).sum();

    }

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants)
    {

        if(!cardNumValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
        }
        if(!cardCvvValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
        }
        if(!cardDateValid(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
        }
        if(!pizzasInRange(orderToValidate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        }


        Boolean[] pizzaFound = new Boolean[orderToValidate.getPizzasInOrder().length];
        Arrays.fill(pizzaFound, false);
        int firstPizzaIn = 0;
        int currentPizzaIn = 0;
        boolean restaurantOpen = true;
        boolean totalIsCorrect = true;
        int orderTotal = 0;


        // checks if all pizzas are valid, checks if pizza from more than one restaurant, checks if restaurant is open

        for(int z = 0; z < orderToValidate.getPizzasInOrder().length && restaurantOpen && totalIsCorrect &&
                orderToValidate.getOrderValidationCode() == OrderValidationCode.UNDEFINED; z++)
        {
            for (int x = 0; !pizzaFound[z] && x < definedRestaurants.length &&
                    orderToValidate.getOrderValidationCode() == OrderValidationCode.UNDEFINED; x++)
            {
                for (int y = 0; y < definedRestaurants[x].menu().length; y++)
                {

                    if (orderToValidate.getPizzasInOrder()[z].name().equals(definedRestaurants[x].menu()[y].name()))
                    {


                        if(orderToValidate.getPizzasInOrder()[z].priceInPence() !=
                                definedRestaurants[x].menu()[y].priceInPence())
                        {
                                orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
                                totalIsCorrect = false;
                                break;
                        }
                        else
                        {
                                orderTotal += orderToValidate.getPizzasInOrder()[z].priceInPence();
                        }
                        List<DayOfWeek> RestaurantDays = Arrays.stream(definedRestaurants[x].openingDays()).toList();
                        if(!RestaurantDays.contains(orderToValidate.getOrderDate().getDayOfWeek()))
                        {
                            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                            restaurantOpen = false;
                            break;
                        }

                        if(z == 0)
                        {
                            firstPizzaIn = x;
                        }
                        currentPizzaIn = x;
                        pizzaFound[z] = true;
                        break;
                    }
                }
            }
            if(!pizzaFound[z])
            {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
                break;
            }
            if (currentPizzaIn != firstPizzaIn)
            {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                break;
            }

        }
        if(orderToValidate.getPriceTotalInPence() != orderTotal + SystemConstants.ORDER_CHARGE_IN_PENCE)
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);
        }

        //if, by this point, there have been no changed to order code (no errors), set to no error code.
        //set status to valid. if not, set order status to invalid.

        if(orderToValidate.getOrderValidationCode().equals(OrderValidationCode.UNDEFINED))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);
        }
        else
        {
            orderToValidate.setOrderStatus(OrderStatus.INVALID);
        }
        return orderToValidate;
    }


}


