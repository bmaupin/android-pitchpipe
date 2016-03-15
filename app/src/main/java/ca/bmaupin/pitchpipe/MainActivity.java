package ca.bmaupin.pitchpipe;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.billthefarmer.mididriver.MidiDriver;


public class MainActivity extends Activity    implements View.OnTouchListener, View.OnClickListener,
        MidiDriver.OnMidiStartListener
{
    private TextView text;

    protected MidiDriver midi;
    protected MediaPlayer player;

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
            v.setOnTouchListener(this);

        v = findViewById(R.id.button2);
        if (v != null)
            v.setOnTouchListener(this);

/*
        v = findViewById(R.id.button3);
        if (v != null)
            v.setOnClickListener(this);

        v = findViewById(R.id.button4);
        if (v != null)
            v.setOnClickListener(this);
*/

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

    // On touch

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();
        int id = v.getId();

        switch (action)
        {
            // Down

            case MotionEvent.ACTION_DOWN:
                switch (id)
                {
                    case R.id.button1:
                        sendMidi(0x90, 48, 63);
                        sendMidi(0x90, 52, 63);
                        sendMidi(0x90, 55, 63);
                        break;

                    case R.id.button2:
                        sendMidi(0x90, 55, 63);
                        sendMidi(0x90, 59, 63);
                        sendMidi(0x90, 62, 63);
                        break;

                    default:
                        return false;
                }
                break;

            // Up

            case MotionEvent.ACTION_UP:
                switch (id)
                {
                    case R.id.button1:
                        sendMidi(0x80, 48, 0);
                        sendMidi(0x80, 52, 0);
                        sendMidi(0x80, 55, 0);
                        break;

                    case R.id.button2:
                        sendMidi(0x80, 55, 0);
                        sendMidi(0x80, 59, 0);
                        sendMidi(0x80, 62, 0);
                        break;

                    default:
                        return false;
                }
                break;

            default:
                return false;
        }

        return false;
    }

    // On click

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        switch (id)
        {
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

        sendMidi(0xc0, 6);

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
}
