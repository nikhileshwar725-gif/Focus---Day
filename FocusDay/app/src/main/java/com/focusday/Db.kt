package com.focusday

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val date: String,          // yyyy-MM-dd
    val startTime: String?,    // HH:mm, optional
    val done: Boolean = false
)

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE date = :date ORDER BY startTime, id")
    fun forDate(date: String): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Query("UPDATE tasks SET done = :done WHERE id = :id")
    suspend fun setDone(id: Long, done: Boolean)
}

@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class AppDb : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        fun build(ctx: Context): AppDb =
            Room.databaseBuilder(ctx.applicationContext, AppDb::class.java, "focusday.db").build()
    }
}
