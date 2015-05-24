package fr.brubru.myhours.packModel;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;
import fr.brubru.myhours.packView.MainActivity;
import fr.brubru.myhours.packView.ManageActivity;

/**
 * Created by StessyB on 02/04/2015.
 */
public class HolidayAdapter extends BaseExpandableListAdapter
{
    private List<Month> myListMonth;
    private LayoutInflater myInflater;
    private Context context;

    public HolidayAdapter(List<Month> list, Context context)
    {
        this.myListMonth = list;
        this.context = context;
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public HolidayAdapter()
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
        return myListMonth.get(groupPosition).getMyDays().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return myListMonth.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return myListMonth.get(groupPosition).getMyDays().get(childPosition);
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
    public boolean hasStableIds()
    {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if(convertView == null)
        {
            convertView = myInflater.inflate(R.layout.listmonths_group, null);

            Resources r = context.getResources();
            float px10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
            convertView.setPadding(
                    convertView.getPaddingLeft() + (int) px10,
                    convertView.getPaddingTop(),
                    convertView.getPaddingRight(),
                    convertView.getPaddingBottom());
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
            convertView = myInflater.inflate(R.layout.item_list_holidays, null);
            Resources r = context.getResources();
            float px20 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
            convertView.setPadding(
                    convertView.getPaddingLeft() + (int) px20,
                    convertView.getPaddingTop(),
                    convertView.getPaddingRight(),
                    convertView.getPaddingBottom());
        }
        TextView txtListChild = (TextView) convertView.findViewById(R.id.holidaysDayTxt);
        txtListChild.setText(myListMonth.get(groupPosition).getMyDays().get(childPosition).getHoliday());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
