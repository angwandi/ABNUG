package com.example.demad.inventoryapp1;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.example.demad.inventoryapp1.data.BookContract;

import static com.example.demad.inventoryapp1.data.BookContract.*;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_TITLE;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.CONTENT_URI;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    BookDetailsCursorAdapter bookDetailsCursorAdapter;
    private static final int BOOK_LOADER = 1;
    private Uri currentContentURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_details);
        ListView detailsListView = findViewById(R.id.details_list);
        bookDetailsCursorAdapter = new BookDetailsCursorAdapter(this, null);
        detailsListView.setAdapter(bookDetailsCursorAdapter);
        currentContentURI = getIntent().getData();
        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                BookEntry._ID,
                COLUMN_BOOK_TITLE,
                COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                COLUMN_BOOK_SUPPLY_NAME,
                COLUMN_BOOK_SUPPLY_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                currentContentURI, // Provider content URI to query
                projection,                        // Columns to include in the resulting Cursor
                null,                      // No selection clause
                null,                  // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        bookDetailsCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        bookDetailsCursorAdapter.swapCursor(null);
    }
}
