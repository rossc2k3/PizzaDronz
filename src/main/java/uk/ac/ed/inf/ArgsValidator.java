package uk.ac.ed.inf;

import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;

public class ArgsValidator
{
    public static void argsValidate(String[] args)
    {
        //validating args; mainly concerned with the formatting of the date with some limiting for
        //a date actually existing (valid month). doesn't account for putting in the 31st date of
        //a month with only 30, this will get picked up by the URL not existing later on

        if (args.length != 2)
        {
            System.err.println("Error: Please provide only a YYYY-mm-dd date and a URl as arguments.");
            System.exit(1);
        }
        String date = args[0];
        String site = args[1];

        if (!date.matches("^[0-9]{4}-(1[0-2]|0[1-9])-(0[1-9]|[12][0-9]|3[01])$"))
        {
            System.err.println("Error: The first argument must be a date in YYYY-mm-dd format.");
            System.exit(1);
        }
        try
        {
            new URL(site);
        } catch (MalformedURLException e)
        {
            System.err.println("Error: The second argument must be a valid URL.");
            System.exit(1);
        }
    }
}
