package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class Month
{
    private long id;
    private String name;
    private int numMonth;
    private int year;
    private List<Day> myDays;
    private List<Week> myWeeks;

    public Month(String month, int year, List<Day> days)
    {
        this.myDays = days;
        this.name = month;
        this.year = year;
    }

    public Month()
    {
        this.myDays = new ArrayList<>();
        this.myWeeks = new ArrayList<>();
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
        return name + " " + year;
    }
}
