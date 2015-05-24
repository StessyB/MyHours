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
import android.widget.TextView;

import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Month;
import fr.brubru.myhours.packModel.MonthAdapter;
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
    private static TextView myTextView;
    private static ExpandableListView myDaysExpandableListView;
    private static MonthAdapter myMonthAdapter;

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
        myDaysExpandableListView = (ExpandableListView) rootView.findViewById(R.id.ExpandableListViewDays);
        myTextView = (TextView) rootView.findViewById(R.id.txtMainFragment);
        myTextView.setVisibility(View.GONE);
        listDays();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public static void seeList()
    {
        Variables.isSee = true;
        System.out.println("seeList MainActivity");
    }

    public static void updateDays(String type)
    {
        if(MainActivity.getInstance() != null)
        {
            DataBaseHelper db = new DataBaseHelper(Variables.context, "month");
            List<Month> myMonths = db.getListByMonth();
            db.closeDB();
            myTextView.setVisibility(View.VISIBLE);
            if((myMonths != null) && (myMonths.size() > 0)) myTextView.setVisibility(View.GONE);
            if((type.equals("add")) || (type.equals("drop")) || (type.equals("see")))
            {
                if(myMonthAdapter != null) myMonthAdapter.setMyListMonth(myMonths);
            }
            else
            {
                // update ou delete
                // TODO refresh sans recharger la vue
                if(myMonthAdapter != null) myMonthAdapter.setMyListMonth(myMonths);
            }

            if(type.equals("update"))
            {
                //if(myMonthAdapter != null) myMonthAdapter.notifyWeekAdapter(myMonths);
            }
            if(type.equals("delete"))
            {
                //if(myMonthAdapter != null) myMonthAdapter.notifyWeekAdapter(myMonths);
            }
        }
    }

    public static void listDays()
    {
        System.out.println("listDays MainActivity");
        DataBaseHelper db = new DataBaseHelper(Variables.context, "month");
        List<Month> myMonths = db.getListByMonth();
        db.closeDB();
        if((myMonths != null) && (myMonths.size() > 0))
        {
            myTextView.setVisibility(View.GONE);
            myMonthAdapter = new MonthAdapter(myMonths, Variables.context);
            myDaysExpandableListView.setAdapter(myMonthAdapter);
            myMonthAdapter.notifyDataSetChanged();
        }
        else
        {
            myMonthAdapter = new MonthAdapter(myMonths, Variables.context);
            myDaysExpandableListView.setAdapter(myMonthAdapter);
            myMonthAdapter.notifyDataSetChanged();
            myTextView.setVisibility(View.VISIBLE);
        }
    }
}
