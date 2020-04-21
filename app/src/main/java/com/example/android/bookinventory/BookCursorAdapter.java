package com.example.android.bookinventory;

import android.content.ContentUris;
import android.content.ContentValues;
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
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract;


public class BookCursorAdapter extends CursorAdapter {
    private Context currentContext;

    public BookCursorAdapter(Context context, Cursor c) {
        super(context,
                c, 0);
        currentContext = context;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,
                parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idTextView = view.findViewById(R.id.id);
        TextView nameTextView = view.findViewById(R.id.name);
        TextView quantityTextView = view.findViewById(R.id.Quantity);
        TextView priceTextView = view.findViewById(R.id.Price);

        int idColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID);
        int nameColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_book_NAME);
        int quantityColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_book_Quantity);
        int priceColumnIndex = cursor.getColumnIndexOrThrow(BookContract.BookEntry.COLUMN_book_price);

        int bookID = cursor.getInt(idColumnIndex);

        String bookTitle = cursor.getString(nameColumnIndex);
        String bookQuantity = cursor.getString(quantityColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);

        idTextView.setText(String.valueOf(bookID));
        nameTextView.setText(bookTitle);
        quantityTextView.setText(bookQuantity);
        priceTextView.setText(bookPrice);

    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        final View view = super.getView(position, convertView, parent);
        final TextView currentIdTextView = view.findViewById(R.id.id);
        final TextView currentBookQuantity = view.findViewById(R.id.Quantity);

        Button saveBtn = view.findViewById(R.id.saleButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strProductId = currentIdTextView.getText().toString();
                long _id = Long.parseLong(strProductId);
                Uri currentItemUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, _id);
                String strCurrentItemQuantity = currentBookQuantity.getText().toString();
                int currentItemQuantity = Integer.parseInt(strCurrentItemQuantity);
                if (currentItemQuantity > 0)
                    currentItemQuantity--;
                else {
                    Toast.makeText(currentContext, R.string.item_sold, Toast.LENGTH_SHORT).show();
                    return;

                }
                ContentValues newQuantityValue = new ContentValues();
                newQuantityValue.put(BookContract.BookEntry.COLUMN_book_Quantity, currentItemQuantity);
                int rowUpdated = currentContext.getContentResolver().update(currentItemUri, newQuantityValue, null, null);
                if (rowUpdated != 0) {
                    currentBookQuantity.setText(String.valueOf(currentItemQuantity));
                    Toast.makeText(currentContext, R.string.item_sold, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(currentContext, R.string.item_not_sold, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button EditBtn = view.findViewById(R.id.EditButton);
        EditBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String strProductId = currentIdTextView.getText().toString();
                long _id = Long.parseLong(strProductId);

                Intent intent = new Intent(view.getContext(), EditorActivity.class);
                Uri currentItemUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, _id);

                intent.setData(currentItemUri);

                view.getContext().startActivity(intent);
            }
        });

        return view;
    }

}
