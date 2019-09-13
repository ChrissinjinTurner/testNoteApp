package com.example.notes_test_project

import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.text.*
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.*
import android.util.Log
import android.widget.Toast
import android.text.style.ClickableSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.getSpans
import com.bumptech.glide.Glide
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.contentWrapper
import kotlinx.android.synthetic.main.custom_note_item.view.*
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    // simulates the html content that we would get from the API
    private var htmlContent: String =
                "<h1>Heading 1</h2>" +
                "<p><span style=\"font-size: 40px\">Hello there, <b>woah</b> cowboy!</p>" +
                "<p>This is some longer text and it has some <i>other formatting</i> and stuff. Pretty cool eh?</p>" +
                "<p><span style = \"color: #F04B4B;\">This is some longer text and it has some <i>other formatting</i> and stuff. Pretty cool eh?</p>" +
                "<ul>" +
                "    <li>Stuff</li>" +
                "    <li>Things. This is some longer text and it has some <i>other formatting</i> and stuff. Pretty cool eh?</li>" +
                "    <li>Smelty</li>" +
                "</ul>" +
                "<p>This is some longer text and it has some <u>other formatting</u> and stuff. Pretty cool eh?</p>" +
                "<h2>A sub heading</h2>" +
                "<blockquote>This is <a href=\"https://toemat.com\">a sub paragraph</a> and it has some <i>other formatting</i> and stuff. Pretty cool eh?</blockquote>" +
                "<blockquote>This is some longer text and it has some <i>other formatting</i> and stuff. Pretty cool eh?</blockquote>" +
                "<p>This is some longer text and it has some <i>other formatting</i> and stuff. Pretty cool eh?</p>" +
                "<p>Stuff and things.</p>" +
                "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>" +
                "<blockquote>" +
                    "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>" +
                    "<blockquote>" +
                        "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>" +
                    "</blockquote>" +
                    "<p>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</p>" +
                "</blockquote>" +
                "<p> Back to normal indent </p>"

    private var highlightedRanges = arrayListOf<Array<Int>>()
    private var underlinedRanges = arrayListOf<Array<Int>>()
    private var myResponses = arrayListOf<OutlineNotesItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        var items: SermonOutlineItem2? = null
        "https://tools.hello.sc/thomas/test-outline.json".httpGet().responseJson { _, _, result ->
            result.success {
                Log.d("Success", "Grabbed Outline")
                try {
                    val items = Gson().fromJson(it.content, SermonOutlineItem2::class.java)
                    // I'm gonna set the images manually now, but once i'm back on the main project glide should work
//                    Glide.with(this).load(items?.heroImageForegroundUrl).into(headerForegroundImage)
//                    Glide.with(this).load(items?.heroImageBackgroundUrl).into(headerBackgroundImage)
                    outlineTitle.text = items.title
                    outlineDate.text = items.date
                    renderOutline(items.sourceHtml)
                } catch (e: Exception) {
                    Log.d("Error", "⚠️ Outline did not parse correctly")
                }
            }
            result.failure {
                Log.d("Error", "Error grabbing outline")
            }
        }



        // This will render the outline as long as you provide the base html
        // The base html will either be provided from the API if this is your first time opening
        // or from the prefs file if you have opened this before.
