package fr.brubru.myhours.packModel;

import java.util.Date;

import fr.brubru.myhours.packUtils.Utils;

/**
 * Created by Stessy on 07/01/2015.
 */
public class Day
{
    private long id;
    private int numberWeek;
    private long idMonth;
    private String day;
    private Date dateDay;
    private String dayUS;
    private String dayWeek;
    private String H1;
    private String H2;
    private String H3;
    private String H4;
    private Hour morningHours;
    private Hour afternoonHours;

    public Day(String d, String h1, String h2, String h3, String h4)
    {
        this.idMonth = -1;
        this.day = d;
        this.H1 = h1;
        this.H2 = h2;
        this.H3 = h3;
        this.H4 = h4;
        this.morningHours = new Hour();
        this.afternoonHours = new Hour();
        this.id = 0;
        this.dateDay = Utils.stringToDate(this.day);
        this.dayUS = Utils.Format_FR_US(this.day);
        this.numberWeek = Utils.getNumberWeek(this.day);
        this.dayWeek = Utils.getDayWeek(this.day);
    }

    public Day()
    {
        this.idMonth = -1;
        this.day = "";
        this.H1 = "";
        this.H2 = "";
        this.H3 = "";
        this.H4 = "";
        this.morningHours = new Hour();
        this.afternoonHours = new Hour();
        this.id = 0;
        this.numberWeek = 0;
        this.dateDay = new Date();
        this.dayUS = "";
        this.dayWeek = "";
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getDay()
    {
        return day;
    }

    public void setDay(String day)
    {
        this.day = day;
        this.dateDay = Utils.stringToDate(this.day);
        this.dayUS = Utils.Format_FR_US(this.day);
        this.numberWeek = Utils.getNumberWeek(this.day);
        this.dayWeek = Utils.getDayWeek(this.day);
    }

    public void setNumberWeek(int numberWeek)
    {
        this.numberWeek = numberWeek;
    }

    public String getH1()
    {
        return H1;
    }

    public void setH1(String h1)
    {
        H1 = h1;
    }

    public String getH2()
    {
        return H2;
    }

    public void setH2(String h2)
    {
        H2 = h2;
    }

    public String getH3()
    {
        return H3;
    }

    public void setH3(String h3)
    {
        H3 = h3;
    }

    public String getH4()
    {
        return H4;
    }

    public void setH4(String h4)
    {
        H4 = h4;
    }

    public Date getDateDay()
    {
        return dateDay;
    }

    public String getDayUS()
    {
        return dayUS;
    }

    public long getIdMonth()
    {
        return idMonth;
    }

    public void setIdMonth(long idMonth)
    {
        this.idMonth = idMonth;
    }

    public int getNumberWeek()
    {
        return numberWeek;
    }

    public String getDayWeek()
    {
        return dayWeek;
    }

    public Hour getMorningHours()
    {
        morningHours = Utils.compareHours(H1, H2);
        return morningHours;
    }

    public Hour getAfternoonHours()
    {
        afternoonHours = Utils.compareHours(H3, H4);
        return afternoonHours;
    }

    @Override
    public String toString()
    {
        return dayWeek + " " + day + "\n" + H1 + " " + H2 + " " + H3 + " " + H4;
    }

    public String getExport()
    {
        return dayWeek + " " + day + " : " + H1 + " " + H2 + " " + H3 + " " + H4;
    }
}
