package fr.brubru.myhours.packView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.CustomTimePickerDialog;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packService.MyService;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Utils;

public class ManageActivity extends ActionBarActivity
{
    private Calendar myCalendar, myH1Calendar, myH2Calendar, myH3Calendar, myH4Calendar;
    private TextView eDay;
    private TextView eH1;
    private TextView eH2;
    private TextView eH3;
    private TextView eH4;
    private TimePickerDialog.OnTimeSetListener myTimeSetListener;
    private CustomTimePickerDialog myTimePickerDialog;
    private Day myConfigDay;
    private Day myCurrentDay;
    private boolean isStartH1, isStartH2, isStartH3, isStartH4;
    private Switch mySwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        String type = "default";
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        if(b != null) type = b.getString("MANAGE_TYPE");
        if(type.equals("add"))
        {
            setContentView(R.layout.activity_manage_add);
            setTextAdd();
        }
        if(type.equals("drop"))
        {
            dropTable();
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
            setTextUpdate(tmpDay);
        }
        if(type.equals("delete"))
        {
            deleteDay(b.getLong("id"));
        }
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
        DaysFragment.getUpdate();
        if(isOK)
            Toast.makeText(ManageActivity.this, "Le pointage a été bien supprimé.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(ManageActivity.this, "La suppression a rencontré un problème.", Toast.LENGTH_SHORT).show();
        this.finish();
    }

    // *** Supprimer tous les pointages (type = drop) *** \\
    public void dropTable()
    {
        DataBaseHelper myDataBaseHelper = new DataBaseHelper(this, "day");
        boolean isOK = myDataBaseHelper.dropDay();
        myDataBaseHelper.closeDB();
        DaysFragment.getUpdate();
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
        myConfigDay = db.getDayByDay("99/99/9999");
        db.closeDB();        
        myCalendar = Calendar.getInstance();
        // TimePickerDialog au click sur l'heure pour choisir l'heure de pointage
        eH1 = (TextView) findViewById(R.id.H1Settings);
        eH2 = (TextView) findViewById(R.id.H2Settings);
        eH3 = (TextView) findViewById(R.id.H3Settings);
        eH4 = (TextView) findViewById(R.id.H4Settings);
        mySwitch = (Switch) findViewById(R.id.switchNotif);

        if(myConfigDay != null)
        {
            eH1.setText(myConfigDay.getH1());
            eH2.setText(myConfigDay.getH2());
            eH3.setText(myConfigDay.getH3());
            eH4.setText(myConfigDay.getH4());
        }
        myTimeSetListener = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker v, int hour, int minute)
            {
                if(myTimePickerDialog.getId() == eH1.getId()) eH1.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == eH2.getId()) eH2.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == eH3.getId()) eH3.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
                if(myTimePickerDialog.getId() == eH4.getId()) eH4.setText(Utils.pad(hour) + ":" + Utils.pad(minute));
            }
        };

        db = new DataBaseHelper(getApplicationContext(), "settings");
        String service = db.getSetting("service");
        db.closeDB();
        if(service != null)
        {
            if(service.equals("true")) MyService.isEnabled = true;
            if(service.equals("false")) MyService.isEnabled = false;
            mySwitch.setChecked(MyService.isEnabled);
        }

    }

    public void setConfigurationTime(View v)
    {
        myCalendar = Calendar.getInstance();
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
        boolean exists = db.checkDayExists("99/99/9999");
        Day tmpDay = new Day("99/99/9999", eH1.getText().toString(), eH2.getText().toString(), eH3.getText().toString(), eH4.getText().toString());
        if(!exists)
        {
            pid = db.insertDay(tmpDay);
            long id = db.getMaxId();
            tmpDay.setId(id);
        }
        else
        {
            Day day = db.getDayByDay("99/99/9999");
            pid = day.getId();
            tmpDay.setId(pid);
            db.updateDay(tmpDay);
        }
        MyService.myConfigDay = tmpDay;
        db.closeDB();
        db = new DataBaseHelper(getApplicationContext(), "settings");
        exists = db.checkSettingExists("service");
        String value = "false";
        if(mySwitch.isChecked()) value = "true";
        if(!exists)
        {
            pid = db.insertSetting("service", value);
        }
        else
        {
            db.updateSetting("service", value);
        }
        MyService.isEnabled = mySwitch.isChecked();
        db.closeDB();
        if(pid > -1)
        {
            Toast.makeText(ManageActivity.this, "Configuration sauvegardée.", Toast.LENGTH_SHORT).show();
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
        boolean isOK = false;
        boolean exists = db.checkDayExists(eDay.getText().toString());
        if((exists && (eDay.getText().toString().equals(myCurrentDay.getDay()))) || (!exists))
        {
            db.updateDay(tmpDay);
            isOK = true;
        }
        db.closeDB();
        DaysFragment.getUpdate();
        if(isOK)
        {
            Toast.makeText(ManageActivity.this, "Le pointage a bien été modifié.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            if(exists)
                Toast.makeText(ManageActivity.this, "Un pointage avec cette date est exite déjà.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ManageActivity.this, "La modification a rencontré a problème.", Toast.LENGTH_SHORT).show();
        }
    }

    // *** Ajouter un pointage (type = add) *** \\
    public void setTextAdd()
    {
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
        long pid = -2;
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "day");
        // VERIFICATION POINTAGE NON EXISTANT POUR CE JOUR
        boolean exists = db.checkDayExists(day);
        if(!exists)
        {
            Day dAdd = new Day(day, h1, h2, h3, h4);
            pid = db.insertDay(dAdd);
            long id = db.getMaxId();
            dAdd.setId(id);
        }
        db.closeDB();
        DaysFragment.getUpdate();
        if(pid > -1)
        {
            Toast.makeText(ManageActivity.this, "Le pointage a bien été enregistré.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
        else
        {
            if(exists)
                Toast.makeText(ManageActivity.this, "La pointage a déjà été renseigné pour ce jour.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ManageActivity.this, "L'ajout a rencontré un problème.", Toast.LENGTH_SHORT).show();
        }
    }
}
