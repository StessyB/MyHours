package fr.brubru.myhours.packModel;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import java.util.Date;

import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;

/**
 * Created by Stessy on 07/01/2015.
 */
public class Day
{
    private long id;
    private int numberWeek;
    private String type;
    private String time;
    private String myTypes;
    private int year;
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
    private Hour holidaysHours;

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
        holidaysHours = new Hour();
        this.id = 0;
        this.dateDay = Utils.stringToDate(this.day);
        this.dayUS = Utils.Format_FR_US(this.day);
        this.numberWeek = Utils.getNumberWeek(this.day);
        this.dayWeek = Utils.getDayWeek(this.day);
        this.year = Utils.getYearOfDay(this.day);
        this.type = "Travail";
        this.time = "journée";
        this.myTypes = this.type + " " + this.time;
    }

    public Day(String d, String h1)
    {
        this.idMonth = -1;
        this.day = d;
        this.H1 = h1;
        this.H2 = "";
        this.H3 = "";
        this.H4 = "";
        this.morningHours = new Hour();
        this.afternoonHours = new Hour();
        holidaysHours = new Hour();
        this.id = 0;
        this.dateDay = Utils.stringToDate(this.day);
        this.dayUS = Utils.Format_FR_US(this.day);
        this.numberWeek = Utils.getNumberWeek(this.day);
        this.dayWeek = Utils.getDayWeek(this.day);
        this.year = Utils.getYearOfDay(this.day);
        this.type = "Travail";
        this.time = "journée";
        this.myTypes = this.type + " " + this.time;
    }

    public Day()
    {
        this.idMonth = -1;
        this.day = "";
        this.H1 = "";
        this.H2 = "";
        this.H3 = "";
        this.H4 = "";
        this.dayUS = "";
        this.dayWeek = "";
        this.type = "Travail";
        this.time = "journée";
        this.myTypes = this.type + " " + this.time;
        this.dateDay = new Date();
        this.morningHours = new Hour();
        this.afternoonHours = new Hour();
        holidaysHours = new Hour();
        this.id = 0;
        this.numberWeek = 0;
        this.year = 0;
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
        this.year = Utils.getYearOfDay(this.day);
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

    public int getYear()
    {
        return year;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public Hour getMorningHours()
    {
        if((this.type.equals("Travail")) || (!this.time.equals("matin")))
        {
            // TODO
            morningHours = Utils.compareHours(H1, H2);
            //morningHours = Utils.proceedPause(1, morningHours);
        }
        if((!this.type.equals("Travail")) && (this.time.equals("matin"))) morningHours = new Hour();
        if((!this.type.equals("Travail")) && (this.time.equals("journée"))) morningHours = new Hour();
        return morningHours;
    }

    public Hour getAfternoonHours()
    {
        if((this.type.equals("Travail")) || (!this.time.equals("après-midi")))
        {
            // TODO
            afternoonHours = Utils.compareHours(H3, H4);
            //afternoonHours = Utils.proceedPause(2, afternoonHours);
        }
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) afternoonHours = new Hour();
        if((!this.type.equals("Travail")) && (this.time.equals("journée"))) afternoonHours = new Hour();
        return afternoonHours;
    }

    public Hour getHolidaysHours()
    {
        holidaysHours = new Hour();
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) holidaysHours = Utils.compareHours(H3, H4);
        if((!this.type.equals("Travail")) && (this.time.equals("journée"))) holidaysHours = new Hour();
        return holidaysHours;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
        this.myTypes = this.type + " " + this.time;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
        this.myTypes = this.type + " " + this.time;
    }

    public String getMyTypes()
    {
        return myTypes;
    }

    public void setMyTypes(String myTypes)
    {
        this.myTypes = myTypes;
        this.type = this.myTypes.split(" ")[0];
        this.time = this.myTypes.split(" ")[1];
    }

    public String getHoursToString()
    {
        Hour myRest = this.getHours();
        if(myRest.hour == 0) return Utils.pad(myRest.minute) + "mn";
        if((myRest.hour < 0) && (myRest.minute < 0)) return myRest.hour + "h" + Utils.pad(-1 * myRest.minute);
        if((myRest.hour > 0) && (myRest.minute < 0)) return myRest.hour + "h" + Utils.pad(-1 * myRest.minute);
        if(myRest.minute == 0) return myRest.hour + "h";
        return myRest.hour + "h" + Utils.pad(myRest.minute);

    }

    public Hour getHours()
    {
        int minuteDay = 0;
        int minutesToWork = 0;
        int mondayMorningMinute = Utils.compareTime(Variables.myMondayDay.getH1(), Variables.myMondayDay.getH2());
        int mondayAfternoonMinute = Utils.compareTime(Variables.myMondayDay.getH3(), Variables.myMondayDay.getH4());
        int tuesdayMorningMinute = Utils.compareTime(Variables.myTuesdayDay.getH1(), Variables.myTuesdayDay.getH2());
        int tuesdayAfternoonMinute = Utils.compareTime(Variables.myTuesdayDay.getH3(), Variables.myTuesdayDay.getH4());
        int wednesdayMorningMinute = Utils.compareTime(Variables.myWednesdayDay.getH1(), Variables.myWednesdayDay.getH2());
        int wednesdayAfternoonMinute = Utils.compareTime(Variables.myWednesdayDay.getH3(), Variables.myWednesdayDay.getH4());
        int thursdayMorningMinute = Utils.compareTime(Variables.myThursdayDay.getH1(), Variables.myThursdayDay.getH2());
        int thursdayAfternoonMinute = Utils.compareTime(Variables.myThursdayDay.getH3(), Variables.myThursdayDay.getH4());
        int fridayMorningMinute = Utils.compareTime(Variables.myFridayDay.getH1(), Variables.myFridayDay.getH2());
        int fridayAfternoonMinute = Utils.compareTime(Variables.myFridayDay.getH3(), Variables.myFridayDay.getH4());

        if((this.type.equals("Travail")) || (!this.time.equals("journée")))
        {
            String dayString = this.getDayWeek();
            dayString = dayString.replace(".", "");
            dayString = dayString.toLowerCase();
            Day currentDay = Utils.getCurrentDay();
            Hour hour;

            switch (dayString)
            {
                case "lun" :
                    if((!this.getH1().equals("00:00")) && (this.getH2().equals("00:00")))
                    {
                        minutesToWork += mondayMorningMinute;
                        if((Utils.compareTime(H1, currentDay.getH1())) > (Utils.compareTime(H1, Variables.myMondayDay.getH2()) + 120))
                        {
                            hour = Utils.compareHours(H1, Variables.myMondayDay.getH2());
                        }
                        else
                            hour = Utils.compareHours(H1, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH1().equals("00:00")) && (!this.getH2().equals("00:00")))
                    {
                        minutesToWork += mondayMorningMinute;
                        hour = Utils.compareHours(H1, H2);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (this.getH4().equals("00:00")))
                    {
                        minutesToWork += mondayAfternoonMinute;
                        if((Utils.compareTime(H3, currentDay.getH1())) > (Utils.compareTime(H3, Variables.myMondayDay.getH4()) + 120))
                            hour = Utils.compareHours(H3, Variables.myMondayDay.getH4());
                        else
                            hour = Utils.compareHours(H3, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (!this.getH4().equals("00:00")))
                    {
                        minutesToWork += mondayAfternoonMinute;
                        hour = Utils.compareHours(H3, H4);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    break;
                case "mar" :
                    if((!this.getH1().equals("00:00")) && (this.getH2().equals("00:00")))
                    {
                        minutesToWork += tuesdayMorningMinute;
                        if((Utils.compareTime(H1, currentDay.getH1())) > (Utils.compareTime(H1, Variables.myTuesdayDay.getH2()) + 120))
                            hour = Utils.compareHours(H1, Variables.myTuesdayDay.getH2());
                        else
                            hour = Utils.compareHours(H1, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH1().equals("00:00")) && (!this.getH2().equals("00:00")))
                    {
                        minutesToWork += tuesdayMorningMinute;
                        hour = Utils.compareHours(H1, H2);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (this.getH4().equals("00:00")))
                    {
                        minutesToWork += tuesdayAfternoonMinute;
                        if((Utils.compareTime(H3, currentDay.getH1())) > (Utils.compareTime(H3, Variables.myTuesdayDay.getH4()) + 120))
                            hour = Utils.compareHours(H3, Variables.myTuesdayDay.getH4());
                        else
                            hour = Utils.compareHours(H3, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (!this.getH4().equals("00:00")))
                    {
                        minutesToWork += tuesdayAfternoonMinute;
                        hour = Utils.compareHours(H3, H4);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    break;
                case "mer" :
                    if((!this.getH1().equals("00:00")) && (this.getH2().equals("00:00")))
                    {
                        minutesToWork += wednesdayMorningMinute;
                        if((Utils.compareTime(H1, currentDay.getH1())) > (Utils.compareTime(H1, Variables.myWednesdayDay.getH2()) + 120))
                            hour = Utils.compareHours(H1, Variables.myWednesdayDay.getH2());
                        else
                            hour = Utils.compareHours(H1, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH1().equals("00:00")) && (!this.getH2().equals("00:00")))
                    {
                        minutesToWork += wednesdayMorningMinute;
                        hour = Utils.compareHours(H1, H2);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (this.getH4().equals("00:00")))
                    {
                        minutesToWork += wednesdayAfternoonMinute;
                        if((Utils.compareTime(H3, currentDay.getH1())) > (Utils.compareTime(H3, Variables.myWednesdayDay.getH4()) + 120))
                            hour = Utils.compareHours(H3, Variables.myWednesdayDay.getH4());
                        else
                            hour = Utils.compareHours(H3, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (!this.getH4().equals("00:00")))
                    {
                        minutesToWork += wednesdayAfternoonMinute;
                        hour = Utils.compareHours(H3, H4);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    break;
                case "jeu" :
                    if((!this.getH1().equals("00:00")) && (this.getH2().equals("00:00")))
                    {
                        minutesToWork += thursdayMorningMinute;
                        if((Utils.compareTime(H1, currentDay.getH1())) > (Utils.compareTime(H1, Variables.myThursdayDay.getH2()) + 120))
                            hour = Utils.compareHours(H1, Variables.myThursdayDay.getH2());
                        else
                            hour = Utils.compareHours(H1, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH1().equals("00:00")) && (!this.getH2().equals("00:00")))
                    {
                        minutesToWork += thursdayMorningMinute;
                        hour = Utils.compareHours(H1, H2);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (this.getH4().equals("00:00")))
                    {
                        minutesToWork += thursdayAfternoonMinute;
                        if((Utils.compareTime(H3, currentDay.getH1())) > (Utils.compareTime(H3, Variables.myThursdayDay.getH4()) + 120))
                            hour = Utils.compareHours(H3, Variables.myThursdayDay.getH4());
                        else
                            hour = Utils.compareHours(H3, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (!this.getH4().equals("00:00")))
                    {
                        minutesToWork += thursdayAfternoonMinute;
                        hour = Utils.compareHours(H3, H4);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    break;
                case "ven" :
                    if((!this.getH1().equals("00:00")) && (this.getH2().equals("00:00")))
                    {
                        minutesToWork += fridayMorningMinute;
                        if((Utils.compareTime(H1, currentDay.getH1())) > (Utils.compareTime(H1, Variables.myFridayDay.getH2()) + 120))
                            hour = Utils.compareHours(H1, Variables.myFridayDay.getH2());
                        else
                            hour = Utils.compareHours(H1, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH1().equals("00:00")) && (!this.getH2().equals("00:00")))
                    {
                        minutesToWork += fridayMorningMinute;
                        hour = Utils.compareHours(H1, H2);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (this.getH4().equals("00:00")))
                    {
                        minutesToWork += fridayAfternoonMinute;
                        if((Utils.compareTime(H3, currentDay.getH1())) > (Utils.compareTime(H3, Variables.myFridayDay.getH4()) + 120))
                            hour = Utils.compareHours(H3, Variables.myFridayDay.getH4());
                        else
                            hour = Utils.compareHours(H3, currentDay.getH1());
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    if((!this.getH3().equals("00:00")) && (!this.getH4().equals("00:00")))
                    {
                        minutesToWork += fridayAfternoonMinute;
                        hour = Utils.compareHours(H3, H4);
                        minuteDay += hour.hour * 60 + hour.minute;
                    }
                    break;
            }
        }
        int difference = minuteDay - minutesToWork;
        Hour h = new Hour();
        h.hour = difference / 60;
        h.minute = difference % 60;
        return h;
    }

    @Override
    public String toString()
    {
        if(this.type.equals("Travail")) return dayWeek + " " + day + "\n" + H1 + " " + H2 + " " + H3 + " " + H4;
        if((!this.type.equals("Travail")) && (this.time.equals("matin"))) return dayWeek + " " + day + "\n" + this.type + " " + this.type + " " + H3 + " " + H4;
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) return dayWeek + " " + day + "\n"  + H1 + " " + H2 + " " + this.type + " " + this.type;
        return dayWeek + " " + day + "\n" + this.type;
    }

    public Spanned getString()
    {
        Spannable start = new SpannableString(dayWeek + " " + day + "\n" + H1 + " ");
        Spannable wordToSpan = new SpannableString(H2 + " " + H3);
        wordToSpan.setSpan(new ForegroundColorSpan(Color.rgb(0, 91, 150)), 0, wordToSpan.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Spannable end = new SpannableString(" " + H4);
        return (Spanned) TextUtils.concat(start, wordToSpan, end);
    }

    public String getExport()
    {
        if(this.type.equals("Travail")) return dayWeek + " " + day + " : " + H1 + " à " + H2 + " - " + H3 + " à " + H4;
        if((!this.type.equals("Travail")) && (this.time.equals("matin"))) return dayWeek + " " + day + " : " + this.type + " - " + H3 + " à " + H4;
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) return dayWeek + " " + day + " : " + H1 + " à " + H2 + " - " +this.type;
        return dayWeek + " " + day + " : " + this.type;
    }

    public String getHoliday()
    {
        if((!this.type.equals("Travail")) && (this.time.equals("matin"))) return dayWeek + " " + day + " " + this.type + " : M";
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) return dayWeek + " " + day + " " + this.type + " : AP";
        return dayWeek + " " + day + " " + this.type;
    }

    public String getHolidayExport()
    {
        if(!this.type.equals("Travail"))
        {
            if(this.time.equals("journée"))
            {
                if(this.type.equals("CP")) Holiday.myCP += 1;
                if(this.type.equals("RTT")) Holiday.myRTT += 1;
            }
            if((this.time.equals("matin")) || (this.time.equals("après-midi")))
            {
                if(this.type.equals("CP")) Holiday.myCP += 0.5;
                if(this.type.equals("RTT")) Holiday.myRTT += 0.5;
            }
        }
        if(this.type.equals("Férié")) Holiday.myFerie += 1;

        if((!this.type.equals("Travail")) && (this.time.equals("matin"))) return dayWeek + " " + day + " " + this.type + " : matin";
        if((!this.type.equals("Travail")) && (this.time.equals("après-midi"))) return dayWeek + " " + day + " " + this.type + " : après-midi";
        return dayWeek + " " + day + " " + this.type + " : journée";
    }
}
