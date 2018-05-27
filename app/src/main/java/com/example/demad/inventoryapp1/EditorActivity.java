package com.example.demad.inventoryapp1;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demad.inventoryapp1.Data.BookDbHelper;

import static com.example.demad.inventoryapp1.Data.BookContract.BookEntry.*;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    /**
     * Edit field to enter the book title
     */
    private EditText bookTitleEditText;
    /**
     * EditText field to enter the book price
     */
    private EditText priceEditText;
    /**
     * EditText field to enter the book quantity
     */
    private EditText quantityEditText;
    /**
     * EditText field to enter the book supply name
     */
    private EditText supplyNameEditText;
    /**
     * EditText field to enter the book supply phone
     */
    private EditText supplyPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor_activity);
        // Find all relevant views that we will need to read user input from
        bookTitleEditText = findViewById(R.id.title_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        supplyNameEditText = findViewById(R.id.supply_name_edit_text);
        supplyPhoneEditText = findViewById(R.id.supply_phone_edit_text);
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void insertBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookTitleString = bookTitleEditText.getText().toString().trim();
        String bookPriceString = priceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(bookPriceString);
        String bookQuantityString = quantityEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(bookQuantityString);
        String bookSupplyNameString = supplyNameEditText.getText().toString().trim();
        String bookSupplyPhoneString = supplyPhoneEditText.getText().toString().trim();
        // Create database helper
        BookDbHelper bookDbHelper = new BookDbHelper(this);
        // Gets the database in write mode
        SQLiteDatabase db = bookDbHelper.getWritableDatabase();
        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_BOOK_TITLE, bookTitleString);
        contentValues.put(COLUMN_BOOK_PRICE, priceInt);
        contentValues.put(COLUMN_BOOK_QUANTITY, quantityInt);
        contentValues.put(COLUMN_BOOK_SUPPLY_NAME, bookSupplyNameString);
        contentValues.put(COLUMN_BOOK_SUPPLY_PHONE, bookSupplyPhoneString);
        // Insert a new row for book in the database, returning the ID of that new row.
        long newRowId = db.insert(TABLE_NAME, null, contentValues);
        // Show a toast message depending on whether or not the insertion was successful
        if (newRowId == -1) {
            // If the row ID is -1, then there was an error with insertion.
            Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast with the row ID.
            Toast.makeText(this, "Book saved with row id: " + newRowId, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * This adds menu items to the app bar.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/editor_menu.xml file.
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Save Book to the database
                insertBook();
                //Exit Activity
                finish();
                return true;
            case R.id.action_delete:
                //Do nothing for now
                return true;
            case android.R.id.home:
                //Navigate back to parent activity(MainActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
