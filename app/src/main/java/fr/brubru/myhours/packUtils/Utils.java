package fr.brubru.myhours.packUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Stessy on 07/01/2015.
 */
public class Utils
{
    public static void createDir(String logDir)
    {
        File myFile = new File(logDir);
        if(!myFile.exists()) myFile.mkdirs();
    }

    public static String pad(int c)
    {
        if (c >= 10) return String.valueOf(c);
        return "0" + String.valueOf(c);
    }

    public static Date stringToDate(String dateInString)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        try
        {
            return formatter.parse(dateInString);
        } catch (ParseException e) { System.out.println("Utils.stringToDate Error : " + e.getMessage()); return null; }
    }

    public static String Format_FR_US(String dateFR)
    {
        String dateUS = "";
        SimpleDateFormat myFRFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat myUSFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            dateUS = myUSFormat.format(myFRFormat.parse(dateFR));
            return dateUS;
        }
        catch (ParseException e) { System.out.println("Utils.Format_FR_US error : " + e.getMessage()); return dateUS; }
    }

    public static String Format_US_FR(String dateUS)
    {
        String dateFR = "";
        SimpleDateFormat myFRFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat myUSFormat = new SimpleDateFormat("yyyy-MM-dd");
        try
        {
            dateFR = myFRFormat.format(myUSFormat.parse(dateUS));
            return dateFR;
        }
        catch (ParseException e) { System.out.println("Utils.Format_US_FR error : " + e.getMessage()); return dateFR; }
    }
}
