package com.example.notes_test_project

import java.io.Serializable

class OutlineNotesItem(val quotedText: CharSequence,
                        val rangeInOutline: Array<Int>,
                        val responseText: String): Serializable