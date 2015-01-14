package fr.brubru.myhours.packService;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Variables;
import fr.brubru.myhours.packView.MainActivity;

/**
 * Created by sBRUCHHAEUSER on 07/01/2015.
 */
public class MyService extends IntentService
{
    private BroadcastReceiver myBootReceiver;
    private static MyService myService;
    private static Timer timerService;
    public static boolean isEnabled;
    public static Day myConfigDay;
    private boolean H1Notification;
    private boolean H2Notification;
    private boolean H3Notification;
    private boolean H4Notification;

    public MyService()
    {
        super("MyService MyHours");
        setNotification(false);
        myService = this;
    }

    public static MyService getInstance()
    {
        return myService;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        timerService.cancel();
        Variables.serviceStarted = false;
        Toast.makeText(this, "Service MyHours stopping", Toast.LENGTH_SHORT).show();
        unregisterReceiver(myBootReceiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Toast.makeText(this, "Service MyHours starting", Toast.LENGTH_SHORT).show();
        Variables.serviceStarted = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BOOT_COMPLETED);
        myBootReceiver = new MyBootReceiver();
        registerReceiver(myBootReceiver, filter);
        startTimer();
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {

    }


    private void startTimer()
    {
        timerService = new Timer();
        timerService.scheduleAtFixedRate(new TimerTask()
        {
            @Override
            public void run()
            {
                checkCurrentDate();
            }
        },
        //Set how long before to start calling the TimerTask (in milliseconds)
        5000,
        //Set the amount of time between each execution (in milliseconds)
        5000);
    }

    private void checkCurrentDate()
    {
        Calendar myCalendar = Calendar.getInstance();
        int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);
        // *** Désactiver notifications pendant le week end *** \\
        if((dayOfWeek != Calendar.SUNDAY) && (dayOfWeek != Calendar.SATURDAY) || (MyService.isEnabled))
        {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String currentDay = sdf.format(new Date());
            int currentInSeconds = myCalendar.get(Calendar.HOUR_OF_DAY) * 3600 + myCalendar.get(Calendar.MINUTE) * 60 + myCalendar.get(Calendar.SECOND);
            if(myConfigDay != null)
            {
                int myConfigDayH1InSeconds = Integer.parseInt((myConfigDay.getH1()).split(":")[0]) * 3600 + Integer.parseInt((myConfigDay.getH1()).split(":")[1]) * 60;
                int myConfigDayH2InSeconds = Integer.parseInt((myConfigDay.getH2()).split(":")[0]) * 3600 + Integer.parseInt((myConfigDay.getH2()).split(":")[1]) * 60;
                int myConfigDayH3InSeconds = Integer.parseInt((myConfigDay.getH3()).split(":")[0]) * 3600 + Integer.parseInt((myConfigDay.getH3()).split(":")[1]) * 60;
                int myConfigDayH4InSeconds = Integer.parseInt((myConfigDay.getH4()).split(":")[0]) * 3600 + Integer.parseInt((myConfigDay.getH4()).split(":")[1]) * 60;

                //System.out.println("currentInSeconds " + currentInSeconds);
                //System.out.println("myConfigDayH1InSeconds " + myConfigDayH1InSeconds);
                //System.out.println("myConfigDayH2InSeconds " + myConfigDayH2InSeconds);
                //System.out.println("myConfigDayH3InSeconds " + myConfigDayH3InSeconds);
                //System.out.println("myConfigDayH4InSeconds " + myConfigDayH4InSeconds);

                if((!H1Notification) && ((currentInSeconds >= (myConfigDayH1InSeconds - 3)) && (currentInSeconds <= (myConfigDayH1InSeconds + 3))))
                {
                    // TODO
                    H1Notification = true;
                    System.out.println("--- Notification H1 ---");
                }
                if((!H2Notification) && ((currentInSeconds >= (myConfigDayH2InSeconds - 3)) && (currentInSeconds <= (myConfigDayH2InSeconds + 3))))
                {
                    // TODO
                    H2Notification = true;
                    System.out.println("--- Notification H2 ---");
                }
                if((!H3Notification) && ((currentInSeconds >= (myConfigDayH3InSeconds - 3)) && (currentInSeconds <= (myConfigDayH3InSeconds + 3))))
                {
                    // TODO
                    H3Notification = true;
                    System.out.println("--- Notification H3 ---");
                }
                if((!H4Notification) && ((currentInSeconds >= (myConfigDayH4InSeconds - 3)) && (currentInSeconds <= (myConfigDayH4InSeconds + 3))))
                {
                    // TODO
                    H4Notification = true;
                    System.out.println("--- Notification H4 ---");
                }
                // Reset à la date la plus tard de la config + 5 mn
                if((currentInSeconds >= (myConfigDayH4InSeconds + 5*60)) && (currentInSeconds <= (myConfigDayH4InSeconds + 5*60 + 5)))
                {
                    setNotification(false);
                    System.out.println("--- Reset Notification ---");
                }
            }
            else
            {
                DataBaseHelper db = new DataBaseHelper(this, "config");
                MyService.myConfigDay = db.getDayByDay("99/99/9999");
                db.closeDB();
            }
        }
    }

    private void setNotification(boolean active)
    {
        H1Notification = active;
        H2Notification = active;
        H3Notification = active;
        H4Notification = active;
    }
}
