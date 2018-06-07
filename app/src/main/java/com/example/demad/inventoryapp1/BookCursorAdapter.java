package com.example.demad.inventoryapp1;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demad.inventoryapp1.data.BookContract;

/**
 * {@link BookCursorAdapter} is an adapter for a list
 * that uses a {@link Cursor} of book data as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {
    /**
     * Constructs a new {@link BookCursorAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    BookCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    /**
     * Make a new list item view
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  to which the new view is attached to
     * @return the newly created list item
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView titleTextView = view.findViewById(R.id.title_list_item_text_view);
        TextView priceTextView = view.findViewById(R.id.price_list_item_text_view);
        final TextView quantityTextView = view.findViewById(R.id.quantity_list_item_text_view);
        Button shopButton = view.findViewById(R.id.shop_list_item_button);
        // Find the columns of book attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        String bookTitle = cursor.getString(titleColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantities = quantityColumnIndex - 1;
                if (quantities > 1) {
                    quantityTextView.setText(String.valueOf(quantities));

                } else
                    Toast.makeText(context, "Added to bucket!", Toast.LENGTH_SHORT).show();
            }
        });
        // Update the TextViews with the attributes for the current book
        titleTextView.setText(bookTitle);
        priceTextView.setText(String.format("£%s", bookPrice));
        quantityTextView.setText(bookQuantity);
    }
}
