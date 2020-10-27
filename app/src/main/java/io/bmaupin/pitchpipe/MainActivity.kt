package io.bmaupin.pitchpipe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import java.util.*
import kotlin.concurrent.schedule

private const val NOTE_DURATION: Long = 5000
private const val NOTE_VELOCITY = 95

class MainActivity : AppCompatActivity() {
    private lateinit var midi: MidiDriver
    private var stopNoteTimer = Timer()

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

    fun changePitchPipeInstrument(view: View) {
        Snackbar.make(view, view.tag.toString(), Snackbar.LENGTH_SHORT)
            .show()
    }

    fun playPitchPipeNote(view: View) {
        // Stop all previous notes
        stopNoteTimer.cancel()
        stopAllMidiNotes()

        // Play the new note
        val notePitch = view.tag.toString().toInt()
        playMidiNote(notePitch);

        // Schedule the current note to stop; midi is a streaming protocol and so the duration cannot be set when the note is played
        // https://stackoverflow.com/a/54352394/399105
        stopNoteTimer = Timer()
        stopNoteTimer.schedule(NOTE_DURATION) {
            stopAllMidiNotes()
        }
    }

    private fun playMidiNote(notePitch: Int) {
        sendMidi(MidiConstants.NOTE_ON, notePitch, NOTE_VELOCITY);
    }

    private fun stopAllMidiNotes() {
        sendMidi(MidiConstants.CONTROL_CHANGE, 123,0)
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
