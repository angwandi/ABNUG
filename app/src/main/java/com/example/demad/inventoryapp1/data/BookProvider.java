package com.example.demad.inventoryapp1.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.Objects;

import static com.example.demad.inventoryapp1.data.BookContract.*;
import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.*;
import static java.util.Objects.*;

/**
 * {@link ContentProvider} for Books app.
 */
public class BookProvider extends ContentProvider {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 700;
    /**
     * URI matcher code for the content URI for a single book in the books table
     */
    private static final int BOOK_ID = 701;
    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH for UriMatcher case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    /*
        Static initializer to be called the first time anything is called from this class
    */
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        // The content URI of the form "content://com.example.demad.inventoryapp1/books" will map to the
        // integer code {@link #BOOKS}. This URI is used to provide access to MULTIPLE rows
        // of the books table.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        // The content URI of the form "content://com.example.demad.inventoryapp1/books/#" will map to the
        // integer code {@link #BOOK_ID}. This URI is used to provide access to ONE single row
        // of the books table.
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.demad.inventoryapp1/books/3" matches, but
        // "content://com.example.demad.inventoryapp1/books" (without a number at the end) doesn't match.
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Database helper object
     */
    private BookDbHelper mDBHelper;

    @Override
    public boolean onCreate() {
        mDBHelper = new BookDbHelper(getContext());
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDBHelper.getReadableDatabase();
        //This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                /*
                For the BOOKS code, query the books table directly with the given
                projection, selection, selection arguments, and sort order. The cursor
                could contain multiple rows of the books table.
                */
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the PET_ID code, extract out the ID from the URI.
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                /*
                This will perform a query on the books table with the corresponding _ID
                Cursor containing that row of the table.
                */
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        /*
        Set notification URI on the Cursor,
        so we know what content URI the Cursor was created for.
        If the data at this URI changes, then we know we need to update the Cursor.
        */
        cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return CONTENT_LIST_TYPE;
            case BOOK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + "with match " + match);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                assert contentValues != null;
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Uri insertBook(Uri uri, ContentValues contentValues) {
        // Check that the title is not null
        String title = contentValues.getAsString(COLUMN_BOOK_TITLE);
        if (title == null) {
            throw new IllegalArgumentException("Title is required!");
        }
        // Check that the price is valid and not null
        Integer price = contentValues.getAsInteger(COLUMN_BOOK_PRICE);
        if (price == null) {
            throw new IllegalArgumentException("Price is required!");
        }
        // if the quantity is provided, check that it's greater than or equal to 0
        Integer quantity = contentValues.getAsInteger(COLUMN_BOOK_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Quantity must be 0 or greater than 0!");
        }
        /*
        No need to check Supply name, any values is valid (including null).
        Check that the supply phone is not null
        */
        String phone = contentValues.getAsString(COLUMN_BOOK_SUPPLY_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Phone number is required!");
        }
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Insert the new book with the given values
        long id = database.insert(TABLE_NAME, null, contentValues);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Notify all listeners that the data has changed for the book content URI
        requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    // Notify all listeners that the data has changed for the book content URI
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectArgs) {
        // Get writeable database
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(TABLE_NAME, selection, selectArgs);
                break;
            case BOOK_ID:
                // Delete a single row given by the ID in the URI
                selection = _ID + "=?";
                selectArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(TABLE_NAME, selection, selectArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        /*
        If 1 or more rows were deleted, then notify all listeners that the data at the
        given URI has changed
        */
        if (rowsDeleted != 0) {
            requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String
            selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                assert contentValues != null;
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                /*
                For the BOOK_ID code, extract out the ID from the URI,
                so we know which row to update. Selection will be "_id=?" and selection
                arguments will be a String array containing the actual ID.
                */
                selection = _ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                assert contentValues != null;
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    /*
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more book).
     * Return the number of rows that were successfully updated.
     */

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int updateBook(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        /*
        If the {@link BookEntry#COLUMN_BOOK_TITLE} key is present,
        check that the title value is not null.
        */
        if (contentValues.containsKey(COLUMN_BOOK_TITLE)) {
            String title = contentValues.getAsString(COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Title is required!");
            }
        }
        /*
        If the {@link BookEntry#COLUMN_BOOK_PRICE} key is present,
        check that the title value is not null.
        */
        if (contentValues.containsKey(COLUMN_BOOK_PRICE)) {
            Integer price = contentValues.getAsInteger(COLUMN_BOOK_PRICE);
            if (price == null) {
                throw new IllegalArgumentException("Price is required!");
            }
        }
        /*
        If the {@link BookEntry#COLUMN_BOOK_QUANTITY} key is present,
        check that the title value is not null and not less than 0.
        */
        if (contentValues.containsKey(COLUMN_BOOK_QUANTITY)) {
            Integer quantity = contentValues.getAsInteger(COLUMN_BOOK_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Quantity must 0 or greater than 0");
            }
        }
        /*
        No need to check supply name, any values is valid(including null)
        If the {@link BookEntry#COLUMN_BOOK_SUPPLY_PHONE} key is present,
        check that the phone value is not null.
        */
        if (contentValues.containsKey(COLUMN_BOOK_SUPPLY_PHONE)) {
            String phone = contentValues.getAsString(COLUMN_BOOK_SUPPLY_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Phone number is required!");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }
        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDBHelper.getWritableDatabase();
        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TABLE_NAME, contentValues, selection, selectionArgs);
        /*
        If 1 or more rows were updated, then notify all listeners that the data at the
        given URI has changed
        */
        if (rowsUpdated != 0) {
            requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows updated
        return rowsUpdated;
    }
}
