package fr.brubru.myhours.packModel;

import android.app.TimePickerDialog;
import android.content.Context;

/**
 * Created by sBRUCHHAEUSER on 08/01/2015.
 */
public class CustomTimePickerDialog extends TimePickerDialog
{
    private int id;

    public CustomTimePickerDialog(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView)
    {
        super(context, callBack, hourOfDay, minute, is24HourView);
    }

    public CustomTimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView)
    {
        super(context, theme, callBack, hourOfDay, minute, is24HourView);
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }
}
