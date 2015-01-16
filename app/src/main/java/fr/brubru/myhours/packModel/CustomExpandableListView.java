package fr.brubru.myhours.packModel;

import android.content.Context;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;

/**
 * Created by Stessy on 15/01/2015.
 */
public class CustomExpandableListView extends ExpandableListView
{
    private final float mDensity;

    public CustomExpandableListView(Context context)
    {
        super(context);
        mDensity = context.getResources().getDisplayMetrics().density;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        // Taille pour 6 jourss
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(1050, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
