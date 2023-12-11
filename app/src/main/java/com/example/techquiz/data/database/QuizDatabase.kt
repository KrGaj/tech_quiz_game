package com.example.techquiz.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.techquiz.data.database.dao.AnswerDao
import com.example.techquiz.data.database.dao.CategoryDao
import com.example.techquiz.data.database.dao.QuestionDao
import com.example.techquiz.data.database.dao.StatsDao
import com.example.techquiz.data.database.entity.AnswerEntity
import com.example.techquiz.data.database.entity.CategoryEntity
import com.example.techquiz.data.database.entity.PossibleAnswerEntity
import com.example.techquiz.data.database.entity.QuestionEntity

@Database(
    entities = [
        CategoryEntity::class,
        QuestionEntity::class,
        PossibleAnswerEntity::class,
        AnswerEntity::class,
       ],
    version = 1,
)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun questionDao(): QuestionDao
    abstract fun answerDao(): AnswerDao
    abstract fun statsDao(): StatsDao

    companion object {
        const val DATABASE_NAME = "quiz-database.db"
    }
}