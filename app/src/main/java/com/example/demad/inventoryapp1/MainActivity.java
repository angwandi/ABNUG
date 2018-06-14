package com.example.demad.inventoryapp1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.demad.inventoryapp1.data.BookContract;

import static com.example.demad.inventoryapp1.data.BookContract.*;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE;
public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;
    /**
     * Adapter for the ListView
     */
    BookCursorAdapter bookCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup FAB to open EditorBookActivity
        FloatingActionButton fab = findViewById(R.id.fab_main);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorBookActivity.class);
                startActivity(intent);
            }
        });
        // Find the ListView which will be populated with the book data
        ListView bookListView = findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_View);
        bookListView.setEmptyView(emptyView);
        // Setup an Adapter to create a list item for each row of book data in the Cursor.
        bookCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(bookCursorAdapter);
       /* Button shopButton = bookListView.findViewById(R.id.shop_list_item_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "minus quantity", Toast.LENGTH_SHORT).show();
            }
        });*/
        // Setup the item click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link DetailsActivity}
                Intent detailsIntent = new Intent(MainActivity.this, DetailsActivity.class);
                // Form the content URI that represents the specific book that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link BookEntry#CONTENT_URI}.
                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent
                detailsIntent.setData(currentBookUri);
                // Launch the {@link DetailsActivity} to display the data for the current book.
                startActivity(detailsIntent);
            }
        });
        // Kick off the loader
        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    public void shopButton(int bookId, int bookQuantity) {
        bookQuantity = bookQuantity - 1;
        if (bookQuantity >= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            Uri currentUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
            int rowsAffected = getContentResolver().update(currentUri, contentValues, null, null);
            Toast.makeText(this, "Added to bucket", Toast.LENGTH_SHORT).show();
            Log.e("Log message", "rowsAffected " + rowsAffected + bookId + bookQuantity);
        } else {
            Toast.makeText(this, "Book out of stock!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        // and The book of life's book attributes are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookEntry.COLUMN_BOOK_TITLE, "The Book life");
        contentValues.put(BookEntry.COLUMN_BOOK_PRICE, "29");
        contentValues.put(BookEntry.COLUMN_BOOK_QUANTITY, "5");
        contentValues.put(COLUMN_BOOK_SUPPLY_NAME, "Google Books");
        contentValues.put(COLUMN_BOOK_SUPPLY_PHONE, "(+44)7880640470");
        // Receive the new content URI that will allow us to access The book life's data in the future.
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, contentValues);
    }

    /**
     * Helper method to delete all pets in the database.
     */
    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDeleted + "rows deleted from book database");
    }

    /**
     * This adds menu items to the app bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/main_menu.xml file.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Add action click on the main menu items
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                //Add dummy data
                insertBook();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete All confirmation Dialog
     */
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete All Books forever?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllBooks();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table but we only care about the first 3.
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_TITLE,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_SUPPLY_NAME,
                BookEntry.COLUMN_BOOK_SUPPLY_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                BookEntry.CONTENT_URI, // Provider content URI to query
                projection,                        // Columns to include in the resulting Cursor
                null,                      // No selection clause
                null,                  // No selection arguments
                null);                   // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCursorAdapter} with this new cursor containing updated book data
        bookCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        bookCursorAdapter.swapCursor(null);
    }
}
