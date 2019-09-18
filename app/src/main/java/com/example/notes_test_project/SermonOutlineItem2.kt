package com.example.notes_test_project

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SermonOutlineItem2(val id: Int,
                         val title: String,
                         val date: String,
                         val series: String,
                         val heroImageBackgroundUrl: String,
                         val heroImageForegroundUrl: String,
                         val sourceHtml: String,
                         val discrussionQuestions: ArrayList<String>,
                         val reflectionGuide: String,
                         var indicesOfRevealedBlanks: ArrayList<Int>,
                         var highlightedRanges: ArrayList<IntRange>,
                         var underlinedRanges: ArrayList<IntRange>,
                         var myResponses: ArrayList<OutlineNotesItem>,
                         var myNotes: String) : Serializable