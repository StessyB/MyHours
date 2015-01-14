package fr.brubru.myhours.packUtils;

import android.app.Activity;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sBRUCHHAEUSER on 29/12/2014.
 */
public class ExceptionHandler implements UncaughtExceptionHandler
{
    /**
     * Voir {@link java.lang.Thread.UncaughtExceptionHandler}.
     */
    private UncaughtExceptionHandler defaultUEH;

    /**
     * Chemin du repertoire de destination des fichiers.
     */
    private String localPath;

    /**
     * {@link {@link android.app.Activity} courrante pour les logs.
     */
    private Activity myActivity = null;

    /**
     * Constructeur de la classe.
     * @param app :  {@link android.app.Activity} dont on veut logguer les erreurs.
     */
    public ExceptionHandler(Activity app)
    {
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        this.myActivity = app;
    }

    /**
     * Constructeur de la classe.
     * @param path :  <code>String</code> du chemin du repertoire.
     */
    public ExceptionHandler(String path)
    {
        this.localPath = path;
        this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
    }

    /* (non-Javadoc)
     * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
     */
    public void uncaughtException(Thread t, Throwable e)
    {
        DateFormat ft = new SimpleDateFormat("yyyy_MM_dd_HH:mm");
        Date date = new Date( );
        String dateString = ft.format(date);
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        e.printStackTrace(printWriter);
        String stacktrace = result.toString();
        printWriter.close();
        String filename = dateString + ".stacktrace";
        if (localPath != null) writeToFile(stacktrace, filename);
        defaultUEH.uncaughtException(t, e);
    }

    /**
     * Permet d'ecrire les erreurs dans un fichier donne.
     * @param stacktrace : <code>String</code> de l'erreur e ecrire
     * @param filename : <code>String</code> du nom du fichier
     */
    private void writeToFile(String stacktrace, String filename)
    {
        try
        {
            BufferedWriter bos = new BufferedWriter(new FileWriter(localPath + "/" + filename));
            bos.write(stacktrace);
            bos.flush();
            bos.close();
        }
        catch (Exception e) { e.printStackTrace(); }
    }

    /**
     * Permet de recuperer l'erreur stacktrace.
     * @param t : <code>Thread</code>
     * @param e : <code>Throwable</code>
     */
    public void uncaughtException_(Thread t, Throwable e)
    {
        StackTraceElement[] arr = e.getStackTrace();
        String Raghav = t.toString();
        String report = e.toString() + "\n\n";
        report += "--------- Stack trace ---------\n\n" + Raghav;
        for (int i = 0; i <arr.length ; i++)
        {
            report += "    " + arr[i].toString() + "\n";
        }
        report += "-------------------------------\n\n";

        // If the exception was thrown in a background thread inside
        // AsyncTask, then the actual exception can be found with getCause
        report += "--------- Cause ---------\n\n";
        Throwable cause = e.getCause();
        if(cause != null)
        {
            report += cause.toString() + "\n\n";
            arr = cause.getStackTrace();
            for (int i = 0; i < arr.length ; i++)
            {
                report += "    " + arr[i].toString() + "\n";
            }
        }
        report += "-------------------------------\n\n";

        try
        {
            FileOutputStream trace = myActivity.openFileOutput("stack.trace", Context.MODE_PRIVATE);
            trace.write(report.getBytes());
            trace.close();
        }
        catch(IOException ioe) { ioe.printStackTrace(); }

        defaultUEH.uncaughtException(t, e);
    }
}
