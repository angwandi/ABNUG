package com.example.demad.inventoryapp1;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Allows user to create a new book or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity {
    /**
     * Edit field to enter the book title
     */
    private EditText productNameEditText;
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
        productNameEditText = findViewById(R.id.title_edit_text);
        priceEditText = findViewById(R.id.price_edit_text);
        quantityEditText = findViewById(R.id.quantity_edit_text);
        supplyNameEditText = findViewById(R.id.supply_name_edit_text);
        supplyPhoneEditText = findViewById(R.id.supply_phone_edit_text);
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
                //Do nothing for now
                return true;
            case R.id.action_delete:
                //Do nothing for now
                return true;
            case android.R.id.home:
                //Navigate back to parent activity(MAinActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
