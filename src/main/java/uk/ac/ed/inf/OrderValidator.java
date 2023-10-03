package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


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


        //checks if pizza from more than one restaurant.
        boolean fromOneRestaurant = true;
        Boolean[] pizzaFound = new Boolean[orderToValidate.getPizzasInOrder().length];
        int firstPizzaIn = 0;
        int currentPizzaIn = -1;
        // checks if all pizzas are valid, also checks and stores where the first pizzas is.
        for(int z = 0; z < orderToValidate.getPizzasInOrder().length; z++)
        {
            for (int x = 0; !pizzaFound[z] && x < definedRestaurants.length; x++)
            {
                for (int y = 0; y < definedRestaurants[x].menu().length; y++)
                {

                    if (orderToValidate.getPizzasInOrder()[z].name().equals(definedRestaurants[x].menu()[y].name()))
                    {
                        if(z == 0)
                        {
                            firstPizzaIn = x;
                        }
                        pizzaFound[z] = true;
                    }
                }
            }
        }
        for (int zz = 0; zz < orderToValidate.getPizzasInOrder().length; zz++)
        {
            if (!pizzaFound[zz])
            {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);
            }
        }
        //checks every subsequent pizza to see if its name is found in another restaurant. we use the fact that each
        //name is unique to be able to use this.
        //only need to check the restaurant that the first pizza is in.

        for(int i = 1; i < orderToValidate.getPizzasInOrder().length; i++)
        {

            for(int j = 0; j < definedRestaurants[firstPizzaIn].menu().length; j++)
            {
                if (orderToValidate.getPizzasInOrder()[i].name().equals(definedRestaurants[firstPizzaIn].menu()[j].name()))
                {
                    currentPizzaIn = firstPizzaIn;

                }
            }
            if(currentPizzaIn == -1)
            {
                orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);
                break;
            }
            else
            {
                currentPizzaIn = -1;
            }


        }



        if(orderToValidate.getOrderValidationCode().equals(OrderValidationCode.UNDEFINED))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        }
        return orderToValidate;
    }


}


