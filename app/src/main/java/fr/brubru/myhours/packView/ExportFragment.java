package fr.brubru.myhours.packView;

/**
 * Created by Stessy on 10/01/2015.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packModel.Holiday;
import fr.brubru.myhours.packModel.Month;
import fr.brubru.myhours.packModel.Week;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Utils;
import fr.brubru.myhours.packUtils.Variables;

/**
 * A ExportFragment fragment containing a simple view.
 */
public class ExportFragment extends Fragment
{
    private static final String ARG_SECTION_NUMBER = "section_number";
    private Calendar myStartCalendar, myEndCalendar;
    private TextView myStartMonthTxt, myEndMonthTxt;
    private ImageButton myExportBtn;
    private ImageButton myShareBtn;
    private ImageButton mySaveBtn;
    private CheckBox myExportHolidaysCheckBox;
    private static int myType;
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
    private static Font holidayFont = new Font(Font.FontFamily.HELVETICA, 13, Font.NORMAL, BaseColor.BLACK);
    private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC, BaseColor.BLACK);
    private static Font monthFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);
    private static Font weekFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, BaseColor.DARK_GRAY);
    private static Font dayFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);

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
        myStartMonthTxt = (TextView) rootView.findViewById(R.id.startMonthExport);
        myEndMonthTxt = (TextView) rootView.findViewById(R.id.endMonthExport);
        myExportBtn = (ImageButton) rootView.findViewById(R.id.btnExport);
        myShareBtn = (ImageButton) rootView.findViewById(R.id.btnShareExport);
        mySaveBtn = (ImageButton) rootView.findViewById(R.id.btnSavePDF);
        myExportHolidaysCheckBox = (CheckBox) rootView.findViewById(R.id.cbExportHolidays);
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
        if(!Variables.myNbHours.equals("37")) myExportHolidaysCheckBox.setText(R.string.exportCP);
        myStartCalendar = Calendar.getInstance();
        myEndCalendar = Calendar.getInstance();
        final String myFormat = "MMMM yyyy";
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.FRANCE);
        myStartMonthTxt.setText(sdf.format(myStartCalendar.getTime()));
        myEndMonthTxt.setText(sdf.format(myEndCalendar.getTime()));

        final DatePickerDialog.OnDateSetListener myStartDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myStartCalendar.set(Calendar.YEAR, year);
                myStartCalendar.set(Calendar.MONTH, monthOfYear);
                myStartCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myStartMonthTxt.setText(sdf.format(myStartCalendar.getTime()));
            }
        };
        final DatePickerDialog.OnDateSetListener myEndDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                myEndCalendar.set(Calendar.YEAR, year);
                myEndCalendar.set(Calendar.MONTH, monthOfYear);
                myEndCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                myEndMonthTxt.setText(sdf.format(myEndCalendar.getTime()));
            }
        };

        myStartMonthTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(getActivity(), myStartDateSetListener, myStartCalendar.get(Calendar.YEAR), myStartCalendar.get(Calendar.MONTH), myStartCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le mois");

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                    if (daySpinnerId != 0)
                    {
                        View daySpinner = myDatePickerDialog.getDatePicker().findViewById(daySpinnerId);
                        if (daySpinner != null) daySpinner.setVisibility(View.GONE);
                    }
                }
                else
                    myDatePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                myDatePickerDialog.show();
            }
        });

        myEndMonthTxt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DatePickerDialog myDatePickerDialog = new DatePickerDialog(getActivity(), myEndDateSetListener, myEndCalendar.get(Calendar.YEAR), myEndCalendar.get(Calendar.MONTH), myEndCalendar.get(Calendar.DAY_OF_MONTH));
                myDatePickerDialog.setTitle("Choisir le mois");
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                {
                    int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                    if (daySpinnerId != 0)
                    {
                        View daySpinner = myDatePickerDialog.getDatePicker().findViewById(daySpinnerId);
                        if (daySpinner != null) daySpinner.setVisibility(View.GONE);
                    }
                }
                else
                    myDatePickerDialog.getDatePicker().findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);

                myDatePickerDialog.show();
            }
        });

        myExportBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myType = 1;
                getValue();
            }
        });

        myShareBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myType = 2;
                getValue();
            }
        });

        mySaveBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                myType = 3;
                getValue();
            }
        });
    }

    public void getValue()
    {
        String type = "";
        List<Month> myExportMonth;
        myExportBtn.setEnabled(false);
        myShareBtn.setEnabled(false);
        String myFilePath = null;
        String dateStart = myStartMonthTxt.getText().toString();
        String dateEnd = myEndMonthTxt.getText().toString();
        DataBaseHelper myDataBaseHelper = new DataBaseHelper(Variables.context, "month");
        String monthStart = dateStart.split(" ")[0];
        String yearStart = dateStart.split(" ")[1];
        String monthEnd = dateEnd.split(" ")[0];
        String yearEnd = dateEnd.split(" ")[1];
        // TODO verifier date debut <= date fin export
        if(!myExportHolidaysCheckBox.isChecked())
        {
            myExportMonth = myDataBaseHelper.getAllListByMonthPeriod(monthStart, yearStart, monthEnd, yearEnd);
            type = "P";
        }
        else
        {
            // EXPORT CP / RTT
            myExportMonth = myDataBaseHelper.getHolidaysListByMonthPeriod(monthStart, yearStart, monthEnd, yearEnd);
            type = "H";
        }
        myDataBaseHelper.closeDB();
        if((myExportMonth != null) && (myExportMonth.size() > 0)) myFilePath = getPDF(type, monthStart, yearStart, monthEnd, yearEnd, myExportMonth);
        if((myExportMonth != null) && (myExportMonth.size() > 0)) myFilePath = getPDF(type, monthStart, yearStart, monthEnd, yearEnd, myExportMonth);

        if(myFilePath != null)
        {
            if(myType == 1) createNotification(myFilePath);
            if(myType == 2) shareDays(type, monthStart, yearStart, monthEnd, yearEnd, myFilePath);
            if(myType == 3) Toast.makeText(this.getActivity(), "Le PDF a été sauvegardé dans le dossier MyHours.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this.getActivity(), "Il n'y a rien à sauvegarder.", Toast.LENGTH_SHORT).show();
        }
        Holiday.myCP = 0;
        Holiday.myRTT = 0;
        Holiday.myFerie = 0;
    }

    private String getPDF(String type, String startMonth, String startYear, String endMonth, String endYear, List<Month> myExportMonth)
    {
        String filename = "";
        String title = "";
        if((startMonth.equals(endMonth)) && (startYear.equals(endYear)))
        {
            if(type.equals("P"))
            {
                filename = Variables.logDir + File.separator + startMonth + "_" + startYear + ".pdf";
                title = "Export des pointages de " + startMonth + " " + startYear;
            }
            else if(type.equals("H"))
            {
                filename = Variables.logDir + File.separator + startMonth + "_" + startYear + "_conges.pdf";
                title = "Export des congés de " + startMonth + " " + startYear;
        }
        }
        else
        {
            if(type.equals("P"))
            {
                filename = Variables.logDir + File.separator + startMonth + "_" + startYear + "-" + endMonth + "_" + endYear + ".pdf";
                title = "Export des pointages de " + startMonth + " " + startYear + " à " + endMonth + " " + endYear;
            }
            else if(type.equals("H"))
            {
                filename = Variables.logDir + File.separator + startMonth + "_" + startYear + "-" + endMonth + "_" + endYear + "_conges.pdf";
                title = "Export des congés de " + startMonth + " " + startYear + " à " + endMonth + " " + endYear;
            }
        }
        try
        {
            File file = new File(filename);
            if (!file.exists())
            {
                file.createNewFile();
            }
            else
            {
                file.delete();
                file.createNewFile();
            }
            Document document = new Document(PageSize.A4, 30, 30, 20, 20);
            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
            document.open();
            // METADATA
            document.addTitle(title);
            //document.addSubject("Acometis");
            if(type.equals("P")) document.addKeywords("Pointages");
            if(type.equals("H")) document.addKeywords("Congés");
            document.addAuthor("MyHours");
            document.addCreator("MyHours");
            // TITLE PAGE
            Paragraph titleParagraph = new Paragraph(title, titleFont);
            titleParagraph.setAlignment(Element.ALIGN_MIDDLE);
            titleParagraph.setAlignment(Element.ALIGN_CENTER);
            addEmptyLine(titleParagraph, 1);
            Paragraph myDate = new Paragraph("Généré le : " + new Date(), smallFont);
            myDate.setAlignment(Element.ALIGN_MIDDLE);
            myDate.setAlignment(Element.ALIGN_CENTER);
            titleParagraph.add(myDate);
            addEmptyLine(titleParagraph, 1);
            document.add(titleParagraph);
            // SOLDE RTT & CP
            Paragraph myHolidaysParagraph = new Paragraph(Holiday.getMyHolidaysString(), holidayFont);
            myHolidaysParagraph.setAlignment(Element.ALIGN_LEFT);
            addEmptyLine(myHolidaysParagraph, 1);
            document.add(myHolidaysParagraph);

            // PAUSE MIDI
            if(Variables.isLaunchAutoSet)
            {
                Paragraph myLaunchParagraph = new Paragraph(Utils.getMyLaunchString(), holidayFont);
                myLaunchParagraph.setAlignment(Element.ALIGN_LEFT);
                addEmptyLine(myLaunchParagraph, 1);
                document.add(myLaunchParagraph);
            }

            // CONTENT
            for(Month myMonth : myExportMonth)
            {
                Paragraph myMonthParagraph = new Paragraph(myMonth.getExport(), monthFont);
                myMonthParagraph.setIndentationLeft(5);
                addEmptyLine(myMonthParagraph, 1);
                if(type.equals("P"))
                {
                    for(Week myWeek : myMonth.getMyWeeks())
                    {
                        Paragraph myWeekParagraph = new Paragraph(myWeek.getExport(), weekFont);
                        myWeekParagraph.setAlignment(Element.ALIGN_LEFT);
                        myWeekParagraph.setIndentationLeft(10);
                        myMonthParagraph.add(myWeekParagraph);

                        for(Day myDay : myWeek.getMyDays())
                        {
                            Paragraph myDayParagraph = new Paragraph(myDay.getExport(), dayFont);
                            myDayParagraph.setIndentationLeft(20);
                            myMonthParagraph.add(myDayParagraph);
                        }
                        addEmptyLine(myMonthParagraph, 1);
                    }
                }
                else if(type.equals("H"))
                {
                    for(Day myDay : myMonth.getMyDays())
                    {
                        myDay.getExport();
                        Paragraph myDayParagraph = new Paragraph(myDay.getHolidayExport(), dayFont);
                        myDayParagraph.setIndentationLeft(20);
                        myMonthParagraph.add(myDayParagraph);
                    }
                }
                addEmptyLine(myMonthParagraph, 1);
                document.add(myMonthParagraph);
            }
            document.close();
            System.out.println("FilePath " + file.getAbsolutePath());
            return file.getAbsolutePath();
        }
        catch (IOException e) { System.out.println("ExportFragment exportMonth IOException : " + e.getMessage()); return null; }
        catch (DocumentException e) { System.out.println("ExportFragment exportMonth DocumentException : " + e.getMessage()); return null; }
    }

    private static void addEmptyLine(Paragraph paragraph, int number)
    {
        for (int i = 0; i < number; i++)
        {
            paragraph.add(new Paragraph(" "));
        }
    }

    public void createNotification(String filename)
    {
        File file = new File(filename);
        // Prepare intent which is triggered if the notification is selected
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        PendingIntent pIntent = PendingIntent.getActivity(this.getActivity(), 0, intent, 0);
        // Build notification
        Notification notification = new Notification.Builder(this.getActivity())
                .setContentTitle("Décompte téléchargé.")
                .setContentText("Appuyez pour le voir.").setSmallIcon(R.drawable.ic_action_file_file_download)
                .setAutoCancel(true)
                .setContentIntent(pIntent)
                .build();
        NotificationManager notificationManager = (NotificationManager) this.getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        myExportBtn.setEnabled(true);
        myShareBtn.setEnabled(true);
        notificationManager.notify(0, notification);
    }

    private void shareDays(String type, String startMonth, String startYear, String endMonth, String endYear, String myPDF)
    {
        String subject = "";
        if((startMonth.equals(endMonth)) && (startYear.equals(endYear)))
        {
            if(type.equals("P")) subject = "Export des pointages de " + startMonth + " " + startYear;
            else if(type.equals("H")) subject = "Export des congés de " + startMonth + " " + startYear;
        }
        else
        {
            if(type.equals("P")) subject = "Export des pointages de " + startMonth + " " + startYear + " à " + endMonth + " " + endYear;
            else if(type.equals("H")) subject = "Export des congés de " + startMonth + " " + startYear + " à " + endMonth + " " + endYear;
        }
        myExportBtn.setEnabled(true);
        myShareBtn.setEnabled(true);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+ myPDF));
        startActivity(Intent.createChooser(shareIntent, "Partage du décompte"));
    }
}
