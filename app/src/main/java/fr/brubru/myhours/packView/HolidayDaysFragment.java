package fr.brubru.myhours.packView;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.HolidayAdapter;
import fr.brubru.myhours.packModel.Month;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Variables;

public class HolidayDaysFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static TextView myTextView;
    private static ExpandableListView myHolidaysDaysExpandableListView;
    private static HolidayAdapter myHolidayAdapter;
    public static HolidayDaysFragment newInstance(int sectionNumber)
    {
        HolidayDaysFragment fragment = new HolidayDaysFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public HolidayDaysFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_holiday_days, container, false);
        myHolidaysDaysExpandableListView = (ExpandableListView) rootView.findViewById(R.id.ExpandableListViewHolidaysDays);
        myTextView = (TextView) rootView.findViewById(R.id.txtHolidaysDaysFragment);
        myTextView.setVisibility(View.GONE);
        listHolidaysDays();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void listHolidaysDays()
    {
        // TODO
        System.out.println("listHolidaysDays MainActivity");
        DataBaseHelper db = new DataBaseHelper(Variables.context, "month");
        List<Month> myMonths = db.getListHolidayByMonth();
        db.closeDB();
        if((myMonths != null) && (myMonths.size() > 0))
        {
            myTextView.setVisibility(View.GONE);
            myHolidayAdapter = new HolidayAdapter(myMonths, Variables.context);
            myHolidaysDaysExpandableListView.setAdapter(myHolidayAdapter);
            myHolidayAdapter.notifyDataSetChanged();
        }
        else
        {
            myHolidayAdapter = new HolidayAdapter(myMonths, Variables.context);
            myHolidaysDaysExpandableListView.setAdapter(myHolidayAdapter);
            myHolidayAdapter.notifyDataSetChanged();
            if(!Variables.myNbHours.equals("37")) myTextView.setText(R.string.congesCP);
            myTextView.setVisibility(View.VISIBLE);
        }
    }
}