//        renderOutline(htmlContent)

        // This is for the add note button on the bottom of the screen.
        addNoteButton.setOnClickListener {
            openDialog()
        }

        // the more button which will be used for other features like scanning and sharing maybe?
        moreButton.setOnClickListener {
            val popupMenu = PopupMenu(this, moreButton)
            popupMenu.menuInflater.inflate(R.menu.sermon_outline_more_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.shareNotes -> {
                        Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
                    }
                    R.id.scanNotes -> {
                        Toast.makeText(this@MainActivity, item.title, Toast.LENGTH_SHORT).show()
                    }
                }

                true
            }

            popupMenu.show()
        }

        notes_close_button.setOnClickListener {
            noteWrapper.visibility = View.GONE
            outlineWrapper.visibility = View.VISIBLE
            toggleKeyboard()
        }

        constraintContainer.setOnClickListener {
            editText.requestFocus()
            toggleKeyboard(true)
        }
    }

    override fun onBackPressed() {
        if (noteWrapper.visibility == View.VISIBLE) {
            noteWrapper.visibility = View.GONE
            outlineWrapper.visibility = View.VISIBLE
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        // These are initialized and added to when you add an underline or highlight
        if (underlinedRanges.isNotEmpty()) {
            Log.d("Underline Ranges", "${underlinedRanges[0][0]}, ${underlinedRanges[0][1]}")
        }
        if (highlightedRanges.isNotEmpty()) {
            Log.d("highlighted Ranges", "${highlightedRanges[0][0]}, ${highlightedRanges[0][1]}")
        }
    }

    /**
     * Opens the dialog for the notes
     */
    private fun openDialog(selectedText:CharSequence? = null, range: Array<Int>? = null) {
//        val dialog: DialogFragment? = if (selectedText != null) {
//            range?.let { test_dialog().newInstance(selectedText, it) }
//        } else {
//            test_dialog()
//        }
//        dialog?.setStyle(DialogFragment.STYLE_NORMAL, R.style.AlertDialogStyle) // makes the dialog full screen to provide all the space we could possibly need
//        dialog?.show(supportFragmentManager, "tag")

        // optional showing dialog within view
        if (selectedText != null && range != null) {
            noteWrapper.visibility = View.VISIBLE
            outlineWrapper.visibility = View.GONE
            createNote(selectedText)
        } else {
            noteWrapper.visibility = View.VISIBLE
            outlineWrapper.visibility = View.GONE
            editText.requestFocus()
            toggleKeyboard(true)
        }
    }

    private fun createNote(text: CharSequence) {
        val child = layoutInflater.inflate(R.layout.custom_note_item, null)
        child.notedText.text = text
        child.noteBlock.setOnClickListener {
            child.noteEditText.requestFocus()
        }
        contentWrapper.addView(child)

        child.noteEditText.requestFocus()
        toggleKeyboard(true)
    }

    private fun toggleKeyboard(open: Boolean = false) {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (imm?.isActive as Boolean && open) {
            Log.d("SHOW", "keyboard shown")
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) // Show
        } else {
            Log.d("HIDE", "keyboard hidden")
            imm.hideSoftInputFromWindow(editText.applicationWindowToken, InputMethodManager.HIDE_IMPLICIT_ONLY) // hide
        }
    }

    /**
     * Renders the outline and sets it into the textview located within the view
     */
    @Suppress("DEPRECATION")
    private fun renderOutline(sourceHtml: String) {
        val spans: SpannableString
//        val parser = URLImageParser(mainContent, this)
        /**
         * Use the better fromHtml if you have the correct version
         * Else use the deprecated call
         */
        spans = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(sourceHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM, null, null))
        } else {
            SpannableString(Html.fromHtml(sourceHtml, null, null))
        }


        val spansArray = spans.getSpans(0, spans.length, Any::class.java)

        /**
         * <p>: doesn't look like any span is set for these
         * <blockquote>: QuoteSpan
         * <h1><h2>: RelativeSizeSpan
         * <ul><li>: BulletSpan
         * <b>: StyleSpan
         * <i>: StyleSpan
         * color: ForegroundColorSpan
         * <u>: UnderlineSpan
         * <img>: URLSpan
         */
        spansArray.forEach {
//            Log.d("Spans", "$it")
            // get the start/end point of the span
            val start = spans.getSpanStart(it)
            val end = spans.getSpanEnd(it)

//            Log.d("POS", "Start: $start End: $end")
            // I could add a check for a clickablespan and then change the text color to change the color of links maybe?
            when {
                it::class.java == RelativeSizeSpan::class.java -> { // <h1> <h2>...
                    // remove the original span so that we can customize what is put in
                    spans.removeSpan(it)

                    // up first is a custom font, we are using sandals type
                    val font = Typeface.createFromAsset(assets, "sandals_type.ttf")
                    val customTypeFaceSpan = CustomTypefaceSpan(font)
                    spans.setSpan(customTypeFaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    // then since these are used for headers we will make them bigger
                    val relativeSizeSpan = RelativeSizeSpan(1.8f)
                    spans.setSpan(relativeSizeSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//                    Log.d("Edited", "The Relative Size Span was edited")
                }

                it::class.java == StyleSpan::class.java -> { // <i> <b>

                }

                it::class.java == BulletSpan::class.java -> { // <li>
                    spans.removeSpan(it)

                    val leadingMarginSpan = LeadingMarginSpan.Standard(50)
                    spans.setSpan(leadingMarginSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    val bulletSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        BulletSpan(30, getColor(R.color.colorAccent), 5)
                    } else {
                        BulletSpan(30, resources.getColor(R.color.colorAccent))
                    }
                    spans.setSpan(bulletSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                it::class.java == QuoteSpan::class.java -> { // <blockquote>
                    // first we will remove the original QuoteSpan
                    spans.removeSpan(it)

                    val leadingMarginSpan = LeadingMarginSpan.Standard(50)
                    spans.setSpan(leadingMarginSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }

                it::class.java == ImageSpan::class.java -> { // <img>
                    // if i can't get images to work here i can just whenever i find an image span remove it and display the image in
                    // its own imageview (such as for the header image, but we could also do this before parsing the span)
                    // just have the layout for the notes include an image position and a title position
                }

                else -> { // catch all for all other spans if we want to add styling to the random spans, which we probably won't

                }
            }
        }

        mainContent.setTextIsSelectable(true)
        mainContent.customSelectionActionModeCallback = SelectionCallback()
        mainContent.text = spans
//        mainContent.movementMethod = CustomMovementMethod()
        mainContent.movementMethod = LinkMovementMethod.getInstance()
    }







    //========================= Inner Classes ======================================================

    /**
     * Handles Menu selection items onClick
     */
    @Suppress("DEPRECATION")
    inner class SelectionCallback : ActionMode.Callback {
        override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {
//            Log.d("TMR", "💎 onActionItemClicked")

            // add note so i need to grab the selected text, and open up the note dialog frag and allow users to input
            if (menuItem?.itemId == R.id.addNote) {
                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)
                    val clickableSpan = object : ClickableSpan() {
                        // here i need to the adding a view, maybe moving it to a public function call?
                        // It should also select the edittext within the quoted item.
                        override fun onClick(view: View) {
                            addNoteButton.callOnClick()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ds.color = getColor(R.color.greyColor)
                            } else {
                                ds.color = resources.getColor(R.color.greyColor)
                            }
                        }
                    }
                    span.setSpan(clickableSpan, selectionStart, selectionEnd, 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        span.setSpan(BackgroundColorSpan(getColor(R.color.blue)), selectionStart, selectionEnd, 0)
                    } else {
                        span.setSpan(BackgroundColorSpan(resources.getColor(R.color.blue)), selectionStart, selectionEnd, 0)
                    }

                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                    val selectedString = span.subSequence(selectionStart, selectionEnd)
                    openDialog(selectedString, arrayOf(selectionStart, selectionEnd))

                    myResponses.add(OutlineNotesItem(selectedString, arrayOf(selectionStart, selectionEnd), ""))
                }
                return true
            }

            if (menuItem?.itemId == R.id.highlight) {
                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)

                    val clickableSpan = object : ClickableSpan() {
                        // here i need to the adding a view, maybe moving it to a public function call?
                        // It should also select the edittext within the quoted item.
                        override fun onClick(view: View) {
                            // removes it from the array
                            highlightedRanges.forEach {
                                if (it[0] == selectionStart && it[1] == selectionEnd) {
                                    highlightedRanges.remove(it)
                                }
                            }
                            alertToRemoveSpan("highlight", selectionStart, selectionEnd)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ds.color = getColor(R.color.greyColor)
                            } else {
                                ds.color = resources.getColor(R.color.greyColor)
                            }
                        }
                    }
                    span.setSpan(clickableSpan, selectionStart, selectionEnd, 0)

                    span.setSpan(BackgroundColorSpan(Color.parseColor("#f5fc20")), selectionStart, selectionEnd, 0)

                    highlightedRanges.add(arrayOf(selectionStart, selectionEnd))

                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }
                return true
            }

            if (menuItem?.itemId == R.id.underline) {
                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)

                    val clickableSpan = object : ClickableSpan() {
                        // here i need to the adding a view, maybe moving it to a public function call?
                        // It should also select the edittext within the quoted item.
                        override fun onClick(view: View) {
                            // removes it from the array
                            underlinedRanges.forEach {
                                if (it[0] == selectionStart && it[1] == selectionEnd) {
                                    underlinedRanges.remove(it)
                                }
                            }
                            alertToRemoveSpan("underline", selectionStart, selectionEnd)
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ds.color = getColor(R.color.greyColor)
                            } else {
                                ds.color = resources.getColor(R.color.greyColor)
                            }
                        }
                    }
                    span.setSpan(clickableSpan, selectionStart, selectionEnd, 0)

                    span.setSpan(UnderlineSpan(), selectionStart, selectionEnd, 0)

                    underlinedRanges.add(arrayOf(selectionStart, selectionEnd))

                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }
                return true
            }

            return false
        }

        override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            if (menu != null && actionMode != null) {
                actionMode.menuInflater.inflate(R.menu.sermon_outline_selection_menu, menu)
            }

            return true
        }

        override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
            menu?.removeItem(android.R.id.selectAll)
            return false
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            //Nothing to do
        }

        /**
         * Helper function to create dialog that will ask whether you should remove span or not.
         */
        private fun alertToRemoveSpan(typeOfSpan: String, start: Int, end: Int) {
            val alertTitle = if (typeOfSpan == "highlight") {
                "Remove Highlight?"
            } else {
                "Remove Underline?"
            }

            val alertMessage = if (typeOfSpan == "highlight") {
                "Are you sure you want to remove the highlight?"
            } else {
                "Are you sure you want to remove the underline?"
            }
            // alert to ask whether they want to remove the span or not
            AlertDialog.Builder(this@MainActivity)
                .setTitle(alertTitle)
                .setMessage(alertMessage)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes") { dialog, which ->
                    // Continue with delete operation
                    val span = SpannableString(mainContent.text)

                    // remove the clickable span so that this section is no longer clickable
                    val clickableSpan = span.getSpans<ClickableSpan>(start, end)
                    if (clickableSpan.isNotEmpty()) {
                        // if there is an overlap check to see which one to remove
                        if (clickableSpan.size > 1 && span.getSpanStart(clickableSpan[0]) != start) {
                            span.removeSpan(clickableSpan[1])
                        } else {
                            span.removeSpan(clickableSpan[0])
                        }
                    }

                    if (start != -1 && end != -1) { // we want the span bounds to be valid
                        if (typeOfSpan == "highlight") { // if its a highlight set the background to transparent (not sure if this actually removes the span)
                            // might be better to use what i do in the other ones and find the span and then remove it
                            val highlightSpan = span.getSpans<BackgroundColorSpan>(start, end)
                            if (highlightSpan.isNotEmpty()) {
                                if (highlightSpan.size > 1 && span.getSpanStart(highlightSpan[0]) != start) {
                                    span.removeSpan(highlightSpan[1]) // for some reason this removes all of them despite there being another highlightspan
                                } else {
                                    span.removeSpan(highlightSpan[0])
                                }
                            }
                            span.setSpan(BackgroundColorSpan(Color.TRANSPARENT), start, end, 0)
                        } else if (typeOfSpan == "underline") { // underline, find the span using getSpans and then remove it
                            val underlineSpan = span.getSpans<UnderlineSpan>(start, end)
                            if (underlineSpan.isNotEmpty()) {
                                Log.d("REMOVE", "removed span")
                                // if the link is greater than 1 you highlighted a link or highlighted over another item
                                if (underlineSpan.size > 1 && span.getSpanStart(underlineSpan[0]) != start) { // I need to do more testing on whether this actually blocks it from removing the link at all times
                                    span.removeSpan(underlineSpan[1])
                                } else {
                                    span.removeSpan(underlineSpan[0])
                                }
                            }

                        }

                        // reset the updated text into the textview
                        mainContent.clearFocus()
                        mainContent.setTextKeepState(span)
                    }
                }
                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }
}

