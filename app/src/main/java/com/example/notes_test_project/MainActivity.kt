package com.example.notes_test_project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.*
import android.text.Html
import android.text.method.LinkMovementMethod
import android.text.method.Touch
import android.text.style.*
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import androidx.fragment.app.DialogFragment
import test_dialog
import android.widget.Toast
import android.text.style.ClickableSpan
import android.view.*
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.text.getSpans
import kotlin.math.max
import kotlin.math.min


class MainActivity : AppCompatActivity() {

    private var WIDTH = 70

    private var htmlContent: String =
//                "<img src=\"http://x-inferno.com/forum/images/xinfernoban.gif\">" +
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

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spans: SpannableString
        val parser = URLImageParser(mainContent, this)
        /**
         * Use the better fromHtml if you have the correct version
         * Else use the deprecated call
         */
        spans = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SpannableString(Html.fromHtml(htmlContent, Html.FROM_HTML_SEPARATOR_LINE_BREAK_LIST_ITEM, parser, null))
        } else {
            SpannableString(Html.fromHtml(htmlContent, parser, null))
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
            Log.d("Spans", "$it")
            // get the start/end point of the span
            val start = spans.getSpanStart(it)
            val end = spans.getSpanEnd(it)

            Log.d("POS", "Start: $start End: $end")
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
                    Log.d("Edited", "The Relative Size Span was edited")
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
                    // its own imageview (such as for the header image)
                }

                else -> { // catch all for all other spans if we want to add styling to the random spans

                }
            }
        }

        mainContent.setTextIsSelectable(true)
        mainContent.customSelectionActionModeCallback = SelectionCallback()
        mainContent.text = spans
        mainContent.movementMethod = CustomMovementMethod()

        addNoteButton.setOnClickListener {
            openDialog()
        }
    }

    private fun openDialog() {
        val dialog:DialogFragment = test_dialog()
        dialog.setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen) // makes the dialog full screen to provide all the space we could possibly need
        dialog.show(supportFragmentManager, "tag")
    }

    //
    // Text selection menu overrides (Highlighting, etc!)
    //
    @Suppress("DEPRECATION")
    inner class SelectionCallback(): ActionMode.Callback {
        override fun onActionItemClicked(actionMode: ActionMode?, menuItem: MenuItem?): Boolean {
            Log.d("TMR", "💎 onActionItemClicked")

            // add note so i need to grab the selected text, and open up the note dialog frag and allow users to input
            if (menuItem?.itemId == R.id.addNote) {
                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)
                    val clickableSpan = object : ClickableSpan() {
                        override fun onClick(view: View) {
                            addNoteButton.callOnClick()
                        }

                        override fun updateDrawState(ds: TextPaint) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ds.color = getColor(R.color.blue)
                            } else {
                                ds.color = resources.getColor(R.color.blue)
                            }
                        }
                    }
                    span.setSpan(clickableSpan, selectionStart, selectionEnd, 0)

                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }

                addNoteButton.callOnClick()

                return true
            }

            if (menuItem?.itemId == R.id.highlight) {

//                val textView = mainContent

                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)
                    span.setSpan(BackgroundColorSpan(Color.parseColor("#f5fc20")), selectionStart, selectionEnd, 0)
//                    mainContent.text = span
                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }

//                actionMode?.finish()
                return true
            }

            if (menuItem?.itemId == R.id.underline) {
//                val textView = mainContent

                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    val span = SpannableString(mainContent.text)
                    span.setSpan(UnderlineSpan(), selectionStart, selectionEnd, 0)

//                    mainContent.text = span
                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }

//                actionMode?.finish()
                return true
            }

            if (menuItem?.itemId == R.id.remove) {

//                val textView = mainContent

                val selectionStart = mainContent.selectionStart
                val selectionEnd = mainContent.selectionEnd

                if (selectionStart != -1 && selectionEnd != -1) {
                    //There's a selection between selectionStart and selectionEnd
                    Log.d("TMR", "The selection is 🖍$selectionStart to 🖍$selectionEnd")

                    // This could probably be cleaned up some, but i need to be careful as to not accidentally allow people to remove all formatting
                    val span = SpannableString(mainContent.text)
                    span.setSpan(BackgroundColorSpan(Color.TRANSPARENT), selectionStart, selectionEnd, 0)

                    val underlineSpan = span.getSpans<UnderlineSpan>(selectionStart, selectionEnd)
                    if (underlineSpan.isNotEmpty()) {
                        Log.d("REMOVE", "removed span")
                        span.removeSpan(underlineSpan[0])
                    }
//                    mainContent.text = span
                    mainContent.clearFocus()
                    mainContent.setTextKeepState(span)
                }

//                actionMode?.finish()
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


    }
}

/**
 * The Default Link Movement Method does not support having links within the selection area (weirdly)
 * This override helps to overcome that.
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
class URLImageParser(internal var container: View, internal var c: Context) : com.example.notes_test_project.Html.ImageGetter, Html.ImageGetter {

    override fun getDrawable(source: String): Drawable {
        val urlDrawable = URLDrawable()

        // get the actual source
        val asyncTask = ImageGetterAsyncTask(urlDrawable)

        asyncTask.execute(source)

        // return reference to URLDrawable where I will change with actual image from
        // the src tag
        return urlDrawable
    }

    @SuppressLint("StaticFieldLeak")
    inner class ImageGetterAsyncTask(internal var urlDrawable: URLDrawable) :
        AsyncTask<String, Void, Drawable>() {

        override fun doInBackground(vararg params: String): Drawable? {
            val source = params[0]
            return fetchDrawable(source)
        }

        override fun onPostExecute(result: Drawable) {
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, 0 + result.intrinsicWidth, 0 + result.intrinsicHeight)

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result

            // redraw the image by invalidating the container
            this@URLImageParser.container.invalidate()
        }

        /***
         * Get the Drawable from URL
         * @param urlString
         * @return
         */
        private fun fetchDrawable(urlString: String): Drawable? {
            return try {
                val `is` = fetch(urlString)
                val drawable = Drawable.createFromStream(`is`, "src")
                drawable.setBounds(0, 0, 0 + drawable.intrinsicWidth, 0 + drawable.intrinsicHeight)
                drawable
            } catch (e: Exception) {
                null
            }

        }

        @Throws(MalformedURLException::class, IOException::class)
        private fun fetch(urlString: String): InputStream {

            val url = URL(urlString)
            val urlConnection = url.openConnection() as HttpURLConnection

            return urlConnection.getInputStream()

        }
    }

    class URLDrawable : Drawable() {
        override fun setAlpha(p0: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun getOpacity(): Int {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun setColorFilter(p0: ColorFilter?) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        // the drawable that you need to set, you could set the initial drawing
        // with the loading image if you need to
        var drawable: Drawable? = null

        override fun draw(canvas: Canvas) {
            // override the draw to facilitate refresh function later
            if (drawable != null) {
                drawable!!.draw(canvas)
            }
        }
    }
}
