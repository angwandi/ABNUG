package com.example.demad.inventoryapp1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.demad.inventoryapp1.Data.BookContract;
import com.example.demad.inventoryapp1.Data.BookDbHelper;

import static com.example.demad.inventoryapp1.Data.BookContract.BookEntry.*;

public class MainActivity extends AppCompatActivity {
    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper bookDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomAppBar bottomAppBar = (BottomAppBar) findViewById(R.id.bottomBar);
        bottomAppBar.replaceMenu(R.menu.main_menu);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        bookDbHelper = new BookDbHelper(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the books database.
     */
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE,
        };
        TextView displayView = findViewById(R.id.textView);
        try (Cursor cursor = db.query(
                BookContract.BookEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null)) {
            // Create a header in the Text View that looks like this:
            //
            // The books table contains <number of rows in Cursor> books.
            // _id - title - price - quantity - supply name - supply phone
            //
            // In the while loop below, iterate through the rows of the cursor and display
            // the information from each column in this order.
            displayView.setText("The books table contains " + cursor.getCount() + " books.\n\n");
            displayView.append(_ID + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_TITLE + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_PRICE + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME + " - " +
                    BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE + "\n ");
            // Figure out the index of each column
            int idColIndex = cursor.getColumnIndex(_ID);
            int bTitleColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
            int bPriceColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int bQuantityColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int bSupplyNameColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME);
            int bSupplyPhoneColIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE);
            // Iterate through all the returned rows in the cursor
            while (cursor.moveToNext()) {
                // Use that index to extract the String or Int value of the word
                // at the current row the cursor is on.
                int currentID = cursor.getInt(idColIndex);
                String currentBookTitle = cursor.getString(bTitleColIndex);
                int currentBookPrice = cursor.getInt(bPriceColIndex);
                int currentBookQuantity = cursor.getInt(bQuantityColIndex);
                String currentBookSupplyName = cursor.getString(bSupplyNameColIndex);
                String currentBookSupplyPhone = cursor.getString(bSupplyPhoneColIndex);
                // Display the values from each column of the current row in the cursor in the TextView
                displayView.append(("\n" + currentID + " - " +
                        currentBookTitle + " - " +
                        currentBookPrice + " - " +
                        currentBookQuantity + " - " +
                        currentBookSupplyName + " - " +
                        currentBookSupplyPhone));
            }
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Gets the database in write mode
        SQLiteDatabase db = bookDbHelper.getReadableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and The book of life's book attributes are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BOOK_TITLE, "The Book life");
        contentValues.put(COLUMN_BOOK_PRICE, "29");
        contentValues.put(COLUMN_BOOK_QUANTITY, "5");
        contentValues.put(COLUMN_BOOK_SUPPLY_NAME, "Google Books");
        contentValues.put(COLUMN_BOOK_SUPPLY_PHONE, "(+44)7880640470");
        // Insert a new row for The book of life in the database, returning the ID of that new row.
        // The first argument for db.insert() is the books table name.
        // The second argument provides the name of a column in which the framework
        // can insert NULL in the event that the ContentValues is empty (if
        // this is set to "null", then the framework will not insert a row when
        // there are no values).
        // The third argument is the ContentValues object containing the info for the book of life.
        long newRowID = db.insert(TABLE_NAME, null, contentValues);
        Log.v("MainActivity", "New Row ID" + newRowID);
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
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
