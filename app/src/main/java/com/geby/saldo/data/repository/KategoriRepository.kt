package com.geby.saldo.data.repository
import android.content.Context
import com.geby.saldo.data.local.database.AppDatabase
import com.geby.saldo.data.local.database.CategoryDao
import com.geby.saldo.data.local.item.DummyItems.dummyCategoryItems
import com.geby.saldo.data.model.Category

class KategoriRepository private constructor(context: Context) {

    private val categoryDao: CategoryDao = AppDatabase.getDatabase(context).categoryDao()

    suspend fun insertDefaultIfEmpty() {
        if (categoryDao.getCount() == 0) {
            categoryDao.insertAll(dummyCategoryItems)
        }
    }

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    suspend fun getAll(): List<Category> {
        return categoryDao.getAll()
    }

    companion object {
        @Volatile
        private var INSTANCE: KategoriRepository? = null

        fun getInstance(context: Context): KategoriRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: KategoriRepository(context.applicationContext).also {
                    INSTANCE = it
                }
            }
        }
    }
}