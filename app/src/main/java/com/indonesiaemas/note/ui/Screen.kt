package com.indonesiaemas.note.ui


sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddNote : Screen("add_note")
    object EditNote : Screen("edit_note/{noteId}") {
        fun createRoute(noteId: Int) = "edit_note/$noteId"
    }
    object NoteDetail : Screen("note_detail/{noteId}") {
        fun createRoute(noteId: Int) = "note_detail/$noteId"
    }
    object Archive : Screen("archive")
    object Search : Screen("search")
    object About : Screen("about")
}