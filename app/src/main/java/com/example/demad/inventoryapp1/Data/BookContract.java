package com.example.demad.inventoryapp1.Data;

import android.provider.BaseColumns;

public final class BookContract {
    public static abstract class BookEntry implements BaseColumns{
        public static String TABLE_NAME = "books";
        public static String _ID = BaseColumns._ID;
        public static final String C_BOOK_TITLE= "title";
        public static final String C_BOOK_PRICE= "price";
        public static final String C_BOOK_QUANTITY= "quantity";
        public static final String C_BOOK_SUPPLY_NAME= "supply_name";
        public static final String C_BOOK_SUPPLY_PHONE= "supply_phone";
    }
}
