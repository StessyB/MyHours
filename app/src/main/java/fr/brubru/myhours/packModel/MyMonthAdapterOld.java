package fr.brubru.myhours.packModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packView.MainActivity;
import fr.brubru.myhours.packView.ManageActivity;

/**
 * Created by sBRUCHHAEUSER on 13/01/2015.
 */
public class MyMonthAdapterOld extends BaseExpandableListAdapter
{
    private List<Month> myListMonth = new ArrayList<Month>();
    private LayoutInflater myInflater;
    private Context context;

    public MyMonthAdapterOld(List<Month> list, Context context)
    {
        this.myListMonth = list;
        this.context = context;
        myInflater = LayoutInflater.from(context);
    }

    public MyMonthAdapterOld()
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
        return myListMonth.get(groupPosition).getMyDays().size();
    }

    @Override
    public Month getGroup(int groupPosition)
    {
        return myListMonth.get(groupPosition);
    }

    @Override
    public Day getChild(int groupPosition, int childPosition)
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
    public boolean hasStableIds()
    {
        return true;
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
        final int child = childPosition;
        final int group = groupPosition;

        if(convertView == null)
        {
            convertView = myInflater.inflate(R.layout.item_list_layout, parent, false);
        }

        TextView txtListChild = (TextView) convertView.findViewById(R.id.dayTxt);
        txtListChild.setText(getChild(groupPosition,childPosition).toString());
        ImageButton deleteBtn = (ImageButton) convertView.findViewById(R.id.removeDay);
        ImageButton editBtn = (ImageButton) convertView.findViewById(R.id.editDay);

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
