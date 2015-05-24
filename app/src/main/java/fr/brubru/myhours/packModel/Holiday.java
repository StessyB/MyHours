package fr.brubru.myhours.packModel;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import fr.brubru.myhours.packUtils.Variables;

/**
 * Created by StessyB on 02/02/2015.
 */
public class Holiday
{
    public static double myCP, myRTT, myFerie;
    private long id;
    private String name;
    private float value;

    public Holiday(String name)
    {
        this.name = name;
        this.value = 0;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public float getValue()
    {
        return value;
    }

    public void setValue(float value)
    {
        this.value = value;
    }

    @Override
    public String toString()
    {
        return name + " --> " + value;
    }

    public static double getMyCP()
    {
        return Variables.myCP.getValue();
    }

    public static double getMyRTT()
    {
        return Variables.myRTT.getValue();
    }

    public static String getMyHolidaysString()
    {
        DecimalFormat df = new DecimalFormat("##.###");
        df.setRoundingMode(RoundingMode.DOWN);
        DecimalFormatSymbols dfs = new DecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        String CP = df.format(getMyCP());
        String RTT = df.format(getMyRTT());
        if(Variables.myNbHours.equals("37"))
            return "Solde CP : " + CP + ", solde RTT : " + RTT + "\nPris " +  df.format(myCP) + " CP et " +  df.format(myRTT) + " RTT\n" + "Ferie : " + df.format(myFerie);
        else
            return "Solde CP : " + CP + "\nPris " +  df.format(myCP) + " CP\n" + "Ferie : " + df.format(myFerie);
    }
}
