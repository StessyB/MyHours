package fr.brubru.myhours.packService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by sBRUCHHAEUSER on 07/01/2015.
 */
public class MyBootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
        {
            // Phone is booted, lets start the service
            //Intent serviceIntent = new Intent(context, MySysDateReceiver.class);
            //context.startService(serviceIntent);
        }
    }


}
