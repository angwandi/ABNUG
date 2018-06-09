package com.example.demad.inventoryapp1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.demad.inventoryapp1.data.BookContract;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorBookActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;
    /**
     * Content URI for the existing book (null if it's a new book)
     */
    private Uri currentEditorBookUri;
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
    /**
     * Boolean flag that keeps track of whether the book has been edited (true) or not (false)
     */
    private boolean bookHasChanged = false;
    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        /*
        Examine the intent that was used to launch this activity,
        in order to figure out if we're creating a new book or editing an existing one.
        */
        currentEditorBookUri = getIntent().getData();
        /*
        If the intent DOES NOT contain a book content URI, then we know that we are
        creating a new book.
        */
        if (currentEditorBookUri == null) {
            // This is a new book, so change the app bar to say "Add a Book"
            setTitle("Add a Book");
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Book");
            invalidateOptionsMenu();
            // Initialize a loader to read the book data from the database
            // and display the current values in the editor
            getSupportLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        bookTitleEditText = findViewById(R.id.title_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        supplyNameEditText = findViewById(R.id.supply_name_edit_text);
        supplyPhoneEditText = findViewById(R.id.supply_phone_edit_text);
        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        bookTitleEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplyNameEditText.setOnTouchListener(touchListener);
        supplyPhoneEditText.setOnTouchListener(touchListener);
    }

    /**
     * Get user input from editor and save new book into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String bookTitleString = bookTitleEditText.getText().toString().trim();
        String bookPriceString = priceEditText.getText().toString().trim();
        int priceInt = Integer.parseInt(bookPriceString);
        String bookQuantityString = quantityEditText.getText().toString().trim();
        int quantityInt = Integer.parseInt(bookQuantityString);
        String bookSupplyNameString = supplyNameEditText.getText().toString().trim();
        String bookSupplyPhoneString = supplyPhoneEditText.getText().toString().trim();
        // Check if this is supposed to be a new book
        // and check if all the fields in the editor are blank
        if (currentEditorBookUri == null && TextUtils.isEmpty(bookTitleString) &&
                TextUtils.isEmpty(bookPriceString) && TextUtils.isEmpty(bookQuantityString) &&
                TextUtils.isEmpty(bookSupplyNameString) && TextUtils.isEmpty(bookSupplyPhoneString)) {
            // Since no fields were modified, we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        // Create a ContentValues object where column names are the keys,
        // and book attributes from the editor are the values.
        ContentValues contentValues = new ContentValues();
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_TITLE, bookTitleString);
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceInt);
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityInt);
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME, bookSupplyNameString);
        contentValues.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE, bookSupplyPhoneString);
        // Determine if this is a new or existing book by checking if currentEditorBookUri is null or not
        if (currentEditorBookUri == null) {
            // This is a NEW book, so insert a new book into the provider,
            // returning the content URI for the new book.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, contentValues);
            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error with saving book", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Book saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING book, so update the book with content URI: currentEditorBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentEditorBookUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(currentEditorBookUri, contentValues, null, null);
            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating book", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Book updated", Toast.LENGTH_SHORT).show();
            }
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If this is a new book, hide the "Delete" menu item.
        if (currentEditorBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
            return super.onPrepareOptionsMenu(menu);
        }
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                //Save Book to the database
                saveBook();
                //Exit Activity
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the book hasn't changed, continue with navigating up to parent activity
                // which is the {@link MainActivity}.
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorBookActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorBookActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Since the editor shows all book attributes, define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE};
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                currentEditorBookUri,          // Query the content URI for the current book
                projection,                    // Columns to include in the resulting Cursor
                null,                  // No selection clause
                null,               // No selection arguments
                null);                 // Default sort order
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        // Release early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading cursor from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of book attributes that we're interested in
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplyNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME);
            int supplyPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE);
            // Extract out the value from the Cursor for the given column index
            String bookTitle = cursor.getString(titleColumnIndex);
            int bookPrice = cursor.getInt(priceColumnIndex);
            int bookQuantity = cursor.getInt(quantityColumnIndex);
            String supplyName = cursor.getString(supplyNameColumnIndex);
            String supplyPhone = cursor.getString(supplyPhoneColumnIndex);
            // Update the views on the screen with the values from the database
            bookTitleEditText.setText(bookTitle);
            priceEditText.setText(String.valueOf(bookPrice));
            quantityEditText.setText(String.valueOf(bookQuantity));
            supplyNameEditText.setText(supplyName);
            supplyPhoneEditText.setText(supplyPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        bookTitleEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplyNameEditText.setText("");
        supplyPhoneEditText.setText("");
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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
}
