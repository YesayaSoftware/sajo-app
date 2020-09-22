package software.yesaya.sajo.data.sources.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import software.yesaya.sajo.data.sources.local.dao.OwnerDao
import software.yesaya.sajo.data.sources.local.dao.TaskDao
import software.yesaya.sajo.data.sources.local.entities.Owner
import software.yesaya.sajo.data.sources.local.entities.Task

@Database(
    entities = [
        Owner::class,
        Task::class
    ],
    version = 1
)

abstract class SajoDatabase : RoomDatabase() {
    abstract val ownerDao: OwnerDao
    abstract val taskDao: TaskDao

    companion object {
        private lateinit var INSTANCE: SajoDatabase

        fun getDatabase(context: Context): SajoDatabase {
            synchronized(SajoDatabase::class.java) {
                if (!Companion::INSTANCE.isInitialized) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        SajoDatabase::class.java, "sajodatabase"
                    ).build()
                }
            }

            return INSTANCE
        }
    }
}



