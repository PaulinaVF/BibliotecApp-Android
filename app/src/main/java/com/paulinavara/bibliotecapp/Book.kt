package com.paulinavara.bibliotecapp

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "books")
class Book (
    val title: String,
    val author: String,
    val publisher: String,
    val year: String,
    val cover: ByteArray,
    val price: Double,
    val category: String,
    @PrimaryKey(autoGenerate = true)
    var idBook: Int = 0
    ): Serializable