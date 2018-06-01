package com.example.demad.inventoryapp1.Data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * books database API Contract class
 */
public final class BookContract {
    /**
     * Empty constructor to prevent accident instantiation of the contract class
     */
    private BookContract() {
    }

    /**
     * SetUp the Content authority to use in the {@link BookProvider} content provider
     */
    public static final String CONTENT_AUTHORITY = "com.example.demad.inventoryapp1";
    /**
     * Using CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /**
     * SetUp a path for books to be appended to the base content URI for possible URI's
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that defines constant values for the books database table.
     * Each entry in the table represents a single book.
     */
    public static abstract class BookEntry implements BaseColumns {
        /**
         * The content URI to access the book data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);
        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of books.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        /*
         * The MIME type of the {@link #CONTENT_URI} for a single book.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;
        /**
         * Name of database table for books
         */
        public static final String TABLE_NAME = "books";
        /**
         * Unique ID number for the book (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public static final String _ID = BaseColumns._ID;
        /**
         * Title for the book.
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_TITLE = "title";
        /**
         * Price for the book.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_PRICE = "price";
        /**
         * Book quantity.
         * <p>
         * Type: INTEGER
         */
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        /**
         * Supply name for the book
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLY_NAME = "name";
        /**
         * Supply phone for the book
         * <p>
         * Type: TEXT
         */
        public static final String COLUMN_BOOK_SUPPLY_PHONE = "phone";
    }
}
