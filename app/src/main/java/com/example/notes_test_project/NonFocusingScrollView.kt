package com.example.notes_test_project

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.widget.ScrollView
import java.util.ArrayList

class NonFocusingScrollView : ScrollView {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    override fun onRequestFocusInDescendants(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        return true
    }

    override fun getFocusables(direction: Int): ArrayList<View> {
        return ArrayList()
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        super.requestChildFocus(child, child)
    }
}
