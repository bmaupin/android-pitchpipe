package io.bmaupin.pitchpipe

import android.content.res.Configuration
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ViewFlipper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import cn.sherlock.com.sun.media.sound.SF2Soundbank
import cn.sherlock.com.sun.media.sound.SoftSynthesizer
import com.google.android.material.button.MaterialButton
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

        // Dynamically set instrument button size
        val instrumentButtons = findViewById<LinearLayout>(R.id.instrument_buttons)
        instrumentButtons.doOnLayout {
            // Use a slightly different calculation depending on the screen orientation
            val newHeight =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    // Start with the height of the parent layout
                    ((instrumentButtons.height -
                            // Subtract 10% top/bottom margin
                            (instrumentButtons.height * 0.1 * 2)) /
                            // Divide by number of buttons
                            instrumentButtons.childCount).toInt()
                } else {
                    // Start with the width of the parent layout
                    ((instrumentButtons.width -
                            // Use a slightly bigger margin
                            (instrumentButtons.width * 0.15 * 2)) /
                            // Divide by number of buttons
                            instrumentButtons.childCount).toInt()
                }

            for (instrumentButton in instrumentButtons.children) {
                instrumentButton.updateLayoutParams {
                    height = newHeight
                    width = newHeight
                }

                if (instrumentButton is MaterialButton) {
                    instrumentButton.iconSize = (newHeight * 0.6).toInt()
                }
            }
        }

        // Dynamically set note button size
        val noteButtonsViewFlipper = findViewById<ViewFlipper>(R.id.note_buttons)
        noteButtonsViewFlipper.doOnLayout {
            // Get the height to use by taking the measurements from the guitar note buttons, because it has the most buttons
            val guitarNoteButtons = findViewById<LinearLayout>(R.id.note_buttons_guitar)

            // Yikes (https://stackoverflow.com/a/8780360/399105)
            val outValue = TypedValue()
            resources.getValue(R.dimen.note_button_text_size_percentage, outValue, true)
            val noteButtonTextSizePercentage = outValue.float

            // This is more or less copied from the instrument buttons, above
            val newHeight =
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    ((guitarNoteButtons.height -
                            (guitarNoteButtons.height * 0.1 * 2)) /
                            guitarNoteButtons.childCount).toInt()
                } else {
                    ((guitarNoteButtons.width -
                            (guitarNoteButtons.width * 0.15 * 2)) /
                            guitarNoteButtons.childCount).toInt()
                }

            // Now apply the height to all of the note buttons
            for (noteButtonsLayout in noteButtonsViewFlipper.children) {
                if (noteButtonsLayout is LinearLayout) {
                    for (noteButton in noteButtonsLayout.children) {
                        noteButton.updateLayoutParams {
                            height = newHeight
                            width = newHeight
                        }

                        if (noteButton is Button) {
                            noteButton.setTextSize(
                                TypedValue.COMPLEX_UNIT_PX,
                                newHeight * noteButtonTextSizePercentage
                            )
                        }
                    }
                }
            }
        }

        // Restore the group of displayed note buttons if the screen orientation is changed (https://stackoverflow.com/a/7118495/399105)
        if (savedInstanceState != null) {
            val noteButtonsDisplayedIndex =
                savedInstanceState.getInt("NOTE_BUTTONS_DISPLAYED_INDEX")
            noteButtonsViewFlipper.displayedChild = noteButtonsDisplayedIndex
            val noteButtonsVisibility = savedInstanceState.getInt("NOTE_BUTTONS_VISIBILITY")
            noteButtonsViewFlipper.visibility = noteButtonsVisibility
        }

        try {
            synth = SoftSynthesizer()
            synth!!.open()
            val soundFont =
                SF2Soundbank(
                    assets.open("GeneralUser GS v1.471.sf2")
                )
            synth!!.loadAllInstruments(soundFont)
            // Set the instrument
            synth!!.channels[0].programChange(4)
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

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)

        // Save which group of note buttons that is being displayed when the screen orientation is changed (https://stackoverflow.com/a/7118495/399105)
        val noteButtons = findViewById<ViewFlipper>(R.id.note_buttons)
        val noteButtonsDisplayedIndex: Int = noteButtons.displayedChild
        savedInstanceState.putInt("NOTE_BUTTONS_DISPLAYED_INDEX", noteButtonsDisplayedIndex)
        // We also need to save the visibility state
        val noteButtonsVisibility: Int = noteButtons.visibility
        savedInstanceState.putInt("NOTE_BUTTONS_VISIBILITY", noteButtonsVisibility)
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
                val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                builder.setMessage(R.string.dialog_about_content)
                val dialog: AlertDialog = builder.create()
                dialog.show()

                // Make the HTML links in the dialog clickable. Must be called after show()
                // (https://stackoverflow.com/a/3367392/399105)
                (dialog.findViewById<TextView>(android.R.id.message)!!).movementMethod =
                    LinkMovementMethod.getInstance()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun changePitchPipeInstrument(view: View) {
        stopAllMidiNotes()

        val noteButtons = findViewById<ViewFlipper>(R.id.note_buttons)

        // The note buttons are invisible when the app first starts; make them visible when an instrument is selected
        noteButtons.visibility = View.VISIBLE

        var index = 0
        when (view.tag.toString()) {
            getString(R.string.button_tag_banjo) -> {
                val noteButtonsBanjo = findViewById<LinearLayout>(R.id.note_buttons_banjo)
                index = noteButtons.indexOfChild(noteButtonsBanjo)
            }

            getString(R.string.button_tag_guitar) -> {
                val noteButtonsGuitar = findViewById<LinearLayout>(R.id.note_buttons_guitar)
                index = noteButtons.indexOfChild(noteButtonsGuitar)
            }

            getString(R.string.button_tag_mandolin) -> {
                val noteButtonsMandolin = findViewById<LinearLayout>(R.id.note_buttons_mandolin)
                index = noteButtons.indexOfChild(noteButtonsMandolin)
            }

            getString(R.string.button_tag_ukulele) -> {
                val noteButtonsUkulele = findViewById<LinearLayout>(R.id.note_buttons_ukulele)
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