package fr.brubru.myhours.packView;

/**
 * Created by Stessy on 10/01/2015.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Variables;

/**
 * A ExportFragment fragment containing a simple view.
 */
public class ExportFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Calendar myCalendar;
    private TextView myStartTxt;
    private TextView myEndTxt;
    private Button myExportBtn;

    public static ExportFragment newInstance(int sectionNumber)
    {
        ExportFragment fragment = new ExportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ExportFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_export, container, false);
        myStartTxt = (TextView) rootView.findViewById(R.id.startExport);
        myEndTxt = (TextView) rootView.findViewById(R.id.endExport);
        myExportBtn = (Button) rootView.findViewById(R.id.btnExport);
        SetTextExport();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public void SetTextExport()
    {
        myCalendar = Calendar.getInstance();
        final String myFormat = "dd/MM/yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);

        myStartTxt.setText(sdf.format(myCalendar.getTime()));
        final DatePickerDialog.OnDateSetListener myStartDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myStartTxt.setText(sdf.format(myCalendar.getTime()));
            }
        };
        myStartTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(getActivity(), myStartDateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le jour");
                myDatePickerDialog.show();
            }
        });

        myEndTxt.setText(sdf.format(myCalendar.getTime()));
        final DatePickerDialog.OnDateSetListener myEndDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myEndTxt.setText(sdf.format(myCalendar.getTime()));
            }
        };
        myEndTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(getActivity(), myEndDateSetListener, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le jour");
                myDatePickerDialog.show();
            }
        });

        myExportBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String start = myStartTxt.getText().toString();
                String end = myEndTxt.getText().toString();
                DataBaseHelper myDataBaseHelper = new DataBaseHelper(Variables.context, "day");
                List<Day> myExportDays = myDataBaseHelper.getDaysByPeriod(start, end);
                exportDays(start, end, myExportDays);
            }
        });
    }

    private void exportDays(String start, String end, List<Day> myExportDays)
    {
        // TODO ajouter nb heures
        String export = "";
        for(Day day : myExportDays)
            export += day.getExport() + "\n";
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra("sms_body", export);
        //intent.putExtra("address", destinataire);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Export des pointages du " + start + " au "  +end);
        intent.putExtra(Intent.EXTRA_TEXT, export);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, "Partage de l'export"));
    }
}
