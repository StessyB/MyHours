package fr.brubru.myhours.packUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.brubru.myhours.packModel.Hour;

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

    public static Hour compareHours(String H1, String H2)
    {
        Hour h = new Hour();
        int h1 = Integer.parseInt(H1.split(":")[0]);
        int m1 = Integer.parseInt(H1.split(":")[1]);
        int h2 = Integer.parseInt(H2.split(":")[0]);
        int m2 = Integer.parseInt(H2.split(":")[1]);
        double mTotal1 = h1 * 60 + m1;
        double mTotal2 = h2 * 60 + m2;
        int ecart = (int) (mTotal2 - mTotal1);
        int ecartHour = ecart / 60;
        int ecartMinute = ecart % 60;
        h.hour = ecartHour;
        h.minute = ecartMinute;
        return h;
    }

    public static int getNumberWeek(String day)
    {
        String format = "dd/MM/yyyy";
        SimpleDateFormat df = new SimpleDateFormat(format);
        Date date = null;
        try
        {
            date = df.parse(day);
            Calendar myCalendar = Calendar.getInstance();
            myCalendar.setTime(date);
            return myCalendar.get(Calendar.WEEK_OF_YEAR);
        }
        catch (ParseException e) { System.out.println("Utils.getNumberWeek error : " + e.getMessage()); return 0; }
    }

    public static String getDayWeek(String day)
    {
        final String myFormat = "dd/MM/yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        try
        {
            Date myDate = sdf.parse(day);
            return (String) android.text.format.DateFormat.format("E", myDate);
        }
        catch (ParseException e) { System.out.println("Utils.getDayWeek parse 2 error : " + e.getMessage()); return null; }
    }
}