//============================= Nested Classes =====================================================
// These could be moved either into a seperate file or nested inside depending on what kind of clarity
// i want in the files.

/**
 * The Default Link Movement Method does not support having links within a selectable textview (weirdly, not sure why)
 * This override helps to overcome that.
 *
 * may not be needed as i just tested with a different movement method
 */
class CustomMovementMethod : LinkMovementMethod() {
    override fun canSelectArbitrarily (): Boolean {
        return true
    }

    override fun initialize(widget: TextView, text: Spannable) {
        Selection.setSelection(text, text.length)
    }

    override fun onTouchEvent(widget: TextView, buffer: Spannable, event: MotionEvent) : Boolean {
        val action = event.action

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            var x = event.x
            var y = event.y.toInt()

            x -= widget.totalPaddingLeft
            y -= widget.totalPaddingTop

            x += widget.scrollX
            y += widget.scrollY

            val layout = widget.layout
            val line = layout.getLineForVertical(y)
            val off = layout.getOffsetForHorizontal(line, x)

            var link = buffer.getSpans(off, off, ClickableSpan::class.java)

            if (link.isNotEmpty()) {
                if (action == MotionEvent.ACTION_UP) {
                    link[0].onClick(widget)
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer, buffer.getSpanStart(link[0]), buffer.getSpanEnd(link[0]))
                }
                return true
            }
        }
        return Touch.onTouchEvent(widget, buffer, event)
    }
}

