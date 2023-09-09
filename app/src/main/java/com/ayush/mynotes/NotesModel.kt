package com.ayush.mynotes

class NotesModel(
    val title: String,
    val content: String,
    val time: String,
    val id: Int,
    var isExpanded: Boolean = false
)