package fr.brubru.myhours.packUtils;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by sBRUCHHAEUSER on 07/01/2015.
 */
public class Variables 
{
    public static boolean serviceStarted = false;
    public static boolean appliStarted = false;
    public static Context context;
    public static String logDir = Environment.getExternalStorageDirectory() + File.separator + "MyHours";
}
