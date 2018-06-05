import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.demad.inventoryapp1.R;
import com.example.demad.inventoryapp1.data.BookContract;

public class BookDetailsCursorAdapter extends CursorAdapter {
    public BookDetailsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.activity_book_detail, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the book details layout
        TextView titleTextView = view.findViewById(R.id.title_input_details_textView);
        TextView priceTextView = view.findViewById(R.id.price_input_details_textView);
        TextView quantityTextView = view.findViewById(R.id.quantity_input_detail_textView);
        TextView supplyNameTextView = view.findViewById(R.id.supply_name_input_details_textView);
        TextView supplyPhoneTextView = view.findViewById(R.id.supply_phone_input_details_textView);
        // Find the columns of books attributes that we're interested in
        int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        int supplyNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_NAME);
        int supplyPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLY_PHONE);
        // Read the book attributes from the Cursor for the current book
        String title = cursor.getString(titleColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);
        String name = cursor.getString(supplyNameColumnIndex);
        String phone = cursor.getString(supplyPhoneColumnIndex);
        // If the the book name is empty string or null, then use some default text
        // that says "None", so the TextView isn't blank.
        if (TextUtils.isEmpty(name)) {
            name = context.getString(R.string.empty_supplyName_details_textUtils);
        }
        // Update the TextViews with the attributes for the current book
        titleTextView.setText(title);
        priceTextView.setText(String.format("£%s", price));
        quantityTextView.setText(quantity);
        supplyNameTextView.setText(name);
        supplyPhoneTextView.setText(phone);
    }
}
