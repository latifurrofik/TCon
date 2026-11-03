package com.tcon.companion.core

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class MeetingHistorySummary(
    val id: Long,
    val topic: String,
    val startedAt: Long,
    val participants: String,
    val rounds: Int
)

data class ProjectMemoryItem(
    val id: Long,
    val title: String,
    val content: String,
    val updatedAt: Long
)

data class MeetingHistoryEntry(
    val speaker: String,
    val content: String,
    val createdAt: Long
)

/**
 * Private, on-device meeting history. No transcript content is uploaded or backed up.
 */
class MeetingHistoryStore(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE meetings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                topic TEXT NOT NULL,
                started_at INTEGER NOT NULL,
                participants TEXT NOT NULL,
                rounds INTEGER NOT NULL
            )""".trimIndent()
        )
        db.execSQL(
            """CREATE TABLE entries (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                meeting_id INTEGER NOT NULL,
                speaker TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                FOREIGN KEY(meeting_id) REFERENCES meetings(id) ON DELETE CASCADE
            )""".trimIndent()
        )
        db.execSQL(
            """CREATE TABLE project_memory (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                updated_at INTEGER NOT NULL
            )""".trimIndent()
        )
        db.execSQL("CREATE INDEX idx_entries_meeting ON entries(meeting_id, id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL(
                """CREATE TABLE project_memory (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    updated_at INTEGER NOT NULL
                )""".trimIndent()
            )
        }
    }

    fun addMemory(title: String, content: String): Long {
        val values = ContentValues().apply {
            put("title", title.trim())
            put("content", content.trim())
            put("updated_at", System.currentTimeMillis())
        }
        return writableDatabase.insertOrThrow("project_memory", null, values)
    }

    fun listMemories(): List<ProjectMemoryItem> {
        val result = mutableListOf<ProjectMemoryItem>()
        readableDatabase.query(
            "project_memory", arrayOf("id", "title", "content", "updated_at"),
            null, null, null, null, "updated_at DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += ProjectMemoryItem(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3))
            }
        }
        return result
    }

    fun deleteMemory(id: Long) {
        writableDatabase.delete("project_memory", "id = ?", arrayOf(id.toString()))
    }

    fun buildProjectContext(maxCharacters: Int = 5000): String =
        listMemories().joinToString("\n") { "- ${it.title}: ${it.content}" }.take(maxCharacters)
    fun createMeeting(topic: String, participants: String, rounds: Int): Long {
        val values = ContentValues().apply {
            put("topic", topic)
            put("started_at", System.currentTimeMillis())
            put("participants", participants)
            put("rounds", rounds)
        }
        return writableDatabase.insertOrThrow("meetings", null, values)
    }

    fun appendEntry(meetingId: Long, speaker: String, content: String) {
        val values = ContentValues().apply {
            put("meeting_id", meetingId)
            put("speaker", speaker)
            put("content", content)
            put("created_at", System.currentTimeMillis())
        }
        writableDatabase.insertOrThrow("entries", null, values)
    }

    fun listMeetings(limit: Int = 50): List<MeetingHistorySummary> {
        val result = mutableListOf<MeetingHistorySummary>()
        readableDatabase.query(
            "meetings",
            arrayOf("id", "topic", "started_at", "participants", "rounds"),
            null, null, null, null, "started_at DESC", limit.toString()
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += MeetingHistorySummary(
                    id = cursor.getLong(0),
                    topic = cursor.getString(1),
                    startedAt = cursor.getLong(2),
                    participants = cursor.getString(3),
                    rounds = cursor.getInt(4)
                )
            }
        }
        return result
    }

    fun getEntries(meetingId: Long): List<MeetingHistoryEntry> {
        val result = mutableListOf<MeetingHistoryEntry>()
        readableDatabase.query(
            "entries",
            arrayOf("speaker", "content", "created_at"),
            "meeting_id = ?",
            arrayOf(meetingId.toString()),
            null, null, "id ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += MeetingHistoryEntry(
                    speaker = cursor.getString(0),
                    content = cursor.getString(1),
                    createdAt = cursor.getLong(2)
                )
            }
        }
        return result
    }

    companion object {
        private const val DATABASE_NAME = "tcon_meeting_history.db"
        private const val DATABASE_VERSION = 2
    }
}
