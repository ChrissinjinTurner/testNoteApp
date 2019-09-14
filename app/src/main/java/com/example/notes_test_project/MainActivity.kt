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
import android.widget.RelativeLayout
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
import android.view.animation.AnimationUtils


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

    private var otherHtml: String = "<p>Teach us to number our days, that we may gain a heart of wisdom. <b>PSALM 90:12 NIV</b></p>\n<h1>PRIORITY 1: My Relationship with the most amazing <u>GOD</u></h1>\n" +
            "<p>One thing I ask from the Lord, this only do I seek:<br />that I may dwell in the house of the Lord all the days of my life,<br />to gaze on the beauty of the Lord and to seek " +
            "him in his temple. <b>PSALM 27:4 NIV</b></p>\n<h1>PRIORITY 2: My Relationship with <u>ME AND MY BEST FRIEND JOHN</u></h1>\n<p>May God himself, the God of peace, sanctify you through and through. May your " +
            "whole spirit, soul and body be kept blameless at the coming of our Lord Jesus Christ. <b>1 THESSALONIANS 5:23 NIV</b></p>\n<h1>PRIORITY 3: My Relationship with <u>MY SPOUSE</u></h1>\n" +
            "<p>&ldquo;For this reason a man will leave his father and mother and be united to his wife, and the two will become one flesh.&rdquo; This is a profound mystery&mdash;but I am talking " +
            "about Christ and the church.<b> EPHESIANS 5:31-32 NIV</b></p>\n<h1>PRIORITY 4: My Relationship with <u>MY KIDS</u></h1>\n<p>Anyone who does not provide for their relatives, and especially " +
            "for their own household, has denied the faith and is worse than an unbeliever. <b>1 TIMOTHY 5:8 NIV</b></p>\n<h1>PRIORITY 5: My Relationship with <u>MY WORLD</u></h1>\n<p>For God so loved " +
            "the world that he gave his one and only Son, that whoever believes in him shall not perish but have eternal life. <b>JOHN 3:16 NIV</b></p>"

    private var highlightedRanges = arrayListOf<Array<Int>>()
    private var underlinedRanges = arrayListOf<Array<Int>>()
    private var myResponses = arrayListOf<OutlineNotesItem>()
    private var blankRanges = ArrayList<IntRange>()
    private var indicesOfRevealedBlanks = arrayListOf<Array<Int>>()


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
//                    renderOutline(items.sourceHtml)
                    renderOutline(otherHtml)
                } catch (e: Exception) {
                    Log.d("Error", "⚠️ Outline did not parse correctly")
                }
            }
            result.failure {
                Log.d("Error", "Error grabbing outline")
            }
        }

        // This is for the add note button on the bottom of the screen.
        addNoteButton.setOnClickListener {
            openDialog()
        }

        // handles the close button on the dialog popup
        notes_close_button.setOnClickListener {
            val bottomDown = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_down
            )
            val hiddenPanel = noteWrapper
            hiddenPanel.startAnimation(bottomDown)
            hiddenPanel.visibility = View.GONE
