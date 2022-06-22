package com.paulinavara.bibliotecapp

import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BookActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var coverImageView: ImageView
    private lateinit var authorTextView: TextView
    private lateinit var publisherTextView: TextView
    private lateinit var yearTextView: TextView
    private lateinit var categoryTextView: TextView
    private lateinit var priceTextView: TextView
    private lateinit var book: Book
    private lateinit var database: AppDatabase
    private lateinit var bookLiveData: LiveData<Book>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        titleTextView = findViewById(R.id.ab_textViewTitle)
        authorTextView = findViewById(R.id.ab_textViewAuthor)
        publisherTextView = findViewById(R.id.ab_textViewPublisher)
        yearTextView = findViewById(R.id.ab_textViewYear)
        coverImageView = findViewById(R.id.ab_imageViewCover)
        categoryTextView = findViewById(R.id.ab_textViewCategory)
        priceTextView = findViewById(R.id.ab_textViewPrice)

        database = AppDatabase.getDatabase(this)
        val idBook = intent.getIntExtra("id",0)
        bookLiveData = database.books().get(idBook)

        bookLiveData.observe(this, Observer {
            book = it

            titleTextView.text = book.title
            authorTextView.text = book.author
            publisherTextView.text = book.publisher
            yearTextView.text = book.year
            categoryTextView.text = book.category
            priceTextView.text = "$" + book.price.toString()

            val image: ByteArray = book.cover
            val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)

            coverImageView.setImageBitmap(bmp)
        })

        // Add edit and delete options functionality
        val editBook: ImageView = findViewById(R.id.editItem)
        editBook.setOnClickListener {
            val intent = Intent(this, AddBookActivity::class.java)
            intent.putExtra("idBook", book.idBook)
            startActivity(intent)
        }

        val deleteBook: ImageView = findViewById(R.id.deleteItem)
        deleteBook.setOnClickListener {
            val bookLiveDataOwner = this
            AlertDialog.Builder(this).apply {
                setTitle("Eliminar libro")
                setMessage("¿Estás seguro de que deseas eliminar el libro "+book.title+"? Esta acción es irreversible.")
                setPositiveButton("Sí") { _:DialogInterface, _: Int ->
                    bookLiveData.removeObservers(bookLiveDataOwner)

                    CoroutineScope(Dispatchers.IO).launch {
                        database.books().delete(book)
                    }

                    this@BookActivity.finish()
                }
                setNegativeButton("No", null)
            }.show()
        }

        // Dismiss current Activity
        val dismissActivity: ImageView = findViewById(R.id.backItem)
        dismissActivity.setOnClickListener {
            super.onBackPressed()
        }

    }
}