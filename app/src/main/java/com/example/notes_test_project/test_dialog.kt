import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.example.notes_test_project.R
import kotlinx.android.synthetic.main.custom_note_item.*
import kotlinx.android.synthetic.main.custom_note_item.view.*
import kotlinx.android.synthetic.main.test_dialog.*

class test_dialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.test_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        editText.requestFocus()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

//        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        dialog.fullscreen_dialog_close.setOnClickListener {
            dismiss()
        }

        // if you click anywhere in the white area, even outside the edit text area we will focus you on the edit text
        constraintContainer.setOnClickListener {
            editText.requestFocus()
            val imm =
                view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        val selectedText = arguments?.getCharSequence("selectedText")
        if (selectedText != null) {
            createNote(selectedText)
        }

//        // creates a note item, maybe i can move this into a function?
//        val child = layoutInflater.inflate(R.layout.custom_note_item, null)
//        child.notedText.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
//        contentWrapper.addView(child)
    }

    fun newInstance(selectedText: CharSequence, range: Array<Int>): test_dialog {
        val f = test_dialog()

        // Supply num input as an argument.
        val args = Bundle()
        args.putCharSequence("selectedText", selectedText)
        f.arguments = args

        return f
    }

    /**
     * Maybe i can take in or return a reference to this and then request focus?
     * Maybe using a randomly generated noteid? or maybe i can use the range of the selected text as an ID?
     * I'm gonna need to have to give these specific names so that i can grab them nicely, they can't all have notedText and it needs to be unique at least a little.
     */
    fun createNote(text: CharSequence) {
        val child = layoutInflater.inflate(R.layout.custom_note_item, null)
        child.notedText.text = text
        child.noteBlock.setOnClickListener {
            child.noteEditText.requestFocus()
        }
        contentWrapper.addView(child)
        // maybe i can use the addview with child/index on it to put it on the bottom
        noteEditText.requestFocus()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}