//            outlineWrapper.visibility = View.VISIBLE
            toggleKeyboard()
        }

        // handles opening the keyboard if you click on the lower part of the dialog
        constraintContainer.setOnClickListener {
            editText.requestFocus()
            toggleKeyboard(true)
        }
    }

    /**
     * This Overrides the back button allowing it to close the dialog if its visible
     * or close the app if its not (this will need to probably be edited to only close
     * the activity and go back to the outline list).
     */
    override fun onBackPressed() {
        if (noteWrapper.visibility == View.VISIBLE) { // if the view is visible hide it
            val bottomDown = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_down
            )
            val hiddenPanel = noteWrapper
            hiddenPanel.startAnimation(bottomDown)
            hiddenPanel.visibility = View.GONE
        } else { // else just close the activity
            finish()
        }
    }

    /**
     * Using this to test that the information was stored properly
     */
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
        if (selectedText != null && range != null) {
            val bottomUp = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_up
            )
            val hiddenPanel = noteWrapper
            hiddenPanel.startAnimation(bottomUp)
            hiddenPanel.visibility = View.VISIBLE
            hiddenPanel.postDelayed({
                createNote(selectedText)
            }, 250)
        } else {
            val bottomUp = AnimationUtils.loadAnimation(
                this,
                R.anim.bottom_up
            )
            val hiddenPanel = noteWrapper
            hiddenPanel.startAnimation(bottomUp)
            hiddenPanel.visibility = View.VISIBLE
            editText.requestFocus()
            hiddenPanel.postDelayed({
                toggleKeyboard(true)
            }, 450)
        }
    }

    /**
     * Creates a note item and then adds it to the dialog popup
     * It also opens the keyboard on the edit text that was just created.
     */
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

    /**
     * Helper function to allow opening and closing of the keyboard at will
     */
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
        /**
         * Use the better fromHtml if you have the correct version
         * Else use the deprecated call
         */
        val spans: SpannableString = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(sourceHtml, Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM, null, null))
        } else {
            SpannableString(Html.fromHtml(sourceHtml, null, null))
        }

        var makeBlanks = true


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
            // get the start/end point of the span
            val start = spans.getSpanStart(it)
            val end = spans.getSpanEnd(it)

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
                }

                it::class.java == StyleSpan::class.java -> { // <i> <b>

                }

                it::class.java == BulletSpan::class.java -> { // <li>
                    spans.removeSpan(it)

                    val leadingMarginSpan = LeadingMarginSpan.Standard(50)
                    spans.setSpan(leadingMarginSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

                    val bulletSpan = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        BulletSpan(30, getColor(R.color.colorPrimaryDark), 5)
                    } else {
                        BulletSpan(30, resources.getColor(R.color.colorPrimaryDark))
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

                it::class.java == UnderlineSpan::class.java -> { // <u>
                    val nbsp = "\u00A0"
                    val tempString = spans.substring(start, end)

                    tempString.replace(" ".toRegex(), nbsp)

                    spans.replaceRange(start, end + 1, tempString)

                    blankRanges.add(IntRange(start, start + tempString.length))
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

        Log.d("RANGES", "${blankRanges}")

        /**
         * This should add in the blanks over the underlined ranges
         * One thing I need to do is test some more of this before releasing. I'm not 100% sure it works
         * as well as the main one
         * Also when they go onto a second line i need to figure out what to do there, cause right now it looks
         * terrible
         */
        mainContent.addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ ->
            if (makeBlanks) {
                makeBlanks = false
                val rect = Rect(0, 0, 0, 0)
                val layout = mainContent.layout
                Log.d("Layout", "The layout was changed: blanks added, $layout")

                // iterates through the blankRanges which are set when parsing the html
                blankRanges.forEachIndexed blanks@{ blankIndex, range ->
                    val start = range.start
                    val end = range.endInclusive

                    // Get the dimensions of the box from the textview
                    rect.left = layout.getPrimaryHorizontal(start).toInt()
                    rect.right = layout.getSecondaryHorizontal(end).toInt()
                    rect.bottom = layout.getLineBaseline(layout.getLineForOffset(start))
                    rect.top = rect.bottom + layout.getLineAscent(layout.getLineForOffset(start))

                    // add some extra spacing to help not covering the whole word
                    rect.top -= 5 //Add a little extra to the top
                    rect.bottom += 15
                    rect.right += 10 //On some words a small tip was showing, this covers it
                    rect.left -= 10 // also covers on the left

                    // create the blank view that will cover the blanks
                    val blankView = RelativeLayout(this)

                    blankView.setBackgroundColor(Color.parseColor("#F0F0F0"))

                    // the width/height as well as x and y location are dependent on the rectangle.
                    val w = rect.right - rect.left
                    val h = rect.bottom - rect.top

                    blankView.layoutParams = RelativeLayout.LayoutParams(w, h)
                    // For some reason the y height is a little off without subtracting 10. Not sure why...
                    blankView.x = rect.left.toFloat() + mainContent.paddingTop - 10 // is subtracting 10 here bad?
                    blankView.y = rect.top.toFloat() + mainContent.paddingTop

                    // sets the onclick listener to the blank, also putting an animation on
                    blankView.isClickable = true
                    blankView.setOnClickListener { view ->
                        val width = view.width

                        view.animate().scaleX(0f).setDuration(500).setStartDelay(100)
                        view.animate().xBy(width.toFloat() / 2).setDuration(500).setStartDelay(100)
                            .withEndAction {
                                labelContainer.removeView(view)
                            }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            blankView.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                        }

                        // this will be used more once i get into saving the reviewed blanks.
//                    indicesOfRevealedBlanks.add(arrayOf(itemIndex, blankIndex))
                    }

                    //Add 'underline' to blank
                    val linePadding = 10
                    val theLine = View(this)
                    theLine.setBackgroundColor(Color.BLACK)
                    theLine.layoutParams = RelativeLayout.LayoutParams(w - (linePadding * 2), 4)
                    theLine.x = linePadding.toFloat()
                    theLine.y = h.toFloat() - 10f

                    // add the views into the container view
                    blankView.addView(theLine)
                    labelContainer.addView(blankView)
                    blankView.invalidate()
                    blankView.requestLayout()
                }
            }
        }
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
