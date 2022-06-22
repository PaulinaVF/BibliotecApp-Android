package com.paulinavara.bibliotecapp

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.Observer
import com.paulinavara.bibliotecapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var filteredListIsActive: Boolean = false
    private lateinit var searchView: SearchView
    private lateinit var filteredListGlobal: MutableList<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get books from database
        val booksListView: ListView = findViewById(R.id.listViewBooks)
        var booksList = emptyList<Book>()
        val database = AppDatabase.getDatabase(this)
        database.books().getAll().observe(this, Observer {
            booksList = it
            val adapter = BooksAdapter(this, booksList)
            booksListView.adapter = adapter
        })

        // Filter by search view
        val classContext = this
        searchView = findViewById(R.id.searchView)
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                var filteredList = mutableListOf<Book>()
                val adapter = BooksAdapter(classContext, booksList)
                filteredListIsActive = false
                booksListView.adapter = adapter
                if (newText != null){
                    filteredListIsActive = true
                    for (book in booksList){
                        if(book.title.toLowerCase().contains(newText!!.toLowerCase())){
                            filteredList.add(book)
                        } else if (book.author.toLowerCase().contains(newText!!.toLowerCase())){
                            filteredList.add(book)
                        } else if (book.publisher.toLowerCase().contains(newText!!.toLowerCase())){
                            filteredList.add(book)
                        } else if (book.year.toLowerCase().contains(newText!!.toLowerCase())){
                            filteredList.add(book)
                        }
                    }

                    val adapter = BooksAdapter(classContext, filteredList)
                    booksListView.adapter = adapter
                    filteredListGlobal = filteredList
                }
                return true
            }

        })

        // Go to book details
        booksListView.setOnItemClickListener { parent, view, position, id ->
            val intent = Intent(this, BookActivity::class.java)

            if(filteredListIsActive){
                intent.putExtra("id", filteredListGlobal[position].idBook)
            }else {
                intent.putExtra("id", booksList[position].idBook)
            }

            startActivity(intent)
        }

        // Go to add book
        val addBookIcon: ImageView = findViewById(R.id.addBookIcon)
        addBookIcon.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            startActivity(intent)
        }
    }

}