package com.argostic.weekend;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseObject;

import java.nio.charset.Charset;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beam);
        //SharedPreferences sharedPreference = this.getSharedPreferences("com.argostic.weekend", Context.MODE_PRIVATE);
        //sharedPreference.edit().putString("username", "rob").apply();

        //String username = sharedPreference.getString("username", "");
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "j7UnOVZnDCQUfq0YwVhsnMvHSfkWQzXlkNS0faD9", "O1Q42eHi73OMmwQLlXUoYi1rYxhi6nIBVUoFXGkO");
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();


        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure about new attendee?")
                .setMessage("Do you really want to add a new member to your class?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_beam, container, false);
        }
    }


    //Button method
    public void setText(View v) {
        EditText ed = (EditText) findViewById(R.id.editext);
        TextView text = (TextView) findViewById(R.id.text);
        text.setText(ed.getText());
    }

    @Override
    protected void onResume() {
        super.onResume();
        //set Ndef message to send by beam
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        assert nfcAdapter != null;
        nfcAdapter.setNdefPushMessageCallback(
                new NfcAdapter.CreateNdefMessageCallback() {
                    public NdefMessage createNdefMessage(NfcEvent event) {
                        return createMessage();
                    }
                }, this);

        //See if app got called by AndroidBeam intent.
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            extractPayload(getIntent());
        }
    }

    /**
     * Creates a new NdefMessage with payload of text field.
     * @return NFC Data Exchange Format
     */
    private NdefMessage createMessage() {
        String mimeType = "application/com.argostic.weekend";
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));

        //GENERATE PAYLOAD
        TextView text = (TextView) findViewById(R.id.text);
        byte[] payLoad = text.getText().toString().getBytes();

        //GENERATE NFC MESSAGE
        return new NdefMessage(
                new NdefRecord[]{
                        new NdefRecord(NdefRecord.TNF_MIME_MEDIA,
                                mimeBytes,
                                null,
                                payLoad),
                        NdefRecord.createApplicationRecord("com.argostic.weekend")
                });
    }

    private void extractPayload(Intent beamIntent) {
        Parcelable[] messages = beamIntent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        NdefMessage message = (NdefMessage) messages[0];
        NdefRecord record = message.getRecords()[0];
        String payload = new String(record.getPayload());
        TextView text = (TextView) findViewById(R.id.text);
        text.setText(payload);
    }

}
