package fr.brubru.myhours.packModel;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class MonthAdapter extends BaseExpandableListAdapter
{
    private List<Month> myListMonth;
    private LayoutInflater myInflater;
    private Context context;
    private CustomExpandableListView elv;
    private WeekAdapter myWeekAdapter;

    public MonthAdapter(List<Month> list, Context context)
    {
        this.myListMonth = list;
        this.context = context;
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public MonthAdapter()
    {
        myListMonth = new ArrayList<>();
    }

    public List<Month> getMyListMonth()
    {
        return myListMonth;
    }

    public void setMyListMonth(List<Month> myListMonth)
    {
        this.myListMonth = myListMonth;
        if(myWeekAdapter != null) myWeekAdapter.notifyDataSetChanged();
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount()
    {
        return myListMonth.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
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
        if(myListMonth.get(groupPosition).getMyWeeks().size() > 0)
        {
            if(elv != null) elv.setAdapter(new WeekAdapter());
            elv = new CustomExpandableListView(context);
            elv.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT));
            myWeekAdapter = new WeekAdapter(myListMonth.get(groupPosition).getMyWeeks(), context);
            elv.setAdapter(myWeekAdapter);
            elv.setPadding(0, 0, 0, 0);
            elv.setGroupIndicator(null);
            myWeekAdapter.notifyDataSetChanged();
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
