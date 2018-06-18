package com.example.demad.inventoryapp1;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.demad.inventoryapp1.data.BookContract;

import static com.example.demad.inventoryapp1.data.BookContract.BookEntry.*;

public class BookDetailsCursorAdapter extends CursorAdapter {
    BookDetailsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.activity_book_detail, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the book details layout
        TextView titleTextView = view.findViewById(R.id.title_input_details_textView);
        TextView priceTextView = view.findViewById(R.id.price_input_details_textView);
        TextView quantityTextView = view.findViewById(R.id.quantity_input_detail_textView);
        TextView supplyNameTextView = view.findViewById(R.id.supply_name_input_details_textView);
        TextView supplyPhoneTextView = view.findViewById(R.id.supply_phone_input_details_textView);
        Button increaseQuantityButton = view.findViewById(R.id.add);
        Button decreaseQuantityButton = view.findViewById(R.id.minus);
        Button phoneSupplyButton = view.findViewById(R.id.call);
        // Find the columns of books attributes that we're interested in
        final int idColumnIndex = cursor.getColumnIndex(BookContract.BookEntry._ID);
        int titleColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_QUANTITY);
        int supplyNameColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_SUPPLY_NAME);
        final int supplyPhoneColumnIndex = cursor.getColumnIndex(COLUMN_BOOK_SUPPLY_PHONE);
        // Read the book attributes from the Cursor for the current book
        final String bookID = cursor.getString(idColumnIndex);
        String title = cursor.getString(titleColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        String name = cursor.getString(supplyNameColumnIndex);
        final String phone = cursor.getString(supplyPhoneColumnIndex);
        /*Increase quantity by unit of 1*/
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsActivity detailsActivity = (DetailsActivity) context;
                detailsActivity.increaseQuantity(Integer.valueOf(bookID), quantity);
            }
        });
        /*Decrease quantity by unit of 1*/
        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetailsActivity detailsActivity = (DetailsActivity) context;
                detailsActivity.decreaseQuantity(Integer.valueOf(bookID), quantity);
            }
        });
        /*Phone Supply using default phone app*/
        phoneSupplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            }
        });
        // Update the TextViews with the attributes for the current book
        titleTextView.setText(title);
        priceTextView.setText(String.format("£%s", price));
        quantityTextView.setText(String.valueOf(quantity));
        supplyNameTextView.setText(name);
        supplyPhoneTextView.setText(phone);
    }
}
