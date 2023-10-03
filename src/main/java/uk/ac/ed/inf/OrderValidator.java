package uk.ac.ed.inf;


import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.OrderValidation;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.time.YearMonth;


public class OrderValidator implements OrderValidation
{
    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants)
    {

        if(!orderToValidate.getCreditCardInformation().getCreditCardNumber().matches("^[0-9]{16}$"))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);
        }
        else if(!orderToValidate.getCreditCardInformation().getCvv().matches("^[0-9]{3}$"))
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);
        }
        YearMonth expiry = YearMonth.parse(orderToValidate.getCreditCardInformation().getCreditCardExpiry());
        //change YearMonth.now to order date pls :3
        else if(orderToValidate.getCreditCardInformation().getCreditCardExpiry().matches("(0[1-9]|1[0-2])/[0-9][0-9]")) //&&
                //YearMonth.parse(orderToValidate.getCreditCardInformation().getCreditCardExpiry(), DateTimeFormatter.ofPattern("MM/yy")).isBefore( YearMonth.parse(YearMonth.now().toString(), DateTimeFormatter.ofPattern("MM/yy")) ) )
        {
            YearMonth expiry = YearMonth.parse(orderToValidate.getCreditCardInformation().getCreditCardExpiry());

            if (expiry.isBefore(YearMonth.now()))
            {

            }
        }
        else
        {
            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);
        }
        return orderToValidate;
    }


}


