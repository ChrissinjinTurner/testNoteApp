package com.example.notes_test_project

import android.text.SpannableString
import java.io.Serializable

class OutlineNotesItem(val quotedText: String,
                       val rangeInOutline: IntRange,
                       val responseText: String): Serializable