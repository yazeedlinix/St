package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkshopItemDao {
    @Query("SELECT * FROM inventory_items ORDER BY id DESC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE categoryCode = :categoryCode ORDER BY id DESC")
    fun getItemsByCategory(categoryCode: String): Flow<List<InventoryItem>>

    @Query("""
        SELECT * FROM inventory_items 
        WHERE name LIKE '%' || :query || '%' 
           OR sku LIKE '%' || :query || '%' 
           OR storageLocation LIKE '%' || :query || '%'
           OR technicalSpecs LIKE '%' || :query || '%'
        ORDER BY id DESC
    """)
    fun searchItems(query: String): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE sku = :sku LIMIT 1")
    suspend fun getItemBySku(sku: String): InventoryItem?

    @Query("SELECT COUNT(*) FROM inventory_items WHERE categoryCode = :categoryCode")
    suspend fun getCountByCategory(categoryCode: String): Int

    @Query("SELECT COUNT(*) FROM inventory_items")
    suspend fun getTotalCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: InventoryItem): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<InventoryItem>)

    @Update
    suspend fun updateItem(item: InventoryItem)

    @Delete
    suspend fun deleteItem(item: InventoryItem)

    @Query("DELETE FROM inventory_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE inventory_items SET quantity = :newQuantity WHERE id = :id")
    suspend fun updateQuantity(id: Long, newQuantity: Int)
}
