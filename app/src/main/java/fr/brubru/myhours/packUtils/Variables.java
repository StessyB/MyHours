package fr.brubru.myhours.packUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.Holiday;

/**
 * Created by sBRUCHHAEUSER on 07/01/2015.
 */
public class Variables 
{
    public static boolean serviceStarted = false;
    public static boolean appliStarted = false;
    public static boolean isLaunchAutoSet;
    //public static boolean isPause1AutoSet, isPause2AutoSet;
    public static Context context;
    public static String logDir = Environment.getExternalStorageDirectory() + File.separator + "MyHours";
    public static Day myMondayDay, myTuesdayDay, myWednesdayDay, myThursdayDay, myFridayDay;
    //public static String myPause1Minutes, myPause2Minutes;
    public static String myLaunchMinutes, myNbHours;
    public static Holiday myCP, myRTT;
    public static boolean isSee;
}
