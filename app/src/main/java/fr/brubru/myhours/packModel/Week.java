package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.packUtils.Utils;

/**
 * Created by Stessy on 14/01/2015.
 */
public class Week
{
    private long id;
    private int number;
    private Hour numberHours;
    private long idMonth;
    private int year;
    private List<Day> myDays;

    public Week()
    {
        this.id = 0;
        this.idMonth = 0;
        this.year = 0;
        this.number = 0;
        this.numberHours = new Hour();
        this.myDays = new ArrayList<>();
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public int getNumber()
    {
        return number;
    }

    public void setNumber(int number)
    {
        this.number = number;
    }

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public long getIdMonth()
    {
        return idMonth;
    }

    public void setIdMonth(long idMonth)
    {
        this.idMonth = idMonth;
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
        for(Day day : this.myDays)
        {
            Hour hMorning = day.getMorningHours();
            Hour hAfternoon = day.getAfternoonHours();
            if((hMorning.hour >= 0) && (hMorning.minute >= 0)&& (hAfternoon.hour >= 0) && (hAfternoon.minute >= 0))
            {
                hours += hMorning.hour + hAfternoon.hour;
                minutes += hMorning.minute + hAfternoon.minute;
            }
            else if((hMorning.hour >= 0) && (hMorning.minute >= 0)&& (hAfternoon.hour <= 0) && (hAfternoon.minute <= 0))
            {
                hours += hMorning.hour;
                minutes += hMorning.minute;
            }
            else if((hMorning.hour <= 0) && (hMorning.minute <= 0)&& (hAfternoon.hour > 0) && (hAfternoon.minute > 0))
            {
                hours += hAfternoon.hour;
                minutes += hAfternoon.minute;
            }
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

    public void setMyDays(List<Day> myDays)
    {
        this.myDays = myDays;
    }

    @Override
    public String toString()
    {
        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))  return "Semaine " + number;
        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))  return "Semaine " + number + " --> 00h00";
        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))  return "Semaine " + number + " --> 00h" + Utils.pad(this.getNumberHours().minute);
        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))  return "Semaine " + number + " --> " + this.getNumberHours().hour + "h00";
        return "Semaine " + number + " --> " + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute);
    }
}
