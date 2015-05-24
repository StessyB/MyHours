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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;
import fr.brubru.myhours.packView.MainActivity;
import fr.brubru.myhours.packView.ManageActivity;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class WeekAdapter extends BaseExpandableListAdapter
{
    private List<Week> myListWeeks;
    private LayoutInflater myInflater;
    private Context context;

    public WeekAdapter(List<Week> list, Context context)
    {
        this.myListWeeks = list;
        this.context = context;
        this.myInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public WeekAdapter()
    {
        myListWeeks = new ArrayList<>();
    }

    public List<Week> getMyListWeeks()
    {
        return myListWeeks;
    }

    public void setMyListWeeks(List<Week> myListWeeks)
    {
        this.myListWeeks = myListWeeks;
        this.notifyDataSetChanged();
    }

    @Override
    public int getGroupCount()
    {
        return myListWeeks.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return myListWeeks.get(groupPosition).getMyDays().size();
    }

    @Override
    public Week getGroup(int groupPosition)
    {
        return myListWeeks.get(groupPosition);
    }

    @Override
    public Day getChild(int groupPosition, int childPosition)
    {
        return myListWeeks.get(groupPosition).getMyDays().get(childPosition);
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
            convertView = myInflater.inflate(R.layout.item_week, null);
            Resources r = context.getResources();
            float px10 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, r.getDisplayMetrics());
            convertView.setPadding(
                    convertView.getPaddingLeft() + (int) px10,
                    convertView.getPaddingTop(),
                    convertView.getPaddingRight(),
                    convertView.getPaddingBottom());
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListWeekHeader);
        lblListHeader.setTypeface(null, Typeface.NORMAL);
        lblListHeader.setText(getGroup(groupPosition).toString());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        final int child = childPosition;
        final int group = groupPosition;
        if(convertView == null)
        {
            convertView = myInflater.inflate(R.layout.item_list_layout, null);
            Resources r = context.getResources();
            float px20 = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, r.getDisplayMetrics());
            convertView.setPadding(
                    convertView.getPaddingLeft() + (int) px20,
                    convertView.getPaddingTop(),
                    convertView.getPaddingRight(),
                    convertView.getPaddingBottom());
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.dayTxt);
        // Compare H2 et H3 si < launchTime mn --> blue
        int launchMinutes = 60;
        final Day myDay = myListWeeks.get(groupPosition).getMyDays().get(childPosition);
        int minute = Utils.compareTime(myListWeeks.get(groupPosition).getMyDays().get(childPosition).getH2(), myListWeeks.get(groupPosition).getMyDays().get(childPosition).getH3());
        if(Variables.myLaunchMinutes != null) launchMinutes = Integer.parseInt(Variables.myLaunchMinutes);
        if((minute < launchMinutes) && (minute > 0) && (!Variables.isLaunchAutoSet))
            txtListChild.setText(myDay.getString());
        else
            txtListChild.setText(myDay.toString());
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.removeDay);
        ImageButton editBtn = (ImageButton) convertView.findViewById(R.id.editDay);

        txtListChild.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(Variables.context, myDay.getHoursToString(), Toast.LENGTH_SHORT).show();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent manage = new Intent(MainActivity.getInstance(), ManageActivity.class);
                manage.putExtra("MANAGE_TYPE", "delete");
                manage.putExtra("id", getChild(group,child).getId());
                MainActivity.getInstance().startActivity(manage);
                notifyDataSetChanged();
            }
        });
        editBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent manage = new Intent(MainActivity.getInstance(), ManageActivity.class);
                manage.putExtra("MANAGE_TYPE", "update");
                manage.putExtra("id", getChild(group,child).getId());
                manage.putExtra("day", getChild(group,child).getDay());
                manage.putExtra("H1", getChild(group,child).getH1());
                manage.putExtra("H2", getChild(group,child).getH2());
                manage.putExtra("H3", getChild(group,child).getH3());
                manage.putExtra("H4", getChild(group,child).getH4());
                manage.putExtra("type", getChild(group,child).getType());
                manage.putExtra("time", getChild(group,child).getTime());
                MainActivity.getInstance().startActivity(manage);
                notifyDataSetChanged();
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i2)
    {
        return false;
    }
}
