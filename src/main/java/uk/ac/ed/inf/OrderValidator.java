package uk.ac.ed.inf;


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
        if(!((orderToValidate.getPizzasInOrder().length <= 4)))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);
        }


        Boolean[] pizzaFound = new Boolean[orderToValidate.getPizzasInOrder().length];
        Arrays.fill(pizzaFound, false);
        int firstPizzaIn = 0;
        int currentPizzaIn = 0;
        boolean restaurantOpen = true;

        // checks if all pizzas are valid, checks if pizza from more than one restaurant, checks if restaurant is open

        for(int z = 0; z < orderToValidate.getPizzasInOrder().length; z++)
        {
            for (int x = 0; !pizzaFound[z] && x < definedRestaurants.length; x++)
            {
                for (int y = 0; y < definedRestaurants[x].menu().length && restaurantOpen; y++)
                {

                    if (orderToValidate.getPizzasInOrder()[z].name().equals(definedRestaurants[x].menu()[y].name()))
                    {
                        if(y == 0)
                        {
                            List<DayOfWeek> RestaurantDays = Arrays.stream(definedRestaurants[x].openingDays()).toList();
                            if(!RestaurantDays.contains(orderToValidate.getOrderDate().getDayOfWeek()))
                            {
                                orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);
                                restaurantOpen = false;
                                break;
                            }
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

        if(orderToValidate.getOrderValidationCode().equals(OrderValidationCode.UNDEFINED))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        }
        return orderToValidate;
    }


}


