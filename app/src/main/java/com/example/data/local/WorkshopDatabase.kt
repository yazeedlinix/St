package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [InventoryItem::class], version = 2, exportSchema = false)
abstract class WorkshopDatabase : RoomDatabase() {
    abstract fun workshopItemDao(): WorkshopItemDao

    companion object {
        @Volatile
        private var INSTANCE: WorkshopDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): WorkshopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WorkshopDatabase::class.java,
                    "workshop_inventory_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(WorkshopDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class WorkshopDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateIfEmpty(database.workshopItemDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateIfEmpty(database.workshopItemDao())
                    }
                }
            }

            private suspend fun populateIfEmpty(itemDao: WorkshopItemDao) {
                if (itemDao.getTotalCount() == 0) {
                    itemDao.insertAll(InitialWorkshopData.getInitialItems())
                }
            }
        }
    }
}
