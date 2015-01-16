package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stessy on 14/01/2015.
 */
public class Week
{
    private long id;
    private int number;
    private long idMonth;
    private int year;
    private List<Day> myDays;

    public Week(int num, int y, List<Day> d)
    {
        this.number = num;
        this.year = y;
        this.id = 0;
        this.myDays = d;
    }

    public Week()
    {
        this.id = 0;
        this.year = 0;
        this.number = 0;
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
        return "Semaine " + number;
    }
}
