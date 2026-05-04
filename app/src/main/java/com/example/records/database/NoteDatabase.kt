package com.example.records.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Note::class, Folder::class, FolderNoteJoin::class], version = 6)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun folderDao(): FolderDao
    abstract fun folderNoteJoinDao(): FolderNoteJoinDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getDatabase(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NoteDatabase::class.java,
                    "note_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                    .build()
                INSTANCE = instance
                instance
            }
        }
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add the lastUpdated column with a default value
                database.execSQL("ALTER TABLE Note ADD COLUMN lastUpdated INTEGER DEFAULT 0 NOT NULL")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add encryption support columns
                database.execSQL("ALTER TABLE Note ADD COLUMN searchIndex TEXT NOT NULL DEFAULT ''")
                database.execSQL("ALTER TABLE Note ADD COLUMN isEncrypted INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add recycle bin support
                database.execSQL("ALTER TABLE Note ADD COLUMN deletedAt INTEGER")
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add pin support
                database.execSQL("ALTER TABLE Note ADD COLUMN isPinned INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add folder color support
                database.execSQL("ALTER TABLE Folder ADD COLUMN color INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
