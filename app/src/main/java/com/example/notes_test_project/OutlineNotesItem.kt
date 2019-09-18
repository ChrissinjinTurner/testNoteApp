package com.example.notes_test_project

import android.text.SpannableString
import java.io.Serializable

class OutlineNotesItem(var quotedText: String,
                       var rangeInOutline: IntRange,
                       var responseText: String): Serializable