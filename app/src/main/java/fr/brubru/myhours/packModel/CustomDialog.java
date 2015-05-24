package fr.brubru.myhours.packModel;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.RelativeLayout;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packView.ManageActivity;

/**
 * Created by StessyB on 01/02/2015.
 */
public class CustomDialog extends Dialog
{
    Context myContext;
    public Button allDay, morningDay, afternoonDay;

    public CustomDialog(Context context)
    {

        super(context);
        myContext = context;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        RelativeLayout myLayout = (RelativeLayout) LayoutInflater.from(myContext).inflate(R.layout.dialog_type_day, null);
        setContentView(myLayout);
        allDay = (Button) findViewById(R.id.btnDay);
        morningDay = (Button) findViewById(R.id.btnMorning);
        afternoonDay = (Button) findViewById(R.id.btnAfternoon);
        allDay.setOnClickListener(ManageActivity.myClickEvent);
        morningDay.setOnClickListener(ManageActivity.myClickEvent);
        afternoonDay.setOnClickListener(ManageActivity.myClickEvent);
    }
}
