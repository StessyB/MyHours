package fr.brubru.myhours.packView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Variables;

/**
 * Created by sBRUCHHAEUSER on 02/02/2015.
 */
public class HolidayFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static EditText myCP, myRTT;
    private static Button myHolidayBtn;

    public static HolidayFragment newInstance(int sectionNumber)
    {
        HolidayFragment fragment = new HolidayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HolidayFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_holiday, container, false);
        myRTT = (EditText) rootView.findViewById(R.id.eRTT);
        myCP = (EditText) rootView.findViewById(R.id.eCP);
        myHolidayBtn = (Button) rootView.findViewById(R.id.btnHolidays);
        SetTextHoliday();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void SetTextHoliday()
    {
        MainActivity.getMyHolidays();
        myRTT.setText(Float.toString(Variables.myRTT.getValue()));
        myCP.setText(Float.toString(Variables.myCP.getValue()));
        myHolidayBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateHoliday(v);
            }
        });
    }

    public void updateHoliday(View v)
    {
        long nb = 0; long pid = -1;
        DataBaseHelper db = new DataBaseHelper(this.getActivity().getApplicationContext(), "holiday");
        Float CP = Float.parseFloat(myCP.getText().toString());
        Variables.myCP.setValue(CP);
        Float RTT = Float.parseFloat(myRTT.getText().toString());
        Variables.myRTT.setValue(RTT);
        boolean exitsRTT = db.checkHolidayExists("RTT");
        if(exitsRTT) nb = db.updateHoliday(Variables.myRTT);
        if(!exitsRTT) pid = db.insertHoliday(Variables.myRTT);
        boolean exitsCP = db.checkHolidayExists("CP");
        if(exitsCP) nb = db.updateHoliday(Variables.myCP);
        if(!exitsCP) pid = db.insertHoliday(Variables.myCP);
        db.closeDB();
        if((nb > 0) || (pid > -1))
            Toast.makeText(this.getActivity(), "Les soldes ont bien été modifiés.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this.getActivity(), "La modification a rencontré un problème.", Toast.LENGTH_SHORT).show();
    }
}