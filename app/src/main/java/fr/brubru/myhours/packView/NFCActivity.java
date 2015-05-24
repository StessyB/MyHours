package fr.brubru.myhours.packView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

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
    private static String mySound = "sounds_job_done.mp3";
    private final static int MAX_VOLUME = 100;
    private static MediaPlayer myMediaPlayer;
    private static AudioManager mAudioManager;
    private static int soundVolume;

    @Override
    public void onCreate(Bundle savedState)
    {
        super.onCreate(savedState);
        myActivity = this;
        this.setVisible(false);
        System.out.println("--- onCreate NFCActivity ---");
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        myMediaPlayer = new MediaPlayer();
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        soundVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
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
        try
        {
            while (myMediaPlayer.isPlaying()) Thread.sleep(100);
        }
        catch (InterruptedException e) { System.out.println("NFCActivity makeSound error sleep : " + e.getMessage()); }

        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, soundVolume, 0);
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
        // Make a sound
        makeSound();
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

    private void makeSound()
    {
        if(myMediaPlayer.isPlaying())
        {
            myMediaPlayer.stop();
            myMediaPlayer.reset();
        }
        try
        {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            AssetFileDescriptor afd = getAssets().openFd(this.mySound);
            myMediaPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            afd.close();
            myMediaPlayer.prepare();
            myMediaPlayer.start();
        }
        catch (IllegalStateException e) { System.out.println("NFCActivity makeSound error 1 : " + e.getMessage()); }
        catch (IOException e){ System.out.println("NFCActivity makeSound error 2 : " + e.getMessage()); }
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
        Day currentDay = Utils.getCurrentDay();
        long pid = -2;
        DataBaseHelper dbDay = new DataBaseHelper(getApplicationContext(), "day");
        boolean exists = dbDay.checkDayExists(currentDay.getDay());
        boolean isAdded = false;
        if(!exists)
        {
            Day dAdd = new Day(currentDay.getDay(), currentDay.getH1(), "00:00", "00:00", "00:00");
            pid = dbDay.insertDay(dAdd);
            long id = dbDay.getMaxId();
            dAdd.setId(id);
        }
        else
        {
            // Mise a jour de la prochaine heure != "00:00"
            Day day = dbDay.getDayByDay(currentDay.getDay());
            if((day.getType().equals("Travail")) || ((!day.getType().equals("Travail")) && (day.getTime().equals("après-midi"))))
            {
                if(day.getH1().equals("00:00"))
                {
                    System.out.println("TAG NFC saveToDatabase H1 UPDATE");
                    day.setH1(currentDay.getH1());
                    pid = dbDay.updateDay(day);
                    isAdded = true;
                }
                else if(day.getH2().equals("00:00"))
                {
                    if(day.getH1().equals(currentDay.getH1())) pid = -3;
                    if(pid != -3)
                    {
                        System.out.println("TAG NFC saveToDatabase H2 UPDATE");
                        day.setH2(currentDay.getH1());
                        pid = dbDay.updateDay(day);
                        isAdded = true;
                    }
                }
            }
            if(((day.getType().equals("Travail")) && (!isAdded)) || ((!day.getType().equals("Travail")) && (day.getTime().equals("matin"))))
            {
                if(day.getH3().equals("00:00"))
                {
                    if(day.getH2().equals(currentDay.getH1())) pid = -3;
                    if(pid != -3)
                    {
                        System.out.println("TAG NFC saveToDatabase H3 UPDATE");
                        day.setH3(currentDay.getH1());
                        dbDay.updateDay(day);
                        pid = day.getId();
                    }
                    // Verifier qu'il y a une heure entre H2 et H3 si activer
                    /*
                    if(Variables.isLaunchAutoSet)
                    {
                        int launchMinutes = 60;
                        int minute = Utils.compareTime(day.getH2(), day.getH3());
                        DataBaseHelper dbSettings = new DataBaseHelper(getApplicationContext(), "settings");
                        Variables.myLaunchMinutes = dbSettings.getSetting("launchMinutes");
                        dbSettings.close();
                        if(Variables.myLaunchMinutes != null) launchMinutes = Integer.parseInt(Variables.myLaunchMinutes);
                        else Variables.myLaunchMinutes = "60";
                        if((minute < launchMinutes) && (minute > 0))
                        {
                            System.out.println("TAG NFC saveToDatabase H2 UPDATE & H3 UPDATE");
                            // setH3 = H2 + launchMinutes
                            Hour h = Utils.addMinute(day.getH3(), launchMinutes-minute);
                            day.setH3(Utils.pad(h.hour) + ":" + Utils.pad(h.minute));
                            System.out.println("day " + day);
                            dbDay = new DataBaseHelper(getApplicationContext(), "day");
                            dbDay.updateDay(day);
                            pid = day.getId();
                        }
                    } */
                }
                else if(day.getH4().equals("00:00"))
                {
                    if(day.getH3().equals(currentDay.getH1())) pid = -3;
                    if(pid != -3)
                    {
                        System.out.println("TAG NFC saveToDatabase H4 UPDATE");
                        day.setH4(currentDay.getH1());
                        dbDay.updateDay(day);
                        pid = day.getId();
                    }
                }
            }
        }
        dbDay.closeDB();
        if(pid > -1)
        {
            Toast.makeText(NFCActivity.getInstance(), "Le pointage a bien été enregistré.", Toast.LENGTH_SHORT).show();
            if(MainActivity.getInstance() != null)
            {
                if(exists) DaysFragment.updateDays("add");
                else DaysFragment.updateDays("update");
            }
        }
        else
        {
            if(pid == -3)
                Toast.makeText(NFCActivity.getInstance(), "La pointage a déjà été renseigné pour cet horaire.", Toast.LENGTH_SHORT).show();
            else if(exists)
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
                    catch (UnsupportedEncodingException e) { System.out.println("Unsupported Encoding : " + e); }
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
