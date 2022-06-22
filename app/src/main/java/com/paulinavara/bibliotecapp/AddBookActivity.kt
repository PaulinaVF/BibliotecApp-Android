package com.paulinavara.bibliotecapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream

class AddBookActivity : AppCompatActivity() {

    private lateinit var loadImageButton: Button
    private lateinit var coverImageView: ImageView
    private lateinit var saveBookButton: Button
    private lateinit var titleEditText: EditText
    private lateinit var authorEditText: EditText
    private lateinit var publisherEditText: EditText
    private lateinit var yearEditText: EditText
    private lateinit var categoryEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var database: AppDatabase
    private lateinit var bookLiveData: LiveData<Book>
    private lateinit var book: Book

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        loadImageButton = findViewById(R.id.add_buttonLoadCover)
        coverImageView = findViewById(R.id.add_coverImageView)
        saveBookButton = findViewById(R.id.add_buttonAddBook)
        titleEditText = findViewById(R.id.add_editTextTitle)
        authorEditText = findViewById(R.id.add_editTextAuthor)
        publisherEditText = findViewById(R.id.add_editTextPublisher)
        yearEditText = findViewById(R.id.add_editTextYear)
        categoryEditText = findViewById(R.id.add_editTextCategory)
        priceEditText = findViewById(R.id.add_editTextPrice)

        // To use this activity to update book info too
        database = AppDatabase.getDatabase(this)
        var idBook: Int? = null

        if(intent.hasExtra("idBook")){
            saveBookButton.text = "Actualizar libro"
            idBook = intent.extras?.getInt("idBook",0)
            bookLiveData = database.books().get(idBook!!)

            bookLiveData.observe(this, Observer {
                book = it

                titleEditText.setText(book.title)
                authorEditText.setText(book.author)
                publisherEditText.setText(book.publisher)
                yearEditText.setText(book.year)
                categoryEditText.setText(book.category)
                priceEditText.setText(book.price.toString())

                val image: ByteArray = book.cover
                val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
                coverImageView.setImageBitmap(bmp)
            })
        }

        // Upload picture
        loadImageButton.setOnClickListener{
            pickImageFromGallery()
        }

        // Add profile to database
        val database = AppDatabase.getDatabase(this)

        saveBookButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val author = authorEditText.text.toString()
            val publisher = publisherEditText.text.toString()
            val year = yearEditText.text.toString()
            val category = categoryEditText.text.toString()
            val price = priceEditText.text.toString()

            // Get image to save
            val photo = (coverImageView.drawable as BitmapDrawable).bitmap
            val bos = ByteArrayOutputStream()
            photo.compress(Bitmap.CompressFormat.PNG, 100, bos)
            val imageByteArray: ByteArray = bos.toByteArray()

            // Create book
            val book = Book(title, author, publisher, year, imageByteArray, price.toDouble(), category)

            // Check if a new book will be added or it's an update
            if (idBook != null){
                CoroutineScope(Dispatchers.IO).launch {
                    book.idBook = idBook
                    database.books().update(book)

                    this@AddBookActivity.finish()
                }
            } else {
                CoroutineScope(Dispatchers.IO).launch {
                    database.books().insertAll(book)
                    this@AddBookActivity.finish()
                }
            }
        }

        // Dismiss current Activity
        val dismissActivity: ImageView = findViewById(R.id.backItem)
        dismissActivity.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun pickImageFromGallery(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launcher.launch(intent)
    }

    private val launcher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == RESULT_OK
            && result.data != null
        ) {
            val photoUri: Uri? = result.data!!.data
            coverImageView.setImageURI(photoUri)
        }
    }
}