package com.paulinavara.bibliotecapp

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface BooksDao {
    @Query("SELECT * FROM books")
    fun getAll(): LiveData<List<Book>>

    @Query("SELECT * FROM books WHERE idBook = :id")
    fun get(id: Int): LiveData<Book>

    @Insert
    fun insertAll(vararg books: Book)

    @Update
    fun update(book:Book)

    @Delete
    fun delete(book: Book)
}