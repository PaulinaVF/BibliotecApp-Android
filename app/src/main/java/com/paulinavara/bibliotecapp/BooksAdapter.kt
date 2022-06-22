package com.paulinavara.bibliotecapp

import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class BooksAdapter(private val baContext:Context, private val booksList: List<Book>): ArrayAdapter<Book>(baContext, R.layout.item_book, booksList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layout = LayoutInflater.from(baContext).inflate(R.layout.item_book, parent, false)
        val book = booksList[position]
        val titleTextView: TextView = layout.findViewById(R.id.textViewTitle)
        val authorTextView: TextView = layout.findViewById(R.id.textViewAuthor)
        val publisherTextView: TextView = layout.findViewById(R.id.textViewPublisher)
        val yearTextView: TextView = layout.findViewById(R.id.textViewYear)
        val coverImageView: ImageView = layout.findViewById(R.id.imageViewCover)

        titleTextView.text = book.title
        authorTextView.text = book.author
        publisherTextView.text = book.publisher
        yearTextView.text = book.year

        val image: ByteArray = book.cover
        val bmp = BitmapFactory.decodeByteArray(image, 0, image.size)
        coverImageView.setImageBitmap(bmp)

        return layout
    }
}