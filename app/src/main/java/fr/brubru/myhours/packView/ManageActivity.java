package fr.brubru.myhours.packView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.CustomDialog;
import fr.brubru.myhours.packModel.CustomTimePickerDialog;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.ExceptionHandler;
import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;

public class ManageActivity extends ActionBarActivity
{
    public static View.OnClickListener myClickEvent;
    private Calendar myCalendar, myH1Calendar, myH2Calendar, myH3Calendar, myH4Calendar;
    private TextView eDay;
    private TextView H1Monday, H2Monday, H3Monday, H4Monday;
    private TextView H1Tuesday, H2Tuesday, H3Tuesday, H4Tuesday;
    private TextView H1Wednesday, H2Wednesday, H3Wednesday, H4Wednesday;
    private TextView H1Thursday, H2Thursday, H3Thursday, H4Thursday;
    private TextView H1Friday, H2Friday, H3Friday, H4Friday;
    private TextView eH1, eH2, eH3, eH4;
    private EditText launchMinutes, nbHours;
    //private EditText pause1Minutes, pause2Minutes;
    private TimePickerDialog.OnTimeSetListener myTimeSetListener;
    private CustomTimePickerDialog myTimePickerDialog;
    private Day myMondayDay, myTuesdayDay, myWednesdayDay, myThursdayDay, myFridayDay;
    private String isLaunchAutoSet, myLaunchMinutes, myNbHours;
    //private String isPause1AutoSet, myPause1Minutes, isPause2AutoSet, myPause2Minutes;
    private Day myCurrentDay, myConfigDay;
    private boolean isStartH1, isStartH2, isStartH3, isStartH4;
    private CheckBox cbCP, cbRTT, cbCE, cbCSS, cbMaladie, cbFerie, launchAutoSet;
    //private CheckBox pause1AutoSet, pause2AutoSet;
    private CustomDialog myDialog;
    private String myTypeDay;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)) Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Variables.logDir));
        String type = "default";
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) type = b.getString("MANAGE_TYPE");
        if(type.equals("see"))
        {
            SeeDays();
        }
        if(type.equals("add"))
        {
            setContentView(R.layout.activity_manage_add);
            setTextAdd();
        }
        if(type.equals("drop"))
        {
           dropAllDays();
        }
        if(type.equals("settings"))
        {
            setContentView(R.layout.activity_manage_settings);
            setTextSettings();
        }
        if(type.equals("update"))
        {
            setContentView(R.layout.activity_manage_update);
            setStart(false);
            Day tmpDay = new Day();
            tmpDay.setId(b.getLong("id"));
            tmpDay.setDay(b.getString("day"));
            tmpDay.setH1(b.getString("H1"));
            tmpDay.setH2(b.getString("H2"));
            tmpDay.setH3(b.getString("H3"));
            tmpDay.setH4(b.getString("H4"));
            tmpDay.setType(b.getString("type"));
            tmpDay.setTime(b.getString("time"));
            setTextUpdate(tmpDay);
        }
        if(type.equals("delete"))
        {
            deleteDay(b.getLong("id"));
        }
    }

    private void SeeDays()
    {
        DaysFragment.seeList();
        DaysFragment.updateDays("see");
        this.finish();
    }

    public void setStart(boolean value)
    {
        isStartH1 = value;
        isStartH2 = value;
        isStartH3 = value;
        isStartH4 = value;
    }

    // *** Supprimer un pointage (type = delete) *** \\
    public void deleteDay(long id)
    {
        DataBaseHelper myDataBaseHelper = new DataBaseHelper(this, "day");
        boolean isOK = myDataBaseHelper.deleteDay(id);
        myDataBaseHelper.closeDB();
        DaysFragment.updateDays("delete");
        if(isOK)
            Toast.makeText(ManageActivity.this, "Le pointage a été bien supprimé.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ManageActivity.this, "La suppression a rencontré un problème.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    // *** Supprimer tous les pointages (type = drop) *** \\
    public void dropAllDays()
    {
        DataBaseHelper myDataBaseHelper = new DataBaseHelper(this, "day");
        boolean isOK = myDataBaseHelper.dropDay();
        myDataBaseHelper.closeDB();
        DaysFragment.updateDays("drop");
        if(isOK)
            Toast.makeText(ManageActivity.this, "Tous les pointages ont été supprimés.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ManageActivity.this, "La suppression a rencontré un problème.", Toast.LENGTH_SHORT).show();
        this.finish();

    }

    // *** Changer les paramètres (type = settings) *** \\
    public void setTextSettings()
    {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "config");
        myMondayDay = db.getDayByDay("01/99/9999");
        myTuesdayDay = db.getDayByDay("02/99/9999");
        myWednesdayDay = db.getDayByDay("03/99/9999");
        myThursdayDay = db.getDayByDay("04/99/9999");
        myFridayDay = db.getDayByDay("05/99/9999");
        db.closeDB();
        db = new DataBaseHelper(getApplicationContext(), "settings");
        /*isPause1AutoSet = db.getSetting("isPause1AutoSet");
        myPause1Minutes = db.getSetting("pause1Minutes");

        isPause2AutoSet = db.getSetting("isPause2AutoSet");
        myPause2Minutes = db.getSetting("p cause2Minutes");
        */
        isLaunchAutoSet = db.getSetting("isLaunchAutoSet");
        myLaunchMinutes = db.getSetting("launchMinutes");
        myNbHours = db.getSetting("nbHours");
        db.closeDB();

        myCalendar = Calendar.getInstance();
        H1Monday = (TextView) findViewById(R.id.H1Monday);
        H2Monday = (TextView) findViewById(R.id.H2Monday);
        H3Monday = (TextView) findViewById(R.id.H3Monday);
        H4Monday = (TextView) findViewById(R.id.H4Monday);

        H1Tuesday = (TextView) findViewById(R.id.H1Tuesday);
        H2Tuesday = (TextView) findViewById(R.id.H2Tuesday);
        H3Tuesday = (TextView) findViewById(R.id.H3Tuesday);
        H4Tuesday = (TextView) findViewById(R.id.H4Tuesday);

        H1Wednesday = (TextView) findViewById(R.id.H1Wednesday);
        H2Wednesday = (TextView) findViewById(R.id.H2Wednesday);
        H3Wednesday = (TextView) findViewById(R.id.H3Wednesday);
        H4Wednesday = (TextView) findViewById(R.id.H4Wednesday);

        H1Thursday = (TextView) findViewById(R.id.H1Thursday);
        H2Thursday = (TextView) findViewById(R.id.H2Thursday);
        H3Thursday = (TextView) findViewById(R.id.H3Thursday);
        H4Thursday = (TextView) findViewById(R.id.H4Thursday);

        H1Friday = (TextView) findViewById(R.id.H1Friday);
        H2Friday = (TextView) findViewById(R.id.H2Friday);
        H3Friday = (TextView) findViewById(R.id.H3Friday);
        H4Friday = (TextView) findViewById(R.id.H4Friday);
        /*
        pause1AutoSet = (CheckBox) findViewById(R.id.pause1AutoSet);
        pause1Minutes = (EditText) findViewById(R.id.pause1Minutes);

        pause2AutoSet = (CheckBox) findViewById(R.id.pause2AutoSet);
        pause2Minutes = (EditText) findViewById(R.id.pause2Minutes);

        if(isPause1AutoSet != null)
        {
            pause1AutoSet.setChecked(false);
            if(isPause1AutoSet.equals("true")) pause1AutoSet.setChecked(true);
        }
        if(myPause1Minutes != null) pause1Minutes.setText(myPause1Minutes);
        else
            pause1Minutes.setText(Variables.myPause1Minutes);

        if(isPause2AutoSet != null)
        {
            pause2AutoSet.setChecked(false);
            if(isPause2AutoSet.equals("true")) pause2AutoSet.setChecked(true);
        }
        if(myPause2Minutes != null) pause2Minutes.setText(myPause2Minutes);
        else
            pause2Minutes.setText(Variables.myPause2Minutes);
        */

        launchAutoSet = (CheckBox) findViewById(R.id.launchAutoSet);
        launchMinutes = (EditText) findViewById(R.id.launchMinutes);
        nbHours = (EditText) findViewById(R.id.nbHours);

        if(isLaunchAutoSet != null)
        {
            launchAutoSet.setChecked(false);
            if(isLaunchAutoSet.equals("true")) launchAutoSet.setChecked(true);
        }
        if(myLaunchMinutes != null) launchMinutes.setText(myLaunchMinutes);
        else
            launchMinutes.setText(Variables.myLaunchMinutes);

        if(myNbHours != null) nbHours.setText(myNbHours);
        else
            nbHours.setText(Variables.myNbHours);

        if(myMondayDay != null)
        {
            H1Monday.setText(myMondayDay.getH1());
            H2Monday.setText(myMondayDay.getH2());
            H3Monday.setText(myMondayDay.getH3());
            H4Monday.setText(myMondayDay.getH4());
        }
        if(myTuesdayDay != null)
        {
            H1Tuesday.setText(myTuesdayDay.getH1());
            H2Tuesday.setText(myTuesdayDay.getH2());
            H3Tuesday.setText(myTuesdayDay.getH3());
            H4Tuesday.setText(myTuesdayDay.getH4());
        }
        if(myWednesdayDay != null)
        {
            H1Wednesday.setText(myWednesdayDay.getH1());
            H2Wednesday.setText(myWednesdayDay.getH2());
            H3Wednesday.setText(myWednesdayDay.getH3());
            H4Wednesday.setText(myWednesdayDay.getH4());
        }
        if(myThursdayDay != null)
        {
            H1Thursday.setText(myThursdayDay.getH1());
            H2Thursday.setText(myThursdayDay.getH2());
            H3Thursday.setText(myThursdayDay.getH3());
            H4Thursday.setText(myThursdayDay.getH4());
        }
        if(myFridayDay != null)
        {
            H1Friday.setText(myFridayDay.getH1());
            H2Friday.setText(myFridayDay.getH2());
            H3Friday.setText(myFridayDay.getH3());
            H4Friday.setText(myFridayDay.getH4());
        }

        myTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker v, int hour, int minute)
            {
                if(myTimePickerDialog.getId() == H1Monday.getId()) H1Monday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H2Monday.getId()) H2Monday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H3Monday.getId()) H3Monday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H4Monday.getId()) H4Monday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));

                if(myTimePickerDialog.getId() == H1Tuesday.getId()) H1Tuesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H2Tuesday.getId()) H2Tuesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H3Tuesday.getId()) H3Tuesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H4Tuesday.getId()) H4Tuesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));

                if(myTimePickerDialog.getId() == H1Wednesday.getId()) H1Wednesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H2Wednesday.getId()) H2Wednesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H3Wednesday.getId()) H3Wednesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H4Wednesday.getId()) H4Wednesday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));

                if(myTimePickerDialog.getId() == H1Thursday.getId()) H1Thursday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H2Thursday.getId()) H2Thursday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H3Thursday.getId()) H3Thursday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H4Thursday.getId()) H4Thursday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));

                if(myTimePickerDialog.getId() == H1Friday.getId()) H1Friday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H2Friday.getId()) H2Friday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H3Friday.getId()) H3Friday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == H4Friday.getId()) H4Friday.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
            }
        };
    }

    public void setTimeMonday(View v)
    {
        myCalendar = Calendar.getInstance();
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == H1Monday.getId())
        {
            title = "Choisir une heure (matin)";
            if(myMondayDay != null)
            {
                if(myMondayDay.getH1() != null) data = myMondayDay.getH1().split(":");
            }
            id = H1Monday.getId();
        }
        if(v.getId() == H2Monday.getId())
        {
            title = "Choisir une heure (midi)";
            if(myMondayDay != null)
            {
                if(myMondayDay.getH2() != null) data = myMondayDay.getH2().split(":");
            }
            id = H2Monday.getId();
        }
        if(v.getId() == H3Monday.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myMondayDay != null)
            {
                if(myMondayDay.getH3() != null) data = myMondayDay.getH3().split(":");
            }
            id = H3Monday.getId();
        }
        if(v.getId() == H4Monday.getId())
        {
            title = "Choisir une heure (soir)";
            if(myMondayDay != null)
            {
                if(myMondayDay.getH4() != null) data = myMondayDay.getH4().split(":");
            }
            id = H4Monday.getId();
        }
        if((myMondayDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, hour, minute, true);
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }

    public void setTimeTuesday(View v)
    {
        myCalendar = Calendar.getInstance();
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == H1Tuesday.getId())
        {
            title = "Choisir une heure (matin)";
            if(myTuesdayDay != null)
            {
                if(myTuesdayDay.getH1() != null) data = myTuesdayDay.getH1().split(":");
            }
            id = H1Tuesday.getId();
        }
        if(v.getId() == H2Tuesday.getId())
        {
            title = "Choisir une heure (midi)";
            if(myTuesdayDay != null)
            {
                if(myTuesdayDay.getH2() != null) data = myTuesdayDay.getH2().split(":");
            }
            id = H2Tuesday.getId();
        }
        if(v.getId() == H3Tuesday.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myTuesdayDay != null)
            {
                if(myTuesdayDay.getH3() != null) data = myTuesdayDay.getH3().split(":");
            }
            id = H3Tuesday.getId();
        }
        if(v.getId() == H4Tuesday.getId())
        {
            title = "Choisir une heure (soir)";
            if(myTuesdayDay != null)
            {
                if(myTuesdayDay.getH4() != null) data = myTuesdayDay.getH4().split(":");
            }
            id = H4Tuesday.getId();
        }
        if((myTuesdayDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, hour, minute, true);
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }

    public void setTimeWednesday(View v)
    {
        myCalendar = Calendar.getInstance();
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == H1Wednesday.getId())
        {
            title = "Choisir une heure (matin)";
            if(myWednesdayDay != null)
            {
                if(myWednesdayDay.getH1() != null) data = myWednesdayDay.getH1().split(":");
            }
            id = H1Wednesday.getId();
        }
        if(v.getId() == H2Wednesday.getId())
        {
            title = "Choisir une heure (midi)";
            if(myWednesdayDay != null)
            {
                if(myWednesdayDay.getH2() != null) data = myWednesdayDay.getH2().split(":");
            }
            id = H2Wednesday.getId();
        }
        if(v.getId() == H3Wednesday.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myWednesdayDay != null)
            {
                if(myWednesdayDay.getH3() != null) data = myWednesdayDay.getH3().split(":");
            }
            id = H3Wednesday.getId();
        }
        if(v.getId() == H4Wednesday.getId())
        {
            title = "Choisir une heure (soir)";
            if(myWednesdayDay != null)
            {
                if(myWednesdayDay.getH4() != null) data = myWednesdayDay.getH4().split(":");
            }
            id = H4Wednesday.getId();
        }
        if((myWednesdayDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, hour, minute, true);
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }

    public void setTimeThursday(View v)
    {
        myCalendar = Calendar.getInstance();
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == H1Thursday.getId())
        {
            title = "Choisir une heure (matin)";
            if(myThursdayDay != null)
            {
                if(myThursdayDay.getH1() != null) data = myThursdayDay.getH1().split(":");
            }
            id = H1Thursday.getId();
        }
        if(v.getId() == H2Thursday.getId())
        {
            title = "Choisir une heure (midi)";
            if(myThursdayDay != null)
            {
                if(myThursdayDay.getH2() != null) data = myThursdayDay.getH2().split(":");
            }
            id = H2Thursday.getId();
        }
        if(v.getId() == H3Thursday.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myThursdayDay != null)
            {
                if(myThursdayDay.getH3() != null) data = myThursdayDay.getH3().split(":");
            }
            id = H3Thursday.getId();
        }
        if(v.getId() == H4Thursday.getId())
        {
            title = "Choisir une heure (soir)";
            if(myThursdayDay != null)
            {
                if(myThursdayDay.getH4() != null) data = myThursdayDay.getH4().split(":");
            }
            id = H4Thursday.getId();
        }
        if((myThursdayDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, hour, minute, true);
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }

    public void setTimeFriday(View v)
    {
        myCalendar = Calendar.getInstance();
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == H1Friday.getId())
        {
            title = "Choisir une heure (matin)";
            if(myFridayDay != null)
            {
                if(myTuesdayDay.getH1() != null) data = myFridayDay.getH1().split(":");
            }
            id = H1Friday.getId();
        }
        if(v.getId() == H2Friday.getId())
        {
            title = "Choisir une heure (midi)";
            if(myFridayDay != null)
            {
                if(myFridayDay.getH2() != null) data = myFridayDay.getH2().split(":");
            }
            id = H2Friday.getId();
        }
        if(v.getId() == H3Friday.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myFridayDay != null)
            {
                if(myFridayDay.getH3() != null) data = myFridayDay.getH3().split(":");
            }
            id = H3Friday.getId();
        }
        if(v.getId() == H4Friday.getId())
        {
            title = "Choisir une heure (soir)";
            if(myFridayDay != null)
            {
                if(myFridayDay.getH4() != null) data = myFridayDay.getH4().split(":");
            }
            id = H4Friday.getId();
        }
        if((myFridayDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, hour, minute, true);
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }

    public void changeSettings(View v)
    {
        long pid;
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "config");
        //-- Monday --\\
        boolean existsMonday = db.checkDayExists("01/99/9999");
        Day mondayDay = new Day("01/99/9999", H1Monday.getText().toString(), H2Monday.getText().toString(), H3Monday.getText().toString(), H4Monday.getText().toString());
        if(!existsMonday)
        {
            pid = db.insertDay(mondayDay);
            long id = db.getMaxId();
            mondayDay.setId(id);
            Variables.myMondayDay = mondayDay;
        }
        else
        {
            Day day = db.getDayByDay("01/99/9999");
            pid = day.getId();
            mondayDay.setId(pid);
            db.updateDay(mondayDay);
            Variables.myMondayDay = mondayDay;
        }
        //-- Tuesday --\\
        boolean existsTuesday = db.checkDayExists("02/99/9999");
        Day tuesdayDay = new Day("02/99/9999", H1Tuesday.getText().toString(), H2Tuesday.getText().toString(), H3Tuesday.getText().toString(), H4Tuesday.getText().toString());
        if(!existsTuesday)
        {
            pid = db.insertDay(tuesdayDay);
            long id = db.getMaxId();
            tuesdayDay.setId(id);
            Variables.myTuesdayDay = tuesdayDay;
        }
        else
        {
            Day day = db.getDayByDay("02/99/9999");
            pid = day.getId();
            tuesdayDay.setId(pid);
            db.updateDay(tuesdayDay);
            Variables.myTuesdayDay = tuesdayDay;
        }
        //-- Wednesday --\\
        boolean existsWednesday = db.checkDayExists("03/99/9999");
        Day wednesdayDay = new Day("03/99/9999", H1Wednesday.getText().toString(), H2Wednesday.getText().toString(), H3Wednesday.getText().toString(), H4Wednesday.getText().toString());
        if(!existsWednesday)
        {
            pid = db.insertDay(wednesdayDay);
            long id = db.getMaxId();
            wednesdayDay.setId(id);
            Variables.myWednesdayDay = wednesdayDay;
        }
        else
        {
            Day day = db.getDayByDay("03/99/9999");
            pid = day.getId();
            wednesdayDay.setId(pid);
            db.updateDay(wednesdayDay);
            Variables.myWednesdayDay = wednesdayDay;
        }
        //-- Thursday --\\
        boolean existsThursday = db.checkDayExists("04/99/9999");
        Day thursdayDay = new Day("04/99/9999", H1Thursday.getText().toString(), H2Thursday.getText().toString(), H3Thursday.getText().toString(), H4Thursday.getText().toString());
        if(!existsThursday)
        {
            pid = db.insertDay(thursdayDay);
            long id = db.getMaxId();
            thursdayDay.setId(id);
            Variables.myThursdayDay = thursdayDay;
        }
        else
        {
            Day day = db.getDayByDay("04/99/9999");
            pid = day.getId();
            thursdayDay.setId(pid);
            db.updateDay(thursdayDay);
            Variables.myThursdayDay = thursdayDay;
        }
        //-- Friday --\\
        boolean existsFriday = db.checkDayExists("05/99/9999");
        Day fridayDay = new Day("05/99/9999", H1Friday.getText().toString(), H2Friday.getText().toString(), H3Friday.getText().toString(), H4Friday.getText().toString());
        if(!existsFriday)
        {
            pid = db.insertDay(fridayDay);
            long id = db.getMaxId();
            fridayDay.setId(id);
            Variables.myFridayDay = fridayDay;
        }
        else
        {
            Day day = db.getDayByDay("05/99/9999");
            pid = day.getId();
            fridayDay.setId(pid);
            db.updateDay(fridayDay);
            Variables.myFridayDay = fridayDay;
        }
        db.closeDB();
        db = new DataBaseHelper(getApplicationContext(), "settings");
        //-- Cumul du matin automatiquement modifiée si pause1 activée (déduction du temps de pause 1) --\\
        /*
        boolean existsIsPause1 = db.checkSettingExists("isPause1AutoSet");
        Variables.isPause1AutoSet = pause1AutoSet.isChecked();
        isPause1AutoSet = "false";
        if(Variables.isPause1AutoSet) isPause1AutoSet = "true";
        if(!existsIsPause1)
        {
            pid = db.insertSetting("isPause1AutoSet", isPause1AutoSet);
        }
        else
        {
            db.updateSetting("isPause1AutoSet", isPause1AutoSet);
        }
        //-- Durée de la pause 1 --\\
        boolean existsPause1Minutes = db.checkSettingExists("pause1Minutes");
        Variables.myPause1Minutes = pause1Minutes.getText().toString();
        myPause1Minutes = Variables.myPause1Minutes;
        if(!existsPause1Minutes)
        {
            pid = db.insertSetting("pause1Minutes", pause1Minutes.getText().toString());
        }
        else
        {
            db.updateSetting("pause1Minutes", pause1Minutes.getText().toString());
        }
        //-- Cumul du matin automatiquement modifiée si pause2 activée (déduction du temps de pause 2) --\\
        boolean existsIsPause2 = db.checkSettingExists("isPause2AutoSet");
        Variables.isPause2AutoSet = pause2AutoSet.isChecked();
        isPause2AutoSet = "false";
        if(Variables.isPause2AutoSet) isPause2AutoSet = "true";
        if(!existsIsPause2)
        {
            pid = db.insertSetting("isPause2AutoSet", isPause2AutoSet);
        }
        else
        {
            db.updateSetting("isPause2AutoSet", isPause2AutoSet);
        }
        //-- Durée de la pause 2 --\\
        boolean existsPause2Minutes = db.checkSettingExists("pause2Minutes");
        Variables.myPause2Minutes = pause2Minutes.getText().toString();
        myPause2Minutes = Variables.myPause2Minutes;
        if(!existsPause2Minutes)
        {
            pid = db.insertSetting("pause2Minutes", pause2Minutes.getText().toString());
        }
        else
        {
            db.updateSetting("pause2Minutes", pause2Minutes.getText().toString());
        }
        */
        //-- Pointage de la pause de midi automatiquement modifiée si la durée n'est pas correcte --\\
        boolean existsIsLaunch = db.checkSettingExists("isLaunchAutoSet");
        Variables.isLaunchAutoSet = launchAutoSet.isChecked();
        isLaunchAutoSet = "false";
        if(Variables.isLaunchAutoSet) isLaunchAutoSet = "true";
        if(!existsIsLaunch)
        {
            pid = db.insertSetting("isLaunchAutoSet", isLaunchAutoSet);
        }
        else
        {
            db.updateSetting("isLaunchAutoSet", isLaunchAutoSet);
        }
        //-- Durée de la pause de midi --\\
        boolean existsLaunchMinutes = db.checkSettingExists("launchMinutes");
        Variables.myLaunchMinutes = launchMinutes.getText().toString();
        myLaunchMinutes = Variables.myLaunchMinutes;
        if(!existsLaunchMinutes)
        {
            pid = db.insertSetting("launchMinutes", launchMinutes.getText().toString());
        }
        else
        {
            db.updateSetting("launchMinutes", launchMinutes.getText().toString());
        }
        //-- Nombre heures par semaine --\\
        boolean existsNbHours = db.checkSettingExists("nbHours");
        Variables.myNbHours = nbHours.getText().toString();
        myNbHours = Variables.myNbHours;
        if(!existsNbHours)
        {
            pid = db.insertSetting("nbHours", nbHours.getText().toString());
        }
        else
        {
            db.updateSetting("nbHours", nbHours.getText().toString());
        }
        db.closeDB();
        DaysFragment.updateDays("settings");
        if(pid > -1)
        {
            Toast.makeText(ManageActivity.this, "Horaires sauvegardés.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            Toast.makeText(ManageActivity.this, "La modification a rencontré a problème.", Toast.LENGTH_SHORT).show();
        }
    }

    // *** Modifier un pointage (type = update) *** \\
    public void setTextUpdate(Day day)
    {
        myClickEvent = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                dialogTypeClicked(v);
            }
        };
        myCurrentDay = day;
        final String myFormat = "dd/MM/yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        eDay = (TextView) findViewById(R.id.DayUpdate);
        eDay.setText(day.getDay());
        myCalendar = Calendar.getInstance();
        myCalendar.setTime(Utils.stringToDate(day.getDay()));
        final DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                eDay.setText(sdf.format(myCalendar.getTime()));
            }
        };
        eDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(ManageActivity.this, myDateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le jour");
                myDatePickerDialog.show();
            }
        });
        eH1 = (TextView) findViewById(R.id.H1Update);
        eH2 = (TextView) findViewById(R.id.H2Update);
        eH3 = (TextView) findViewById(R.id.H3Update);
        eH4 = (TextView) findViewById(R.id.H4Update);
        eH1.setText(day.getH1());
        eH2.setText(day.getH2());
        eH3.setText(day.getH3());
        eH4.setText(day.getH4());

        cbCP = (CheckBox) findViewById(R.id.cbCP);
        cbRTT = (CheckBox) findViewById(R.id.cbRTT);
        cbCE = (CheckBox) findViewById(R.id.cbCE);
        cbCSS = (CheckBox) findViewById(R.id.cbCSS);
        cbMaladie = (CheckBox) findViewById(R.id.cbMaladie);
        cbFerie = (CheckBox) findViewById(R.id.cbFerie);

        if(myCurrentDay.getType().equals("CP")) cbCP.setChecked(true);
        if(myCurrentDay.getType().equals("RTT")) cbRTT.setChecked(true);
        if(myCurrentDay.getType().equals("CE")) cbCE.setChecked(true);
        if(myCurrentDay.getType().equals("CSS")) cbCSS.setChecked(true);
        if(myCurrentDay.getType().equals("Maladie")) cbMaladie.setChecked(true);
        if(myCurrentDay.getType().equals("Férié")) cbFerie.setChecked(true);

		if(myCurrentDay.getType().equals("Travail"))
        {
			myTypeDay = "allDay";
		}
        if((!myCurrentDay.getType().equals("Travail")) && (myCurrentDay.getTime().equals("journée")))
        {
            eH1.setText("00:00");
            eH1.setEnabled(false);
            eH2.setText("00:00");
            eH2.setEnabled(false);
            eH3.setText("00:00");
            eH3.setEnabled(false);
            eH4.setText("00:00");
            eH4.setEnabled(false);
            myTypeDay = "allDay";
        }
        if((!myCurrentDay.getType().equals("Travail")) && (myCurrentDay.getTime().equals("matin")))
        {
            eH1.setText("00:00");
            eH1.setEnabled(false);
            eH2.setText("00:00");
            eH2.setEnabled(false);
            myTypeDay = "morningDay";
        }
        if((!myCurrentDay.getType().equals("Travail")) && (myCurrentDay.getTime().equals("après-midi")))
        {
            eH3.setText("00:00");
            eH3.setEnabled(false);
            eH4.setText("00:00");
            eH4.setEnabled(false);
            myTypeDay = "afternoonDay";
        }

        myH1Calendar = Calendar.getInstance();
        myH2Calendar = Calendar.getInstance();
        myH3Calendar = Calendar.getInstance();
        myH4Calendar = Calendar.getInstance();
        myTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker v, int hour, int minute)
            {
                if(myTimePickerDialog.getId() == eH1.getId())
                {
                    myH1Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH1Calendar.set(Calendar.MINUTE, minute);
                    eH1.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH2.getId())
                {
                    myH2Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH2Calendar.set(Calendar.MINUTE, minute);
                    eH2.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH3.getId())
                {
                    myH3Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH3Calendar.set(Calendar.MINUTE, minute);
                    eH3.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH4.getId())
                {
                    myH4Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH4Calendar.set(Calendar.MINUTE, minute);
                    eH4.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
            }
        };
    }

    public void setUpdateTime(View v)
    {
        myConfigDay = myCurrentDay;
        String title = "Choisir une heure";
        int id = 0;
        String[] data = null;
        if(v.getId() == eH1.getId())
        {
            title = "Choisir une heure (matin)";
            if(myConfigDay != null)
            {
                if(myConfigDay.getH1() != null) data = myConfigDay.getH1().split(":");
            }
            id = eH1.getId();
        }
        if(v.getId() == eH2.getId())
        {
            title = "Choisir une heure (midi)";
            if(myConfigDay != null)
            {
                if(myConfigDay.getH2() != null) data = myConfigDay.getH2().split(":");
            }
            id = eH2.getId();
        }
        if(v.getId() == eH3.getId())
        {
            title = "Choisir une heure (reprise)";
            if(myConfigDay != null)
            {
                if(myConfigDay.getH3() != null) data = myConfigDay.getH3().split(":");
            }
            id = eH3.getId();
        }
        if(v.getId() == eH4.getId())
        {
            title = "Choisir une heure (soir)";
            if(myConfigDay != null)
            {
                if(myConfigDay.getH4() != null) data = myConfigDay.getH4().split(":");
            }
            id = eH4.getId();
        }
        if((myConfigDay != null) && (data != null))
        {
            String h = data[0];
            String m = data[1];
            if(h.charAt(0) == '0') h = h.substring(1);
            if(m.charAt(0) == '0') m = m.substring(1);
            int hour = Integer.parseInt(h);
            int minute = Integer.parseInt(m);
            if((hour == 0) && (minute == 0))
            {
                if(id == eH1.getId())
                {
                    if(!isStartH1)
                    {
                        myH1Calendar = Calendar.getInstance();
                        isStartH1 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH1Calendar.get(Calendar.HOUR_OF_DAY), myH1Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH2.getId())
                {
                    if(!isStartH2)
                    {
                        myH2Calendar = Calendar.getInstance();
                        isStartH2 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH2Calendar.get(Calendar.HOUR_OF_DAY), myH2Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH3.getId())
                {
                    if(!isStartH3)
                    {
                        myH3Calendar = Calendar.getInstance();
                        isStartH3 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH3Calendar.get(Calendar.HOUR_OF_DAY), myH3Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH4.getId())
                {
                    if(!isStartH4)
                    {
                        myH4Calendar = Calendar.getInstance();
                        isStartH4 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH4Calendar.get(Calendar.HOUR_OF_DAY), myH4Calendar.get(Calendar.MINUTE), true);
                }
            }
            else
            {
                if(id == eH1.getId())
                {
                    System.out.println("H1 " + hour + "h " + minute + " mn");
                    if(!isStartH1)
                    {
                        myH1Calendar.set(Calendar.HOUR_OF_DAY, hour);
                        myH1Calendar.set(Calendar.MINUTE, minute);
                        isStartH1 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH1Calendar.get(Calendar.HOUR_OF_DAY), myH1Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH2.getId())
                {
                    System.out.println("H2 " + hour + "h " + minute + " mn");
                    if(!isStartH2)
                    {
                        myH2Calendar.set(Calendar.HOUR_OF_DAY, hour);
                        myH2Calendar.set(Calendar.MINUTE, minute);
                        isStartH2 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH2Calendar.get(Calendar.HOUR_OF_DAY), myH2Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH3.getId())
                {
                    System.out.println("H3 " + hour + "h " + minute + " mn");
                    if(!isStartH3)
                    {
                        myH3Calendar.set(Calendar.HOUR_OF_DAY, hour);
                        myH3Calendar.set(Calendar.MINUTE, minute);
                        isStartH3 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH3Calendar.get(Calendar.HOUR_OF_DAY), myH3Calendar.get(Calendar.MINUTE), true);
                }
                if(id == eH4.getId())
                {
                    System.out.println("H4 " + hour + "h " + minute + " mn");
                    if(!isStartH4)
                    {
                        myH4Calendar.set(Calendar.HOUR_OF_DAY, hour);
                        myH4Calendar.set(Calendar.MINUTE, minute);
                        isStartH4 = true;
                    }
                    myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH4Calendar.get(Calendar.HOUR_OF_DAY), myH4Calendar.get(Calendar.MINUTE), true);
                }
            }
        }
        else
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        myTimePickerDialog.setId(id);
        myTimePickerDialog.setTitle(title);
        myTimePickerDialog.show();
    }


    public void updateDay(View v)
    {
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "day");
        Day tmpDay = new Day(eDay.getText().toString(), eH1.getText().toString(), eH2.getText().toString(), eH3.getText().toString(), eH4.getText().toString());
        tmpDay.setId(myCurrentDay.getId());

        String type = "Travail";
        String time = "journée";
        if(cbCP.isChecked()) type = "CP";
        if(cbRTT.isChecked()) type = "RTT";
        if(cbCE.isChecked()) type = "CE";
        if(cbCSS.isChecked()) type = "CSS";
        if(cbMaladie.isChecked()) type = "Maladie";
        if(cbFerie.isChecked()) type = "Férié";
        if(myTypeDay.equals("allDay")) time = "journée";
        if(myTypeDay.equals("morningDay")) time = "matin";
        if(myTypeDay.equals("afternoonDay")) time = "après-midi";
        tmpDay.setTime(time);
        tmpDay.setType(type);
        boolean isOK = false;
        boolean exists = db.checkDayExists(eDay.getText().toString());
        if((exists && (eDay.getText().toString().equals(myCurrentDay.getDay()))) || (!exists))
        {
            db.updateDay(tmpDay);
            isOK = true;
        }
        db.closeDB();
        DaysFragment.updateDays("update");
        if(isOK)
        {
            Toast.makeText(ManageActivity.this, "Le pointage a bien été modifié.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            if(exists)
                Toast.makeText(ManageActivity.this, "Un pointage existe déjà pour ce jour.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ManageActivity.this, "La modification a rencontré a problème.", Toast.LENGTH_SHORT).show();
        }
    }

    public void typeDayClicked(View v)
    {
        CheckBox checkBox = (CheckBox) v;
        // CP
        if((v.getId() == cbCP.getId()) && (cbCP.isChecked()))
        {
            cbCE.setChecked(false);
            cbRTT.setChecked(false);
            cbCSS.setChecked(false);
            cbMaladie.setChecked(false);
            cbFerie.setChecked(false);
        }
        // RTT
        if((v.getId() == cbRTT.getId()) && (cbRTT.isChecked()))
        {
            cbCE.setChecked(false);
            cbCP.setChecked(false);
            cbCSS.setChecked(false);
            cbMaladie.setChecked(false);
            cbFerie.setChecked(false);
        }
        // CE
        if((v.getId() == cbCE.getId()) && (cbCE.isChecked()))
        {
            cbRTT.setChecked(false);
            cbCP.setChecked(false);
            cbCSS.setChecked(false);
            cbMaladie.setChecked(false);
            cbFerie.setChecked(false);
        }
        // CSS
        if((v.getId() == cbCSS.getId()) && (cbCSS.isChecked()))
        {
            cbRTT.setChecked(false);
            cbCP.setChecked(false);
            cbMaladie.setChecked(false);
            cbCE.setChecked(false);
            cbFerie.setChecked(false);
        }
        // Ferie
        if((v.getId() == cbFerie.getId()) && (cbFerie.isChecked()))
        {
            cbRTT.setChecked(false);
            cbCP.setChecked(false);
            cbCSS.setChecked(false);
            cbMaladie.setChecked(false);
            cbCE.setChecked(false);
        }
        // Maladie
        if((v.getId() == cbMaladie.getId()) && (cbMaladie.isChecked()))
        {
            cbRTT.setChecked(false);
            cbCP.setChecked(false);
            cbCSS.setChecked(false);
            cbCE.setChecked(false);
            cbFerie.setChecked(false);
        }
        if((v.getId() == cbCP.getId()) || (v.getId() == cbCE.getId()) || (v.getId() == cbRTT.getId()) || (v.getId() == cbCSS.getId()))
        {
            if(checkBox.isChecked())
            {
                showDialogTypeDay();
            }
        }
        else if((v.getId() == cbFerie.getId()) || (v.getId() == cbMaladie.getId()))
        {
            myTypeDay = "allDay";
            eH1.setText("00:00");
            eH1.setEnabled(false);
            eH2.setText("00:00");
            eH2.setEnabled(false);
            eH3.setText("00:00");
            eH3.setEnabled(false);
            eH4.setText("00:00");
            eH4.setEnabled(false);
        }
        if((!cbCP.isChecked()) && (!cbCE.isChecked()) && (!cbRTT.isChecked()) && (!cbCSS.isChecked()) && (!cbMaladie.isChecked()) && (!cbFerie.isChecked()))
        {
            eH1.setEnabled(true);
            eH2.setEnabled(true);
            eH3.setEnabled(true);
            eH4.setEnabled(true);
        }
    }

    public void showDialogTypeDay()
    {
        myDialog = new CustomDialog(ManageActivity.this);
        myDialog.setTitle("Veuillez choisir le type");
        myDialog.show();
    }

    public void dialogTypeClicked(View v)
    {
        if(v.getId() == R.id.btnDay)
        {
            eH1.setText("00:00");
            eH1.setEnabled(false);
            eH2.setText("00:00");
            eH2.setEnabled(false);
            eH3.setText("00:00");
            eH3.setEnabled(false);
            eH4.setText("00:00");
            eH4.setEnabled(false);
            myTypeDay = "allDay";
        }
        if(v.getId() == R.id.btnMorning)
        {
            eH1.setText("00:00");
            eH1.setEnabled(false);
            eH2.setText("00:00");
            eH2.setEnabled(false);
            eH3.setEnabled(true);
            eH4.setEnabled(true);
            myTypeDay = "morningDay";
        }
        if(v.getId() == R.id.btnAfternoon)
        {
            eH1.setEnabled(true);
            eH2.setEnabled(true);
            eH3.setText("00:00");
            eH3.setEnabled(false);
            eH4.setText("00:00");
            eH4.setEnabled(false);
            myTypeDay = "afternoonDay";
        }
        myDialog.dismiss();
    }

    // *** Ajouter un pointage (type = add) *** \\
    public void setTextAdd()
    {
        myClickEvent = new View.OnClickListener()
        {
            public void onClick(View v)
            {
                dialogTypeClicked(v);
            }
        };
        myTypeDay = "allDay";
        myCalendar = Calendar.getInstance();
        // DatePickerDialog au click sur la date pour choisir le jour
        eDay = (TextView) findViewById(R.id.DayAdd);
        final String myFormat = "dd/MM/yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        eDay.setText(sdf.format(myCalendar.getTime()));
        final DatePickerDialog.OnDateSetListener myDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                eDay.setText(sdf.format(myCalendar.getTime()));
            }
        };
        eDay.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(ManageActivity.this, myDateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le jour");
                myDatePickerDialog.show();
            }
        });
        // TimePickerDialog au click sur l'heure pour choisir l'heure de pointage
        myH1Calendar = Calendar.getInstance();
        myH2Calendar = Calendar.getInstance();
        myH3Calendar = Calendar.getInstance();
        myH4Calendar = Calendar.getInstance();
        eH1 = (TextView) findViewById(R.id.H1Add);
        eH2 = (TextView) findViewById(R.id.H2Add);
        eH3 = (TextView) findViewById(R.id.H3Add);
        eH4 = (TextView) findViewById(R.id.H4Add);

        cbCP = (CheckBox) findViewById(R.id.cbCP);
        cbRTT = (CheckBox) findViewById(R.id.cbRTT);
        cbCE = (CheckBox) findViewById(R.id.cbCE);
        cbCSS = (CheckBox) findViewById(R.id.cbCSS);
        cbCSS = (CheckBox) findViewById(R.id.cbCSS);
        cbMaladie = (CheckBox) findViewById(R.id.cbMaladie);
        cbFerie = (CheckBox) findViewById(R.id.cbFerie);

        myTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker v, int hour, int minute)
            {
                if(myTimePickerDialog.getId() == eH1.getId())
                {
                    myH1Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH1Calendar.set(Calendar.MINUTE, minute);
                    eH1.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH2.getId())
                {
                    myH2Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH2Calendar.set(Calendar.MINUTE, minute);
                    eH2.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH3.getId())
                {
                    myH3Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH3Calendar.set(Calendar.MINUTE, minute);
                    eH3.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
                if(myTimePickerDialog.getId() == eH4.getId())
                {
                    myH4Calendar.set(Calendar.HOUR_OF_DAY, hour);
                    myH4Calendar.set(Calendar.MINUTE, minute);
                    eH4.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                }
            }
        };
    }

    public void setTime(View v)
    {
        if(v.getId() == eH1.getId())
        {
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH1Calendar.get(Calendar.HOUR_OF_DAY), myH1Calendar.get(Calendar.MINUTE), true);
            myTimePickerDialog.setTitle("Choisir une heure (matin)");
            myTimePickerDialog.setId(eH1.getId());
        }
        if(v.getId() == eH2.getId())
        {
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH2Calendar.get(Calendar.HOUR_OF_DAY), myH2Calendar.get(Calendar.MINUTE), true);
            myTimePickerDialog.setTitle("Choisir une heure (midi)");
            myTimePickerDialog.setId(eH2.getId());
        }
        if(v.getId() == eH3.getId())
        {
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH3Calendar.get(Calendar.HOUR_OF_DAY), myH3Calendar.get(Calendar.MINUTE), true);
            myTimePickerDialog.setTitle("Choisir une heure (reprise)");
            myTimePickerDialog.setId(eH3.getId());
        }
        if(v.getId() == eH4.getId())
        {
            myTimePickerDialog = new CustomTimePickerDialog(ManageActivity.this, myTimeSetListener, myH4Calendar.get(Calendar.HOUR_OF_DAY), myH4Calendar.get(Calendar.MINUTE), true);
            myTimePickerDialog.setTitle("Choisir une heure (soir)");
            myTimePickerDialog.setId(eH4.getId());
        }
        myTimePickerDialog.show();
    }

    public void addDay(View v)
    {
        // TODO check date <= now
        String day = eDay.getText().toString();
        String h1 = eH1.getText().toString();
        String h2 = eH2.getText().toString();
        String h3 = eH3.getText().toString();
        String h4 = eH4.getText().toString();

        String type = "Travail";
        String time = "journée";

        if(cbCP.isChecked()) type = "CP";
        if(cbRTT.isChecked()) type = "RTT";
        if(cbCE.isChecked()) type = "CE";
        if(cbCSS.isChecked()) type = "CSS";
        if(cbMaladie.isChecked()) type = "Maladie";
        if(cbFerie.isChecked()) type = "Férié";
        if(myTypeDay.equals("allDay")) time = "journée";
        if(myTypeDay.equals("morningDay")) time = "matin";
        if(myTypeDay.equals("afternoonDay")) time = "après-midi";

        long pid = -2;
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "day");
        // VERIFICATION POINTAGE NON EXISTANT POUR CE JOUR
        boolean exists = db.checkDayExists(day);
        if(!exists)
        {
            Day dAdd = new Day(day, h1, h2, h3, h4);
            dAdd.setType(type);
            dAdd.setTime(time);
            pid = db.insertDay(dAdd);
            long id = db.getMaxId();
            dAdd.setId(id);
        }
        db.closeDB();
        DaysFragment.updateDays("add");
        if(pid > -1)
        {
            Toast.makeText(ManageActivity.this, "Le pointage a bien été enregistré.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            if(exists)
                Toast.makeText(ManageActivity.this, "Le pointage a déjà été renseigné pour ce jour.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ManageActivity.this, "L'ajout a rencontré un problème.", Toast.LENGTH_SHORT).show();
        }
    }
}