/**
 * Helps with parsing urls for images, without this images will not display at all
 */
//class URLImageParser(internal var container: View, internal var c: Context) : com.example.notes_test_project.Html.ImageGetter, Html.ImageGetter {
//
//    override fun getDrawable(source: String): Drawable {
//        val urlDrawable = URLDrawable()
//
//        // get the actual source
//        val asyncTask = ImageGetterAsyncTask(urlDrawable)
//
//        asyncTask.execute(source)
//
//        // return reference to URLDrawable where I will change with actual image from
//        // the src tag
//        return urlDrawable
//    }
//
//    @SuppressLint("StaticFieldLeak")
//    inner class ImageGetterAsyncTask(internal var urlDrawable: URLDrawable) :
//        AsyncTask<String, Void, Drawable>() {
//
//        override fun doInBackground(vararg params: String): Drawable? {
//            val source = params[0]
//            return fetchDrawable(source)
//        }
//
//        override fun onPostExecute(result: Drawable) {
//            // set the correct bound according to the result from HTTP call
//            urlDrawable.setBounds(0, 0, 0 + result.intrinsicWidth, 0 + result.intrinsicHeight)
//
//            // change the reference of the current drawable to the result
//            // from the HTTP call
//            urlDrawable.drawable = result
//
//            // redraw the image by invalidating the container
//            this@URLImageParser.container.invalidate()
//        }
//
//        /***
//         * Get the Drawable from URL
//         * @param urlString
//         * @return
//         */
//        private fun fetchDrawable(urlString: String): Drawable? {
//            return try {
//                val `is` = fetch(urlString)
//                val drawable = Drawable.createFromStream(`is`, "src")
//                drawable.setBounds(0, 0, 0 + drawable.intrinsicWidth, 0 + drawable.intrinsicHeight)
//                drawable
//            } catch (e: Exception) {
//                null
//            }
//
//        }
//
//        @Throws(MalformedURLException::class, IOException::class)
//        private fun fetch(urlString: String): InputStream {
//
//            val url = URL(urlString)
//            val urlConnection = url.openConnection() as HttpURLConnection
//
//            return urlConnection.getInputStream()
//
//        }
//    }
//
//    class URLDrawable : Drawable() {
//        override fun setAlpha(p0: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getOpacity(): Int {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun setColorFilter(p0: ColorFilter?) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        // the drawable that you need to set, you could set the initial drawing
//        // with the loading image if you need to
//        var drawable: Drawable? = null
//
//        override fun draw(canvas: Canvas) {
//            // override the draw to facilitate refresh function later
//            if (drawable != null) {
//                drawable!!.draw(canvas)
//            }
//        }
//    }
//}
