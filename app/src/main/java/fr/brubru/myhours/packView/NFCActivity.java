package fr.brubru.myhours.packView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.brubru.myhours.R;
import fr.brubru.myhours.packModel.Day;
import fr.brubru.myhours.packUtils.DataBaseHelper;
import fr.brubru.myhours.packUtils.Utils;

// http://code.tutsplus.com/tutorials/reading-nfc-tags-with-android--mobile-17278

public class NFCActivity extends Activity
{
    private static NFCActivity myActivity;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_TEXT_PLAIN = "text/plain";
    public static final String MIME_TEXT_MYHOURS = "text/myhours";

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        myActivity = this;
        this.setVisible(false);
        System.out.println("--- onCreate NFCActivity ---");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null)
        {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mNfcAdapter.isEnabled())
        {
            Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        handleIntent(getIntent());
    }

    public static NFCActivity getInstance()
    {
        return myActivity;
    }

    @Override
    public void onResume()
    {
        System.out.println("--- onResume NFCActivity ---");
        super.onResume();
        /**
         * It's important, that the activity is in the foreground (resumed). Otherwise an IllegalStateException is thrown.
         */
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    public void onPause()
    {
        System.out.println("--- onPause NFCActivity ---");
        /**
         * Call this before onPause, otherwise an IllegalArgumentException is thrown as well.
         */
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    public void onDestroy()
    {
        System.out.println("--- onDestroy NFCActivity ---");
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        /**
         * This method gets called, when a new Intent gets associated with the current activity instance.
         * Instead of creating a new activity, onNewIntent will be called.
         * In this case this method gets called, when the user attaches a Tag to the device.
         */
        handleIntent(intent);
    }

    private void handleIntent(Intent intent)
    {
        System.out.println("TAG NFC handleIntent START");
        String action = intent.getAction();
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action))
        {
            String type = intent.getType();
            if(MIME_TEXT_PLAIN.equals(type))
            {
                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                new NdefReaderTask().execute(tag);
            }
            if(MIME_TEXT_MYHOURS.equals(type))
            {
                readTag(intent);
                saveToDatabase();
                NFCActivity.getInstance().finish();
            }
            else
            {
                System.out.println("Wrong mime type: " + type);
                NFCActivity.getInstance().finish();
            }
        }
        else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action))
        {
            // In case we would still use the Tech Discovered Intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = Ndef.class.getName();
            for (String tech : techList)
            {
                if (searchedTech.equals(tech))
                {
                    new NdefReaderTask().execute(tag);
                    break;
                }
            }
        }
        System.out.println("TAG NFC handleIntent END");
    }

    private void readTag(Intent intent)
    {
        // Read the first record which contains the NFC data
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if(rawMsgs != null)
        {
            if(rawMsgs.length > 0)
            {
                NdefRecord relayRecord = ((NdefMessage)rawMsgs[0]).getRecords()[0];
                String nfcData = new String(relayRecord.getPayload());
                // Display the data on the tag
                //Toast.makeText(this, nfcData, Toast.LENGTH_SHORT).show();
                System.out.println("TAG NFC readTag result " + nfcData);
            }
        }
    }

    /**
     * @param activity The corresponding {@link Activity} requesting the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter)
    {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);

        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};

        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try
        {
            filters[0].addDataType(MIME_TEXT_PLAIN);
        }
        catch (IntentFilter.MalformedMimeTypeException e)
        {
            throw new RuntimeException("Check your mime type.");
        }
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    /**
     * @param activity The corresponding {@link BaseActivity} requesting to stop the foreground dispatch.
     * @param adapter The {@link NfcAdapter} used for the foreground dispatch.
     */
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter)
    {
        adapter.disableForegroundDispatch(activity);
    }


    private void saveToDatabase()
    {
        System.out.println("TAG NFC saveToDatabase START");
        Calendar myCalendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDayFR = sdf.format(new Date());
        String currentHour = Utils.pad(myCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + Utils.pad(myCalendar.get(Calendar.MINUTE));
        long pid = -2;
        DataBaseHelper db = new DataBaseHelper(getApplicationContext(), "day");
        boolean exists = db.checkDayExists(currentDayFR);
        if(!exists)
        {
            // TODO check heure courante la plus proche de celles des horaires enregistrés (paramètres)
            Day dAdd = new Day(currentDayFR, currentHour, "00:00", "00:00", "00:00");
            pid = db.insertDay(dAdd);
            long id = db.getMaxId();
            dAdd.setId(id);
        }
        else
        {
            // TODO check heure courante la plus proche de celles des horaires enregistrés (paramètres)
            // Mise a jour de la prochaine heure != "00:00"
            Day day = db.getDayByDay(currentDayFR);
            if(day.getH2().equals("00:00"))
            {
                System.out.println("TAG NFC saveToDatabase H2 UPDATE");
                day.setH2(currentHour);
                db.updateDay(day);
                pid = day.getId();
            }
            else if(day.getH3().equals("00:00"))
            {
                System.out.println("TAG NFC saveToDatabase H3 UPDATE");
                day.setH3(currentHour);
                db.updateDay(day);
                pid = day.getId();
            }
            else if(day.getH4().equals("00:00"))
            {
                System.out.println("TAG NFC saveToDatabase H4 UPDATE");
                day.setH4(currentHour);
                db.updateDay(day);
                pid = day.getId();
            }
        }
        db.closeDB();
        if(pid > -1)
        {
            Toast.makeText(NFCActivity.getInstance(), "Le pointage a bien été enregistré.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            if(exists)
                Toast.makeText(NFCActivity.getInstance(), "La pointage a déjà été renseigné pour ce jour.", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(NFCActivity.getInstance(), "L'ajout a rencontré un problème.", Toast.LENGTH_SHORT).show();
        }
        System.out.println("TAG NFC saveToDatabase END");
    }

    /**
     * Background task for reading the data. Do not block the UI thread while reading.
     *
     * @author Ralf Wondratschek
     *
     */
    private class NdefReaderTask extends AsyncTask<Tag, Void, String>
    {
        @Override
        protected String doInBackground(Tag... params)
        {
            Tag tag = params[0];
            Ndef ndef = Ndef.get(tag);
            if (ndef == null)
            {
                // NDEF is not supported by this Tag.
                return null;
            }
            NdefMessage ndefMessage = ndef.getCachedNdefMessage();
            NdefRecord[] records = ndefMessage.getRecords();
            for (NdefRecord ndefRecord : records)
            {
                if(ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT))
                {
                    try
                    {
                        return readText(ndefRecord);
                    }
                    catch (UnsupportedEncodingException e)
                    {
                        System.out.println("Unsupported Encoding : " + e);
                    }
                }
            }
            return null;
        }

        private String readText(NdefRecord record) throws UnsupportedEncodingException
        {
        /*
         * See NFC forum specification for "Text Record Type Definition" at 3.2.1
         *
         * http://www.nfc-forum.org/specs/
         *
         * bit_7 defines encoding
         * bit_6 reserved for future use, must be 0
         * bit_5..0 length of IANA language code
         */

            byte[] payload = record.getPayload();
            // Get the Text Encoding
            if(payload != null)
            {
                if(payload.length > 0)
                {
                    String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
                    // Get the Language Code
                    int languageCodeLength = payload[0] & 0063;
                    // Get the Text
                    return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
                }
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result)
        {
            if (result != null)
            {
                System.out.println("TAG NFC onPostExecute : " + result);
                if(result.equals("MyHours"))
                {
                    saveToDatabase();
                }
                NFCActivity.getInstance().finish();
            }
        }
    }
}
