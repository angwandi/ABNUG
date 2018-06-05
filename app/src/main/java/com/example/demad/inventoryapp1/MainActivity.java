package com.example.demad.inventoryapp1;

import android.content.ContentValues;
import android.content.Intent;
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

import com.example.demad.inventoryapp1.data.BookDbHelper;

import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.*;

public class MainActivity extends AppCompatActivity {
    /**
     * Database helper that will provide us access to the database
     */
    private BookDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Setup BAB */
        BottomAppBar bottomAppBar = findViewById(R.id.bottomBar);
        bottomAppBar.replaceMenu(R.menu.main_menu);
        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertBook() {
        // Gets the database in write mode
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
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
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                //Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
