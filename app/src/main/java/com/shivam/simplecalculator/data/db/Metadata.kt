package com.shivam.simplecalculator.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "metadata")
data class Metadata(
    @PrimaryKey val key: String,
    val value: Long
)
