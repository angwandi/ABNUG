package com.example.demad.inventoryapp1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.*;
/**
 * Displays list of books that were entered and stored in the app.
 */
public class BookDbHelper extends SQLiteOpenHelper {
    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "book.db";
    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_BOOK_TITLE + " TEXT NOT NULL,"
                + COLUMN_BOOK_PRICE + " INTEGER NOT NULL,"
                + COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0,"
                + COLUMN_BOOK_SUPPLY_NAME + " TEXT,"
                + COLUMN_BOOK_SUPPLY_PHONE + " TEXT NOT NULL);";
        //Execute the SQL statement
        db.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME);
    }
}
