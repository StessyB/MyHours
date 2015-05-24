package fr.brubru.myhours.packModel;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;

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

    public Week()
    {
        this.id = 0;
        this.idMonth = 0;
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

    public Hour getNumberHours()
    {
        Hour hour = new Hour();
        int hours = 0;
        int minutes = 0;
        for(Day day : this.myDays)
        {
            if((day.getType().equals("Travail")) || (!day.getTime().equals("journée")))
            {
                if(day.getType().equals("Travail"))
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
                    if(Variables.isSee || Variables.isLaunchAutoSet)
                    {
                        int launchMinutes = 60;
                        int minute = Utils.compareTime(day.getH2(), day.getH3());
                        if(Variables.myLaunchMinutes != null) launchMinutes = Integer.parseInt(Variables.myLaunchMinutes);
                        else Variables.myLaunchMinutes = "60";

                        if((minute < launchMinutes) && (minute > 0))
                        {
                            if (Variables.isSee)
                            {
                                Hour h = Utils.addMinute(day.getH3(), launchMinutes - minute);
                                day.setH3(Utils.pad(h.hour) + ":" + Utils.pad(h.minute));
                            }
                            minutes = minutes - (launchMinutes - minute);
                        }
                    }
                }
                if(day.getTime().equals("matin"))
                {
                    Hour hAfternoon = day.getAfternoonHours();
                    if((hAfternoon.hour >= 0) && (hAfternoon.minute >= 0))
                    {
                        hours += hAfternoon.hour;
                        minutes += hAfternoon.minute;
                    }
                    else if((hAfternoon.hour > 0) && (hAfternoon.minute > 0))
                    {
                        hours += hAfternoon.hour;
                        minutes += hAfternoon.minute;
                    }
                }
                if(day.getTime().equals("après-midi"))
                {
                    Hour hMorning = day.getMorningHours();
                    if((hMorning.hour >= 0) && (hMorning.minute >= 0))
                    {
                        hours += hMorning.hour;
                        minutes += hMorning.minute;
                    }
                    else if((hMorning.hour >= 0) && (hMorning.minute >= 0))
                    {
                        hours += hMorning.hour;
                        minutes += hMorning.minute;
                    }
                }
            }
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
        for(Day day : this.myDays)
        {
            if(day.getType().equals("Travail"))
            {
                Hour hMorning = day.getMorningHours();
                Hour hAfternoon = day.getAfternoonHours();
                int seconds = hMorning.hour * 60 + hMorning.minute + hAfternoon.hour * 60 + hAfternoon.minute;
                if(seconds > 60) nbDays++;
            }
            if(day.getTime().equals("matin"))
            {
                Hour hAfternoon = day.getAfternoonHours();
                int seconds = hAfternoon.hour * 60 + hAfternoon.minute;
                if(seconds > 60) nbDays = nbDays + 0.5;
            }
            if(day.getTime().equals("après-midi"))
            {
                Hour hMorning = day.getMorningHours();
                int seconds = hMorning.hour * 60 + hMorning.minute;
                if(seconds > 60) nbDays = nbDays + 0.5;
            }
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
        for(Day day : this.myDays)
        {
            if(!day.getType().equals("Travail"))
            {
                if(day.getTime().equals("journée"))
                {
                    if(day.getType().equals("CP"))
                    {
                        Holiday.myCP += 1;
                        nbCP += 1;
                    }
                    else if(day.getType().equals("RTT"))
                    {
                        Holiday.myRTT += 1;
                        nbRTT += 1;
                    }
                    else if(day.getType().equals("Férié"))
                    {
                        Holiday.myFerie += 1;
                        nbFerie += 1;
                    }
                    else
                        nbDays++;
                }
                if((day.getTime().equals("matin")) || (day.getTime().equals("après-midi")))
                {
                    if(day.getType().equals("CP"))
                    {
                        Holiday.myCP += 0.5;
                        nbCP += 0.5;
                    }
                    else if(day.getType().equals("RTT"))
                    {
                        Holiday.myRTT += 0.5;
                        nbRTT += 0.5;
                    }
                    else
                        nbDays = nbDays + 0.5;
                }
            }
        }
        myList.add(nbCP);
        myList.add(nbRTT);
        myList.add(nbFerie);
        myList.add(nbDays);
        return myList;
    }

    public Hour getAcquiredHours()
    {
        Hour h = new Hour();
        Hour myWeekHours = this.getNumberHours();
        int minuteWeek = myWeekHours.hour * 60 + myWeekHours.minute;
        int minutesToWork = 0;
        int mondayMorningMinute = Utils.compareTime(Variables.myMondayDay.getH1(), Variables.myMondayDay.getH2());
        int mondayAfternoonMinute = Utils.compareTime(Variables.myMondayDay.getH3(), Variables.myMondayDay.getH4());
        int mondayMinute = mondayMorningMinute + mondayAfternoonMinute;
        int tuesdayMorningMinute = Utils.compareTime(Variables.myTuesdayDay.getH1(), Variables.myTuesdayDay.getH2());
        int tuesdayAfternoonMinute = Utils.compareTime(Variables.myTuesdayDay.getH3(), Variables.myTuesdayDay.getH4());
        int tuesdayMinute = tuesdayMorningMinute + tuesdayAfternoonMinute;
        int wednesdayMorningMinute = Utils.compareTime(Variables.myWednesdayDay.getH1(), Variables.myWednesdayDay.getH2());
        int wednesdayAfternoonMinute = Utils.compareTime(Variables.myWednesdayDay.getH3(), Variables.myWednesdayDay.getH4());
        int wednesdayMinute = wednesdayMorningMinute + wednesdayAfternoonMinute;
        int thursdayMorningMinute = Utils.compareTime(Variables.myThursdayDay.getH1(), Variables.myThursdayDay.getH2());
        int thursdayAfternoonMinute = Utils.compareTime(Variables.myThursdayDay.getH3(), Variables.myThursdayDay.getH4());
        int thursdayMinute = thursdayMorningMinute + thursdayAfternoonMinute;
        int fridayMorningMinute = Utils.compareTime(Variables.myFridayDay.getH1(), Variables.myFridayDay.getH2());
        int fridayAfternoonMinute = Utils.compareTime(Variables.myFridayDay.getH3(), Variables.myFridayDay.getH4());
        int fridayMinute = fridayMorningMinute + fridayAfternoonMinute;
        for(Day day : this.myDays)
        {
            if((day.getType().equals("Travail")) || (!day.getTime().equals("journée")))
            {
                String dayString = day.getDayWeek();
                dayString = dayString.replace(".", "");
                dayString = dayString.toLowerCase();

                switch (dayString)
                {
                    case "lun" :
                        if(day.getTime().equals("matin")) minutesToWork += mondayAfternoonMinute;
                        if(day.getTime().equals("après-midi")) minutesToWork += mondayMorningMinute;
                        if(day.getType().equals("Travail"))
                        {
                            if((day.getH3().equals("00:00")) && (day.getH4().equals("00:00"))) minutesToWork += mondayMorningMinute;
                            else
                                minutesToWork += mondayMinute;
                        }
                        break;
                    case "mar" :
                        if(day.getTime().equals("matin")) minutesToWork += tuesdayAfternoonMinute;
                        if(day.getTime().equals("après-midi")) minutesToWork += tuesdayMorningMinute;
                        if(day.getType().equals("Travail"))
                        {
                            if((day.getH3().equals("00:00")) && (day.getH4().equals("00:00"))) minutesToWork += tuesdayMorningMinute;
                            else
                                minutesToWork += tuesdayMinute;
                        }
                        break;
                    case "mer" :
                        if(day.getTime().equals("matin")) minutesToWork += wednesdayAfternoonMinute;
                        if(day.getTime().equals("après-midi")) minutesToWork += wednesdayMorningMinute;
                        if(day.getType().equals("Travail"))
                        {
                            if((day.getH3().equals("00:00")) && (day.getH4().equals("00:00"))) minutesToWork += wednesdayMorningMinute;
                            else
                                minutesToWork += wednesdayMinute;
                        }
                        break;
                    case "jeu" :
                        if(day.getTime().equals("matin")) minutesToWork += thursdayAfternoonMinute;
                        if(day.getTime().equals("après-midi")) minutesToWork += thursdayMorningMinute;
                        if(day.getType().equals("Travail"))
                        {
                            if((day.getH3().equals("00:00")) && (day.getH4().equals("00:00"))) minutesToWork += thursdayMorningMinute;
                            else
                                minutesToWork += thursdayMinute;
                        }
                        break;
                    case "ven" :
                        if(day.getTime().equals("matin")) minutesToWork += fridayAfternoonMinute;
                        if(day.getTime().equals("après-midi")) minutesToWork += fridayMorningMinute;
                        if(day.getType().equals("Travail"))
                        {
                            if((day.getH3().equals("00:00")) && (day.getH4().equals("00:00"))) minutesToWork += fridayMorningMinute;
                            else
                                minutesToWork += fridayMinute;
                        }
                        break;
                }
            }
        }
        int difference = minuteWeek - minutesToWork;
        h.hour = difference / 60;
        h.minute = difference % 60;
        return h;
    }

    public List<Day> getMyDays()
    {
        return myDays;
    }

    public void setMyDays(List<Day> myDays)
    {
        this.myDays = myDays;
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

    @Override
    public String toString()
    {

        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))  return "Semaine " + number + " (" + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : 00h00 (" + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : 00h" + Utils.pad(this.getNumberHours().minute) + " (" + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : " + this.getNumberHours().hour + "h00 (" + this.getAcquiredHoursToString() + ")";

        return "Semaine " + number + " : " + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute) + " (" + this.getAcquiredHoursToString() + ")";
    }

    public String getExport()
    {

        if((this.getNumberHours().hour == 0) && (this.getNumberHours().minute == 0))
            return "Semaine " + number + " : Heures travaillées 00h00 (Reste " + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : Heures travaillées 00h00 (Reste " + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour < 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute > 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : Heures travaillées 00h" + Utils.pad(this.getNumberHours().minute) + " (Reste " + this.getAcquiredHoursToString() + ")";

        if(((this.getNumberHours().hour > 0) || (this.getNumberHours().hour == 0)) && ((this.getNumberHours().minute < 0) || (this.getNumberHours().minute == 0)))
            return "Semaine " + number + " : Heures travaillées " + this.getNumberHours().hour + "h00 (Reste " + this.getAcquiredHoursToString() + ")";

        return "Semaine " + number + " : Heures travaillées " + this.getNumberHours().hour + "h" + Utils.pad(this.getNumberHours().minute) + " (Reste " + this.getAcquiredHoursToString() + ")";
    }
}
