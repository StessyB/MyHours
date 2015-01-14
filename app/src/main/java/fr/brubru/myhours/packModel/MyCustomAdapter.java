package fr.brubru.myhours.packModel;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packView.MainActivity;
import fr.brubru.myhours.packView.ManageActivity;

/**
 * Created by Stessy on 08/01/2015.
 */
public class MyCustomAdapter extends BaseAdapter implements ListAdapter
{
    private List<Day> list = new ArrayList<Day>();
    private Context context;

    public MyCustomAdapter(List<Day> list, Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int pos)
    {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos)
    {
        return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View view = convertView;
        if (view == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_list_layout, null);
        }
        TextView listItemText = (TextView) view.findViewById(R.id.dayTxt);
        listItemText.setText(list.get(position).toString());
        ImageButton deleteBtn = (ImageButton) view.findViewById(R.id.removeDay);
        ImageButton editBtn = (ImageButton) view.findViewById(R.id.editDay);

        deleteBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent manage = new Intent(MainActivity.getInstance(), ManageActivity.class);
                manage.putExtra("MANAGE_TYPE", "delete");
                manage.putExtra("id", list.get(position).getId());
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
                manage.putExtra("id", list.get(position).getId());
                manage.putExtra("day", list.get(position).getDay());
                manage.putExtra("H1", list.get(position).getH1());
                manage.putExtra("H2", list.get(position).getH2());
                manage.putExtra("H3", list.get(position).getH3());
                manage.putExtra("H4", list.get(position).getH4());
                MainActivity.getInstance().startActivity(manage);
                notifyDataSetChanged();
            }
        });
        return view;
    }
}
