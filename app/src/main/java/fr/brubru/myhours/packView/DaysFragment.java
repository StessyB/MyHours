package fr.brubru.myhours.packView;

/**
 * Created by Stessy on 10/01/2015.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.Month;
import fr.brubru.myhours.packModel.MyCustomAdapter;
import fr.brubru.myhours.packModel.MyMonthAdapter;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Variables;

/**
 * A DaysFragment fragment containing a simple view.
 */
public class DaysFragment extends Fragment
{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    //private static MyCustomAdapter myCustomAdapter;
    //private static ListView myDaysListView;
    private static TextView myTextView;
    private static ExpandableListView myDaysExpandableListView;
    private static MyMonthAdapter myMonthAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static DaysFragment newInstance(int sectionNumber)
    {
        DaysFragment fragment = new DaysFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DaysFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        //myDaysListView = (ListView) rootView.findViewById(R.id.listViewDays);
        myDaysExpandableListView = (ExpandableListView) rootView.findViewById(R.id.ExpandableListViewDays);
        myTextView = (TextView) rootView.findViewById(R.id.txtMainFragment);
        myTextView.setVisibility(View.GONE);
        displayDays();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public static void getUpdate()
    {
        myTextView.setVisibility(View.GONE);
        displayDays();
    }

    public static void displayDays()
    {
        clearDays();
        listDays();
    }

    public static void clearDays()
    {
        //if(myDaysListView != null) myDaysListView.setAdapter(null);
        if(myDaysExpandableListView != null) myDaysExpandableListView.setAdapter(new MyMonthAdapter());
    }

    public static void listDays()
    {
        // TODO voir par semaine, mois ... avec nombre d'heures
        System.out.println("listDays MainActivity");
        //DataBaseHelper db = new DataBaseHelper(Variables.context, "day");
        //List<Day> days = db.getList();
        //db.closeDB();
        DataBaseHelper db = new DataBaseHelper(Variables.context, "month");
        List<Month> months = db.getListPerMonth();
        db.closeDB();
        //if(days.size() > 0)
        if((months != null) && (months.size() > 0))
        {
            //myCustomAdapter = new MyCustomAdapter(days, Variables.context);
            //myDaysListView.setAdapter(myCustomAdapter);
            //myCustomAdapter.notifyDataSetChanged();

            myMonthAdapter = new MyMonthAdapter(months, Variables.context);
            myDaysExpandableListView.setAdapter(myMonthAdapter);
            myMonthAdapter.notifyDataSetChanged();
        }
        else
            myTextView.setVisibility(View.VISIBLE);
    }
}
