package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class  Month
{
    private long id;
    private String name;
    private int numMonth;
    private int year;
    private List<Week> myWeeks;
    private List<Day> myDays;

    public Month()
    {
        this.myWeeks = new ArrayList<>();
        this.myDays = new ArrayList<>();
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
        return hour;
    }

    public double getMyNumberDayWorked()
    {
        double nbDays = 0;
        for(Week week : this.getMyWeeks())
        {
            nbDays += week.getMyNumberDayWorked();
        }
        return nbDays;
    }

    public List<Double> getMyNumberDayNotWorked()
    {
        List<Double> myList = new ArrayList<>();
        double nbRTT = 0;
        double nbCP = 0;
        double nbFerie = 0;
        double nbDays = 0;
        for(Week week : this.getMyWeeks())
        {
            List<Double> tmp = week.getMyNumberDayNotWorked();
            nbCP += tmp.get(0);
            nbRTT += tmp.get(1);
            nbFerie += tmp.get(2);
            nbDays += tmp.get(3);
        }
        myList.add(nbCP);
        myList.add(nbRTT);
        myList.add(nbFerie);
        myList.add(nbDays);
        return myList;
    }

    public String getMyNumberDayNotWorkedToString()
    {
        List<Double> myList = this.getMyNumberDayNotWorked();
        // CP, RTT, Ferie, autres (CE, CSS ou maladie)
        if(Variables.myNbHours.equals("37"))
            return myList.get(0) + " CP, " + myList.get(1) + " RTT, " + myList.get(2) + " Ferie, " + myList.get(3) + " CE, CSS ou maladie";
        else
            return myList.get(0) + " CP, " + myList.get(2) + " Ferie, " + myList.get(3) + " CE, CSS ou maladie";
    }

    public Hour getAcquiredHours()
    {
        Hour hour = new Hour();
        int hours = 0;
        int minutes = 0;
        for(Week week : this.getMyWeeks())
        {
            Hour hWeek = week.getAcquiredHours();
            hours += hWeek.hour;
            minutes += hWeek.minute;
        }
        hours = hours + minutes / 60;
        minutes = minutes % 60;
        hour.hour = hours;
        hour.minute = minutes;
        return hour;
    }

    public String getAcquiredHoursToString()
    {
        Hour myRest = this.getAcquiredHours();
        if(myRest.hour == 0) return Utils.pad(myRest.minute) + "mn";
        if((myRest.hour < 0) && (myRest.minute < 0)) return myRest.hour + "h" + Utils.pad(-1 * myRest.minute);
        if((myRest.hour > 0) && (myRest.minute < 0)) return myRest.hour + "h" + Utils.pad(-1 * myRest.minute);
        if(myRest.minute == 0) return myRest.hour + "h";
        return myRest.hour + "h" + Utils.pad(myRest.minute);

    }

    public List<Week> getMyWeeks() {
        return myWeeks;
    }

    public void setMyWeeks(List<Week> myWeeks) {
        this.myWeeks = myWeeks;
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
        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))  return name + " " + year;

        if((this.getNumberHours().hour < 0) && (this.getNumberHours().minute < 0))  return name + " " + year + " : 00h00 " + " (" + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))
            return name + " " + year + " : 00h" + Utils.pad(this.getNumberHours().minute) + " (" + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return name + " " + year + " : " + this.getNumberHours().hour + "h00 (" + this.getAcquiredHoursToString() + ")";

        return name + " " + year + " : " + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute) + " (" + this.getAcquiredHoursToString() + ")";
    }

    public String getExport()
    {
        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))
            return name + " " + year + ", " + this.getMyNumberDayWorked() + " jours et " + this.getMyNumberDayNotWorkedToString();

        if((this.getNumberHours().hour < 0) && (this.getNumberHours().minute < 0))
            return name + " " + year + " : Total 00h00, " + this.getMyNumberDayWorked() + " jours et " + this.getMyNumberDayNotWorkedToString()
                    + "\n Cumul : " + this.getAcquiredHoursToString();

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))
            return name + " " + year + " : Total 00h" + Utils.pad(this.getNumberHours().minute) + ", " + this.getMyNumberDayWorked() + " jours et " + this.getMyNumberDayNotWorkedToString()
                    + "\n Cumul : " + this.getAcquiredHoursToString();

        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return name + " " + year + " : Total " + this.getNumberHours().hour + "h00, " + this.getMyNumberDayWorked() + " jours et " + this.getMyNumberDayNotWorkedToString()
                    + "\n Cumul : " + this.getAcquiredHoursToString();

        return name + " " + year + " : Total " + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute) + ", " + this.getMyNumberDayWorked() + " jours et " + this.getMyNumberDayNotWorkedToString()
                + "\n Cumul : " + this.getAcquiredHoursToString();
    }
}
