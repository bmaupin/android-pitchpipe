package ca.bmaupin.pitchpipe;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.GeneralMidiConstants;
import org.billthefarmer.mididriver.MidiConstants;
import org.billthefarmer.mididriver.MidiDriver;


public class MainActivity extends Activity    implements View.OnClickListener,
        MidiDriver.OnMidiStartListener
{
    private TextView text;

    protected MidiDriver midi;
    protected MediaPlayer player;

    // TODO: add a setting to change this
    private static final int NOTE_DURATION = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create midi driver

        midi = new MidiDriver();

        // Set on touch listener

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

//        text = (TextView)findViewById(R.id.textView2);

        // Set on midi start listener

        if (midi != null)
            midi.setOnMidiStartListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // On resume

    @Override
    protected void onResume()
    {
        super.onResume();

        // Start midi

        if (midi != null)
            midi.start();
    }

    // On pause

    @Override
    protected void onPause()
    {
        super.onPause();

        // Stop midi

        if (midi != null)
            midi.stop();

        // Stop player

        if (player != null)
            player.stop();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
            // 40, 45, 50, 55, 59, 64
            case R.id.button1:
                playNote(40);
                break;

            case R.id.button2:
                playNote(45);
                break;

            case R.id.button3:
                playNote(50);
                break;

            case R.id.button4:
                playNote(55);
                break;

            case R.id.button5:
                playNote(59);
                break;

            case R.id.button6:
                playNote(64);
                break;

/*
            case R.id.button3:
                if (player != null)
                {
                    player.stop();
                    player.release();
                }

                player = MediaPlayer.create(this, R.raw.ants);
                player.start();
                break;

            case R.id.button4:
                if (player != null)
                    player.stop();
                break;
*/
        }
    }

    // Listener for sending initial midi messages when the Sonivox
    // synthesizer has been started, such as program change.

    @Override
    public void onMidiStart()
    {
        // Program change - harpsicord

        sendMidi(0xc0, GeneralMidiConstants.CHURCH_ORGAN);

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

    protected void sendMidi(int m, int p)
    {
        byte msg[] = new byte[2];

        msg[0] = (byte) m;
        msg[1] = (byte) p;

        midi.write(msg);
    }

    // Send a midi message

    protected void sendMidi(int m, int n, int v)
    {
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
                sendMidi(MidiConstants.NOTE_OFF, notePitch, 63);
            }
        }, NOTE_DURATION);
    }
}
