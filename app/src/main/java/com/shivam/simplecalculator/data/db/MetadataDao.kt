package com.shivam.simplecalculator.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MetadataDao {
    @Query("SELECT value FROM metadata WHERE `key` = :key")
    suspend fun getValue(key: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(metadata: Metadata)
}
