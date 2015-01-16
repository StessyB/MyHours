package fr.brubru.myhours.packModel;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class MyMonthAdapter extends BaseExpandableListAdapter
{
    private List<Month> myListMonth;
    private LayoutInflater myInflater;
    private Context context;
    private CustomExpandableListView elv;

    // TODO SEE http://harrane.blogspot.in/2013/04/three-level-expandablelistview.html#comment-form

    public MyMonthAdapter(List<Month> list, Context context)
    {
        this.myListMonth = list;
        this.context = context;
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //myInflater = LayoutInflater.from(context);
    }

    public MyMonthAdapter()
    {

    }

    @Override
    public int getGroupCount()
    {
        return myListMonth.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        //return myListMonth.get(groupPosition).getMyDays().size();
        return myListMonth.get(groupPosition).getMyWeeks().size();
    }

    @Override
    public Month getGroup(int groupPosition)
    {
        return myListMonth.get(groupPosition);
    }

    @Override
    public Week getChild(int groupPosition, int childPosition)
    {
        //return myListMonth.get(groupPosition).getMyDays().get(childPosition);
        return myListMonth.get(groupPosition).getMyWeeks().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer)
    {
        super.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer)
    {
        if (observer != null)
        {
            super.unregisterDataSetObserver(observer);
        }
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = myInflater.inflate(R.layout.listmonths_group, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListMonthsHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = myInflater.inflate(R.layout.item_week, parent, false);
        }
        //TextView txtListChild = (TextView) convertView.findViewById(R.id.lblListWeekHeader);
        //Week week = myListMonth.get(groupPosition).getMyWeeks().get(childPosition);
        //txtListChild.setText(week.toString());
        if(myListMonth.get(groupPosition).getMyWeeks().size() > 0)
        {
            if(elv != null) elv.setAdapter(new MyWeekAdapter());
            elv = new CustomExpandableListView(context);
            elv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
            MyWeekAdapter myWeekAdapter = new MyWeekAdapter(myListMonth.get(groupPosition).getMyWeeks(), context);
            elv.setAdapter(myWeekAdapter);
            elv.setPadding(0, 0, 0, 0);
            elv.setGroupIndicator(null);
            myWeekAdapter.notifyDataSetChanged();
            //((ViewGroup)convertView).addView(elv);
            return elv;
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2)
    {
        return false;
    }
}
