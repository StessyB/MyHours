package fr.brubru.myhours.packView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.Holiday;
import fr.brubru.myhours.packService.MyService;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.ExceptionHandler;
import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks
{
    private Intent myServiceIntent;
    public static MainActivity myActivity;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("--- onCreate MainActivity ---");
        super.onCreate(savedInstanceState);
        Variables.context = getApplicationContext();
        initMyHours();
        initMyHolidays();
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mTitle = getString(R.string.enregistrement);
        myActivity = this;
        Utils.createDir(Variables.logDir);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)) Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Variables.logDir));
        Variables.appliStarted = true;
        getMyHours();
        getMyHolidays();
        //this.startService();
    }

    @Override
    protected void onResume()
    {
        System.out.println("--- onResume MainActivity ---");
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        System.out.println("--- onPause MainActivity ---");
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        System.out.println("onDestroy MainActivity");
        super.onDestroy();
        Variables.appliStarted = false;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
    {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;
        switch(position)
        {
            case 0:
                System.out.println("--- onNavigationDrawerItemSelected Days MainActivity ---");
                fragment = DaysFragment.newInstance(position + 1);
                break;
            case 1:
                System.out.println("--- onNavigationDrawerItemSelected Export MainActivity ---");
                fragment = ExportFragment.newInstance(position + 1);
                break;
            case 2:
            System.out.println("--- onNavigationDrawerItemSelected Holidays MainActivity ---");
            fragment = HolidayFragment.newInstance(position + 1);
            break;
            case 3:
                System.out.println("--- onNavigationDrawerItemSelected Holidays MainActivity ---");
                fragment = HolidayDaysFragment.newInstance(position + 1);
                break;
        }
        if(fragment != null) fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void onSectionAttached(int number)
    {
        Variables.isSee = false;
        switch(number)
        {
            // Mes enregistrements
            case 1:
                mTitle = getString(R.string.enregistrement);
                break;
            // Exporter
            case 2:
                mTitle = getString(R.string.export);
                break;
            // Solde CP/RTT
            case 3:
                if(Variables.myNbHours.equals("37")) mTitle = getString(R.string.conge);
                else mTitle = getString(R.string.congeCP);
                break;
            // Congés
            case 4:
                mTitle = getString(R.string.congePris);
                break;
        }
    }

    public void restoreActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(!mNavigationDrawerFragment.isDrawerOpen())
        {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    public static MainActivity getInstance()
    {
        return myActivity;
    }

    public void initMyHolidays()
    {
        // TODO if 35h pas de RTT
        //-- CP --\\
        Holiday mCP = new Holiday("CP");
        Variables.myCP = mCP;
        //-- RTT --\\
        Holiday mRTT = new Holiday("RTT");
        Variables.myRTT = mRTT;
    }

    public static void getMyHolidays()
    {
        DataBaseHelper db = new DataBaseHelper(Variables.context, "holiday");
        //-- CP --\\
        Holiday mCP = db.getHolidayByName("CP");
        if(mCP != null) Variables.myCP = mCP;
        //-- RTT --\\
        if(Variables.myNbHours.equals("37"))
        {
            Holiday mRTT = db.getHolidayByName("RTT");
            if(mRTT != null) Variables.myRTT = mRTT;
        }
        db.closeDB();
    }

    public void initMyHours()
    {
        //-- Monday --\\
        Day monday = new Day("01/99/9999", "08:00", "12:00", "13:00", "17:00");
        Variables.myMondayDay = monday;
        //-- Tuesday --\\
        Day tuesday = new Day("02/99/9999", "08:00", "12:00", "13:00", "17:00");
        Variables.myTuesdayDay = tuesday;
        //-- Wednesday --\\
        Day wednesday = new Day("03/99/9999", "08:00", "12:00", "13:00", "17:00");
        Variables.myWednesdayDay = wednesday;
        //-- Thursday --\\
        Day thursday = new Day("04/99/9999", "08:00", "12:00", "13:00", "17:00");
        Variables.myThursdayDay = thursday;
        //-- Friday --\\
        Day friday = new Day("05/99/9999", "08:00", "13:00", "00:00", "00:00");
        Variables.myFridayDay = friday;
        //-- Pause1 minutes --\\
        /*
        Variables.isPause1AutoSet = false;
        Variables.myPause1Minutes = "10";
        //-- Pause2 minutes --\\
        Variables.isPause2AutoSet = false;
        Variables.myPause2Minutes = "10";
        */
        //-- Launch minutes --\\
        Variables.isLaunchAutoSet = false;
        Variables.myLaunchMinutes = "60";
        Variables.isSee = false;
        Variables.myNbHours = "37";
    }

    public void getMyHours()
    {
        DataBaseHelper db = new DataBaseHelper(this.getApplicationContext(), "config");
        //-- Monday --\\
        Day tmpMonday = db.getDayByDay("01/99/9999");
        if(tmpMonday != null) Variables.myMondayDay = tmpMonday;
        //-- Tuesday --\\
        Day tmpTuesday = db.getDayByDay("02/99/9999");
        if(tmpTuesday != null) Variables.myTuesdayDay = tmpTuesday;
        //-- Wednesday --\\
        Day tmpWednesday = db.getDayByDay("03/99/9999");
        if(tmpWednesday != null) Variables.myWednesdayDay = tmpWednesday;
        //-- Thursday --\\
        Day tmpThursday = db.getDayByDay("04/99/9999");
        if(tmpThursday != null) Variables.myThursdayDay = tmpThursday;
        //-- Friday --\\
        Day tmpFriday = db.getDayByDay("05/99/9999");
        if(tmpFriday != null) Variables.myFridayDay = tmpFriday;
        db.closeDB();
        db = new DataBaseHelper(getApplicationContext(), "settings");
        /*
        //-- Auto Pause1 minutes --\\
        String isPause1AutoSet = db.getSetting("isPause1AutoSet");
        if(isPause1AutoSet != null)
        {
            Variables.isPause1AutoSet = false;
            if(isPause1AutoSet.equals("true")) Variables.isPause1AutoSet = true;
        }
        //-- Launch Pause1 --\\
        String pause1Minutes = db.getSetting("pause1Minutes");
        if(pause1Minutes != null) Variables.myPause1Minutes = pause1Minutes;
        //-- Auto Pause2 minutes --\\
        db = new DataBaseHelper(getApplicationContext(), "settings");
        String isPause2AutoSet = db.getSetting("isPause2AutoSet");
        if(isPause2AutoSet != null)
        {
            Variables.isPause2AutoSet = false;
            if(isPause2AutoSet.equals("true")) Variables.isPause2AutoSet = true;
        }
        //-- Launch Pause2 --\\
        String pause2Minutes = db.getSetting("pause2Minutes");
        if(pause2Minutes != null) Variables.myPause2Minutes = pause2Minutes;
        */
        //-- Auto launch minutes --\\
        String isLaunchAutoSet = db.getSetting("isLaunchAutoSet");
        if(isLaunchAutoSet != null)
        {
            Variables.isLaunchAutoSet = false;
            if(isLaunchAutoSet.equals("true")) Variables.isLaunchAutoSet = true;
        }
        //-- Launch minutes --\\
        String launchMinutes = db.getSetting("launchMinutes");
        if(launchMinutes != null) Variables.myLaunchMinutes = launchMinutes;
        //-- Number hours --\\
        String nbHours = db.getSetting("nbHours");
        if(nbHours != null) Variables.myNbHours = nbHours;
        db.closeDB();
    }

    public void startService()
    {
        if(!Variables.serviceStarted)
        {
            myServiceIntent = new Intent(this, MyService.class);
            startService(myServiceIntent);
        }
    }

    public void stopService()
    {
        // smyServiceIntent is null when the application is destroyed
        myServiceIntent = new Intent(this, MyService.class);
        stopService(myServiceIntent);
    }
}
