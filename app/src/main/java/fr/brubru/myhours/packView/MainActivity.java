package fr.brubru.myhours.packView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.widget.ListView;

import java.io.File;
import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.MyCustomAdapter;
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
    //private boolean isDaysDisplayed;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        System.out.println("--- onCreate MainActivity ---");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));
        mTitle = getString(R.string.enregistrement);
        myActivity = this;
        Variables.context = getApplicationContext();
        Utils.createDir(Variables.logDir);
        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler)) Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(Variables.logDir));
        Variables.appliStarted = true;
        this.startService();
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
                // TODO
                System.out.println("--- onNavigationDrawerItemSelected Holidays MainActivity ---");
                //fragment = HolidayFragment.newInstance(position + 1);
                break;
        }
        if(fragment != null) fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }

    public void onSectionAttached(int number)
    {
        switch(number)
        {
            // Mes enregistrements
            case 1:
                mTitle = getString(R.string.enregistrement);
                System.out.println("onSectionAttached Days MainActivity");
                break;
            // Exporter
            case 2:
                mTitle = getString(R.string.export);
                System.out.println("onSectionAttached Export MainActivity");
                break;
            // Congés
            case 3:
                mTitle = getString(R.string.conge);
                System.out.println("onSectionAttached Holidays MainActivity");
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
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onDestroy()
    {
        System.out.println("onDestroy MainActivity");
        super.onDestroy();
        Variables.appliStarted = false;
    }

    public static MainActivity getInstance()
    {
        return myActivity;
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
