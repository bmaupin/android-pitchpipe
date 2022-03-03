package io.bmaupin.pitchpipe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ViewFlipper
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import com.google.android.material.snackbar.Snackbar
import jp.kshoji.javax.sound.midi.Receiver
import jp.kshoji.javax.sound.midi.ShortMessage
import org.billthefarmer.mididriver.GeneralMidiConstants
import org.billthefarmer.mididriver.MidiConstants
import org.billthefarmer.mididriver.MidiDriver
import java.util.*
import kotlin.concurrent.schedule

private const val NOTE_DURATION: Long = 5000
private const val NOTE_VELOCITY = 127

class MainActivity : AppCompatActivity() {
//    TODO
    private lateinit var midi: MidiDriver
    private var stopNoteTimer: TimerTask? = null

    private var synth: SoftSynthesizer? = null
    private var recv: Receiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

//        midi = MidiDriver()

        try {
            val soundFont: SF2Soundbank =
                SF2Soundbank(
                    assets.open("SmallTimGM6mb.sf2")
                )
            synth = SoftSynthesizer()
            synth!!.open()
            synth!!.loadAllInstruments(soundFont)
            synth!!.channels[0].programChange(0)
            recv = synth!!.receiver
        } catch (e: Exception) {
            Snackbar.make(findViewById(R.id.mainLayout), "Error loading the MIDI engine! Playing notes won't work ☹", Snackbar.LENGTH_INDEFINITE)
                .show()
            e.printStackTrace()
        }
    }

    override fun onPause() {
        super.onPause()
        stopAllMidiNotes()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAllMidiNotes()
        synth?.close()
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

        stopAllMidiNotes()

        val noteButtons = findViewById<ViewFlipper>(R.id.note_buttons)
        var index = 0
        when (view.tag.toString()) {
            getString(R.string.button_tag_banjo) -> {
                val noteButtonsBanjo = findViewById<ConstraintLayout>(R.id.note_buttons_banjo)
                index = noteButtons.indexOfChild(noteButtonsBanjo)
            }

            getString(R.string.button_tag_guitar) -> {
                val noteButtonsGuitar = findViewById<ConstraintLayout>(R.id.note_buttons_guitar)
                index = noteButtons.indexOfChild(noteButtonsGuitar)
            }

            getString(R.string.button_tag_mandolin) -> {
                val noteButtonsMandolin = findViewById<ConstraintLayout>(R.id.note_buttons_mandolin)
                index = noteButtons.indexOfChild(noteButtonsMandolin)
            }

            getString(R.string.button_tag_ukulele) -> {
                val noteButtonsUkulele = findViewById<ConstraintLayout>(R.id.note_buttons_ukulele)
                index = noteButtons.indexOfChild(noteButtonsUkulele)
            }
        }
        noteButtons.displayedChild = index
    }

    fun playPitchPipeNote(view: View) {
        // Stop all previous notes
        stopAllMidiNotes()

        // Play the new note
        val notePitch = view.tag.toString().toInt()
        playMidiNote(notePitch);

        // Schedule the current note to stop; midi is a streaming protocol and so the duration cannot be set when the note is played
        // https://stackoverflow.com/a/54352394/399105
        stopNoteTimer = Timer().schedule(NOTE_DURATION){
            stopAllMidiNotes()
        }
    }

    private fun playMidiNote(notePitch: Int) {
        sendMidi(ShortMessage.NOTE_ON, notePitch, NOTE_VELOCITY);
    }

    private fun stopAllMidiNotes() {
        // cancel any currently running stopNoteTimer, otherwise it may trigger later while playing another note
        stopNoteTimer?.cancel()
        // stop any currently playing midi notes
        sendMidi(ShortMessage.CONTROL_CHANGE, 123,0)
    }

    // Send a midi message, 3 bytes
    private fun sendMidi(status: Int, data1: Int, data2: Int) {
        val msg = ShortMessage()
        msg.setMessage(status, data1, data2)
        recv?.send(msg, -1)
    }
}