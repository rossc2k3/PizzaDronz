package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class OrderValidator implements OrderValidation
{

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants)
    {
        //gets date in correct formatting
        DateFormat dateformat = new SimpleDateFormat("MM/yy");
        String expiry = orderToValidate.getCreditCardInformation().getCreditCardExpiry();
        Date formattedExpiry;
        try
        {
            formattedExpiry = dateformat.parse(expiry);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        Date currentDate = new Date();


        if(!orderToValidate.getCreditCardInformation().getCreditCardNumber().matches("^[0-9]{16}$"))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
        }
        if(!orderToValidate.getCreditCardInformation().getCvv().matches("^[0-9]{3}$"))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
        }
        //change YearMonth.now to order date pls :3
        if(!orderToValidate.getCreditCardInformation().getCreditCardExpiry().matches("([1][0-2]|[0][1-9])/([0-9][0-9])") || formattedExpiry.before(currentDate))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);
        }
        if(!((orderToValidate.getPizzasInOrder().length <= SystemConstants.MAX_PIZZAS_PER_ORDER)))
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

        for(int z = 0; z < orderToValidate.getPizzasInOrder().length && restaurantOpen && totalIsCorrect; z++)
        {
            for (int x = 0; !pizzaFound[z] && x < definedRestaurants.length; x++)
            {
                for (int y = 0; y < definedRestaurants[x].menu().length; y++)
                {

                    if (orderToValidate.getPizzasInOrder()[z].name().equals(definedRestaurants[x].menu()[y].name()))
                    {


                        if(orderToValidate.getPizzasInOrder()[z].priceInPence() != definedRestaurants[x].menu()[y].priceInPence())
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


