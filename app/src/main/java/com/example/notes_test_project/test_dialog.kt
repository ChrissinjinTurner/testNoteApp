import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import com.example.notes_test_project.R
import kotlinx.android.synthetic.main.custom_note_item.view.*
import kotlinx.android.synthetic.main.test_dialog.*

class test_dialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.test_dialog, container, false)
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        editText.requestFocus()
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        dialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

        dialog.fullscreen_dialog_close.setOnClickListener {
            dismiss()
        }

        // if you click anywhere in the white area, even outside the edit text area we will focus you on the edit text
        constraintContainer.setOnClickListener {
            editText.requestFocus()
            val imm = view?.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        val child = layoutInflater.inflate(R.layout.custom_note_item, null)
        child.notedText.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat."
        contentWrapper.addView(child)
    }
}