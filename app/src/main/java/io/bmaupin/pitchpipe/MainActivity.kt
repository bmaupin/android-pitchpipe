package io.bmaupin.pitchpipe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import java.util.*
import kotlin.concurrent.schedule

private const val NOTE_DURATION: Long = 5000
private const val NOTE_VELOCITY = 95

class MainActivity : AppCompatActivity() {
    private lateinit var midi: MidiDriver
    private var lastNotePitch = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        midi = MidiDriver()
    }

    override fun onPause() {
        super.onPause()

        midi.stop()
    }

    override fun onResume() {
        super.onResume()

        midi.start()
        // This needs to be called every time the app is resumed, otherwise it will reset to the default instrument
        setMidiInstrument()
    }

    private fun setMidiInstrument() {
        sendMidi(MidiConstants.PROGRAM_CHANGE, GeneralMidiConstants.BAG_PIPE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the action bar menu
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_info -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onPlayButtonClick(view: View) {
        val notePitch = view.tag.toString().toInt()
        stopNote(lastNotePitch)
        lastNotePitch = notePitch
        playNote(notePitch);
    }

    private fun playNote(notePitch: Int) {
        sendMidi(MidiConstants.NOTE_ON, notePitch, NOTE_VELOCITY);

        // Execute code after a certain length of time
        // https://stackoverflow.com/a/54352394/399105
        Timer().schedule(NOTE_DURATION) {
            stopNote(notePitch)
        }
    }

    private fun stopNote(notePitch: Int) {
        sendMidi(MidiConstants.NOTE_OFF, notePitch, 0)
    }

    // Source: https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Send a midi message, 2 bytes
    private fun sendMidi(m: Byte, n: Byte) {
        val msg = ByteArray(2)
        msg[0] = m
        msg[1] = n
        midi.queueEvent(msg)
    }

    // Source: https://github.com/billthefarmer/mididriver/blob/master/app/src/main/java/org/billthefarmer/miditest/MainActivity.java
    // Send a midi message, 3 bytes
    private fun sendMidi(m: Byte, n: Int, v: Int) {
        val msg = ByteArray(3)
        msg[0] = m
        msg[1] = n.toByte()
        msg[2] = v.toByte()
        midi.queueEvent(msg)
    }
}
