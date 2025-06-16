package com.geby.saldo.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.geby.saldo.data.model.Category

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<Category>)

    @Insert
    suspend fun insert(category: Category)

    @Query("SELECT COUNT(*) FROM category")
    suspend fun getCount(): Int

    @Query("SELECT * FROM category")
    suspend fun getAll(): List<Category>
}