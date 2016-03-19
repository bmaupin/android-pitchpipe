package ca.bmaupin.pitchpipe;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity
        implements View.OnClickListener, MidiDriver.OnMidiStartListener,
        AdapterView.OnItemSelectedListener {
    private TextView text;

    protected MidiDriver midi;

    // TODO: add a setting to change this
    private static final int NOTE_DURATION = 5000;
    private int lastNotePitch = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        midi = new MidiDriver();

        View v = findViewById(R.id.button1);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button2);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button3);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button4);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button5);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button6);
        if (v != null)
            v.setOnClickListener(this);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        List<Integer> instrumentIds = new ArrayList<Integer>();
        for (int i=0; i<128; i++) {
            instrumentIds.add(new Integer(i));
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, instrumentIds);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

//        text = (TextView)findViewById(R.id.textView2);

        if (midi != null)
            midi.setOnMidiStartListener(this);
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        sendMidi(0xc0, (Integer)parent.getItemAtPosition(pos));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (midi != null)
            midi.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (midi != null)
            midi.stop();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            // 40, 45, 50, 55, 59, 64
            case R.id.button1:
                stopNote(lastNotePitch);
                lastNotePitch = 40;
                playNote(40);
                break;

            case R.id.button2:
                stopNote(lastNotePitch);
                lastNotePitch = 45;
                playNote(lastNotePitch);
                break;

            case R.id.button3:
                stopNote(lastNotePitch);
                lastNotePitch = 50;
                playNote(lastNotePitch);
                break;

            case R.id.button4:
                stopNote(lastNotePitch);
                lastNotePitch = 55;
                playNote(lastNotePitch);
                break;

            case R.id.button5:
                stopNote(lastNotePitch);
                lastNotePitch = 59;
                playNote(lastNotePitch);
                break;

            case R.id.button6:
                stopNote(lastNotePitch);
                lastNotePitch = 64;
                playNote(lastNotePitch);
                break;
        }
    }

    // Listener for sending initial midi messages when the Sonivox
    // synthesizer has been started, such as program change.
    @Override
    public void onMidiStart()
    {
        // Program change - harpsicord
        /* Out of range:
            - All pipes
            - Harmonica
        */
        sendMidi(0xc0, GeneralMidiConstants.CELLO);

        // Get the config
/*
        int config[] = midi.config();
        String format =
                "maxVoices = %d\nnumChannels = %d\n" +
                        "sampleRate = %d\nmixBufferSize = %d";

        String info = String.format(format, config[0], config[1],
                config[2], config[3]);

        if (text != null)
            text.setText(info);
*/
    }

    // Send a midi message
    protected void sendMidi(int m, int p) {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) p;

        midi.write(msg);
    }

    // Send a midi message
    protected void sendMidi(int m, int n, int v) {
        byte msg[] = new byte[3];

        msg[0] = (byte) m;
        msg[1] = (byte) n;
        msg[2] = (byte) v;

        midi.write(msg);
    }

    void playNote(final int notePitch) {
        sendMidi(MidiConstants.NOTE_ON, notePitch, 63);

        // Execute code after a certain length of time
        // http://stackoverflow.com/a/3039718/399105
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                stopNote(notePitch);
            }
        }, NOTE_DURATION);
    }

    void stopNote(int notePitch) {
        sendMidi(MidiConstants.NOTE_OFF, notePitch, 63);
    }
}