package com.monyechi.aistorysculptor.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        BookEntity::class,
        ChapterEntity::class,
        CharacterEntity::class,
        ChapterCharacterCrossRef::class,
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao
    abstract fun chapterDao(): ChapterDao
    abstract fun characterDao(): CharacterDao
}
