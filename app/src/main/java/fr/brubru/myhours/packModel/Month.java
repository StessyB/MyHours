package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.packUtils.Utils;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class Month
{
    private long id;
    private String name;
    private int numMonth;
    private int year;
    private Hour numberHours;
    private List<Day> myDays;
    private List<Week> myWeeks;

    public Month()
    {
        this.myDays = new ArrayList<>();
        this.myWeeks = new ArrayList<>();
        this.numberHours = new Hour();
        this.name = "";
        this.year = 0;
        this.numMonth = 0;
        this.id = 0;
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

    public int getNumMonth()
    {
        return numMonth;
    }

    public void setNumMonth(int numMonth)
    {
        this.numMonth = numMonth;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void setNumberHours(Hour numberHours)
    {
        this.numberHours = numberHours;
    }

    public Hour getNumberHours()
    {
        Hour hour = new Hour();
        int hours = 0;
        int minutes = 0;
        for(Week week : this.getMyWeeks())
        {
            Hour hWeek = week.getNumberHours();
            hours += hWeek.hour;
            minutes += hWeek.minute;
        }
        hours = hours + minutes / 60;
        minutes = minutes % 60;
        hour.hour = hours;
        hour.minute = minutes;
        this.numberHours = hour;
        return hour;
    }

    public List<Day> getMyDays()
    {
        return myDays;
    }

    public List<Week> getMyWeeks() {
        return myWeeks;
    }

    public void setMyWeeks(List<Week> myWeeks) {
        this.myWeeks = myWeeks;
    }

    public void setMyDays(List<Day> myDays)
    {
        this.myDays = myDays;
    }

    @Override
    public String toString()
    {
        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))  return name + " " + year;
        if((this.getNumberHours().hour < 0) && (this.getNumberHours().minute < 0))  return name + " " + year + " (00h00)";
        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))  return name + " " + year + " (00h" + Utils.pad(this.getNumberHours().minute) + ")";
        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))  return name + " " + year + " (" + this.getNumberHours().hour + "h00)";
        return name + " " + year + " (" + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute) + ")";
    }
}
