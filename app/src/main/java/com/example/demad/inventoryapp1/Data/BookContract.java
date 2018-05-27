package com.example.demad.inventoryapp1.Data;

import android.provider.BaseColumns;

/**
 * books database Contract class
 */
public final class BookContract {
    public static abstract class BookEntry implements BaseColumns {
        public static final String TABLE_NAME = "books";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_BOOK_QUANTITY = "quantity";
        public static final String COLUMN_BOOK_SUPPLY_NAME = "name";
        public static final String COLUMN_BOOK_SUPPLY_PHONE = "phone";
    }
}
