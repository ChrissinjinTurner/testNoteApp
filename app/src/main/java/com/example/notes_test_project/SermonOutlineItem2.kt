package com.example.notes_test_project

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SermonOutlineItem2(var id: Int,
                         val title: String,
                         val date: String,
                         val series: String,
                         val heroImageBackgroundUrl: String,
                         val heroImageForegroundUrl: String,
                         val sourceHtml: String,
                         val discrussionQuestions: String,
                         val reflectionGuide: String) : Serializable