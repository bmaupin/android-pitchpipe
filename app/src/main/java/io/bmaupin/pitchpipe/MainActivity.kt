package io.bmaupin.pitchpipe

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ViewFlipper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import com.google.android.material.snackbar.Snackbar
import jp.kshoji.javax.sound.midi.Receiver
import jp.kshoji.javax.sound.midi.ShortMessage
import java.util.*
import kotlin.concurrent.schedule

private const val NOTE_DURATION: Long = 5000
private const val NOTE_VELOCITY = 127

class MainActivity : AppCompatActivity() {
    private var stopNoteTimer: TimerTask? = null

    private var synth: SoftSynthesizer? = null
    private var recv: Receiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        try {
            synth = SoftSynthesizer()
            synth!!.open()
            val soundFont =
                SF2Soundbank(
                    assets.open("FluidR3_GM.sf2")
                )
            synth!!.loadAllInstruments(soundFont)
            // Set the instrument
            synth!!.channels[0].programChange(22)
            recv = synth!!.receiver
        } catch (e: Exception) {
            Snackbar.make(
                findViewById(R.id.mainLayout),
                "Error loading the MIDI engine! Playing notes won't work ☹",
                Snackbar.LENGTH_INDEFINITE
            )
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
        return when (item.itemId) {
            R.id.action_info -> {
                val builder: AlertDialog.Builder? = AlertDialog.Builder(this)
                builder?.setMessage(R.string.dialog_about_content)
                val dialog: AlertDialog? = builder?.create()
                dialog?.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changePitchPipeInstrument(view: View) {
        Snackbar.make(view, view.tag.toString(), Snackbar.LENGTH_SHORT)
            .show()

        stopAllMidiNotes()

        val noteButtons = findViewById<ViewFlipper>(R.id.note_buttons)

        // The note buttons are invisible when the app first starts; make them visible when an instrument is selected
        noteButtons.visibility = View.VISIBLE;

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
        playMidiNote(notePitch)

        // Schedule the current note to stop; midi is a streaming protocol and so the duration cannot be set when the note is played
        // https://stackoverflow.com/a/54352394/399105
        stopNoteTimer = Timer().schedule(NOTE_DURATION) {
            stopAllMidiNotes()
        }
    }

    private fun playMidiNote(notePitch: Int) {
        sendMidi(ShortMessage.NOTE_ON, notePitch, NOTE_VELOCITY)
    }

    private fun stopAllMidiNotes() {
        // Cancel any currently running stopNoteTimer, otherwise it may trigger later while playing another note
        stopNoteTimer?.cancel()
        // Stop any currently playing midi notes
        sendMidi(ShortMessage.CONTROL_CHANGE, 123, 0)
    }

    // Send a midi message, 3 bytes
    private fun sendMidi(status: Int, data1: Int, data2: Int) {
        val msg = ShortMessage()
        msg.setMessage(status, data1, data2)
        recv?.send(msg, -1)
    }
}