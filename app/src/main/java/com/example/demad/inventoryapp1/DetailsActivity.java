package com.example.demad.inventoryapp1;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.demad.inventoryapp1.data.BookContract;

import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.*;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_PRICE;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    BookDetailsCursorAdapter bookDetailsCursorAdapter;
    private static final int BOOK_LOADER = 1;
    /*Log message*/
    /**
     * Setup a new content URI
     */
    private Uri currentContentURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_details);
        FloatingActionButton edit = findViewById(R.id.fab_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailsActivity.this, EditorBookActivity.class);
                currentContentURI = getIntent().getData();
                intent.setData(currentContentURI);
                startActivity(intent);
            }
        });
        FloatingActionButton delete = findViewById(R.id.fab_delete_forever);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });
        ListView detailsListView = findViewById(R.id.details_list);
        bookDetailsCursorAdapter = new BookDetailsCursorAdapter(this, null);
        detailsListView.setAdapter(bookDetailsCursorAdapter);
        /*Get Intent and data for the new URI*/
        currentContentURI = getIntent().getData();
        getSupportLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                _ID,
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                COLUMN_BOOK_PRICE,
                COLUMN_BOOK_QUANTITY,
                COLUMN_BOOK_SUPPLY_NAME,
                COLUMN_BOOK_SUPPLY_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,  // Parent activity context
                currentContentURI, // Provider content URI to query and load a single book details screen
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

    /*
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete book?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Delete" button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
     * Perform the deletion of the book in the database.
     */
    public final void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentContentURI != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the currentEditorBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(currentContentURI, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting book", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "Book deleted", Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }

    /*Helper method for increase(Add) button*/
    public void increaseQuantity(int bookID, int bookQuantity) {
        bookQuantity = bookQuantity + 1;
        if (bookQuantity >= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            Uri currentUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookID);
            int rowAffected = getContentResolver().update(currentUri, contentValues, null, null);
            Toast.makeText(this, "Quantity Added", Toast.LENGTH_SHORT).show();
            Log.e("Log message", "rowsAffected" + rowAffected + bookID + bookQuantity);
        }
    }

    /*Helper method for decrease(minus) button*/
    public void decreaseQuantity(int bookID, int bookQuantity) {
        bookQuantity = bookQuantity - 1;
        if (bookQuantity >= 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, bookQuantity);
            Uri currentUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookID);
            int rowAffected = getContentResolver().update(currentUri, contentValues, null, null);
            Toast.makeText(this, "Quantity Reduced", Toast.LENGTH_SHORT).show();
            Log.e("Log message", "rowsAffected" + rowAffected + bookID + bookQuantity);
        } else {
            Toast.makeText(this, "Restricted Action", Toast.LENGTH_SHORT).show();
        }
    }
}
