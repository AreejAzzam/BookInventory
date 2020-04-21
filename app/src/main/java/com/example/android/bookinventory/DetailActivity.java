package com.example.android.bookinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private TextView bookNameTextView;
    private TextView bookPriceTextView;
    private TextView bookQuantityTextView;
    private TextView SupplerPhoneTextView;
    private TextView SupplerNameTextView;
    private ImageButton callSupplerImageButton;
    private boolean mBookHasChanged = false;
    private Uri mCurrentBookUri;
    private String bookName;
    private int bookPrice;
    private Button decreaseQuantityButton;
    private Button increaseQuantityButton;

    private int bookQuantity;

    private String bookSupplerName;

    private String bookSupplerPhone;
    private static final int VIEW_PRODUCT_LOADER = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        final Intent intent = getIntent();
        mCurrentBookUri = intent.getData();
        mCurrentBookUri = getIntent().getData();

        getLoaderManager().initLoader(VIEW_PRODUCT_LOADER, null, this);
        bookNameTextView = findViewById(R.id.DelName);
        bookPriceTextView = findViewById(R.id.DelPrice);
        bookQuantityTextView = findViewById(R.id.DelQuantity);
        decreaseQuantityButton = findViewById(R.id.reduceQuantity);
        increaseQuantityButton = findViewById(R.id.increaseQuantity);
        SupplerNameTextView = findViewById(R.id.delSupplerName);
        SupplerPhoneTextView = findViewById(R.id.delphone);
        callSupplerImageButton = findViewById(R.id.callImage);

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity > 0) {
                    int newQuantity = bookQuantity - 1;
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_book_Quantity, newQuantity);
                    int rowsHasChanged = getContentResolver().update(mCurrentBookUri, values, null, null);
                    if (rowsHasChanged == 0) {
                        Toast.makeText(DetailActivity.this, R.string.quantity_error, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetailActivity.this, R.string.quantity_saved, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookQuantity >= 0) {
                    int newQuantity = bookQuantity + 1;
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_book_Quantity, newQuantity);
                    int rowsHasChanged = getContentResolver().update(mCurrentBookUri, values, null, null);
                    if (rowsHasChanged == 0) {
                        Toast.makeText(DetailActivity.this, R.string.quantity_error, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(DetailActivity.this, R.string.quantity_saved, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        callSupplerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + bookSupplerPhone));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_book_NAME,
                BookContract.BookEntry.COLUMN_book_price,
                BookContract.BookEntry.COLUMN_book_Quantity,
                BookContract.BookEntry.COLUMN_book_suppler_name,
                BookContract.BookEntry.COLUMN_book_suppler_phone};

        return new CursorLoader(this,   // Parent activity context
                mCurrentBookUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToFirst()) {
            bookName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_book_NAME));
            bookPrice = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_book_price));
            bookQuantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_book_Quantity));
            bookSupplerName = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_book_suppler_name));
            bookSupplerPhone = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_book_suppler_phone));

            bookNameTextView.setText(bookName);
            bookQuantityTextView.setText(String.valueOf(bookQuantity));
            bookPriceTextView.setText(String.valueOf(bookPrice));

            if (TextUtils.isEmpty(bookSupplerName)) {
                bookSupplerName = getString(R.string.no_suppler);
            }
            SupplerNameTextView.setText(bookSupplerName);
            if (TextUtils.isEmpty(bookSupplerPhone)) {
                bookSupplerPhone = getString(R.string.no_phone);
            }
            SupplerPhoneTextView.setText(bookSupplerPhone);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        return true;
    }

    public void onBackPressed() {
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookNameTextView.setText("");
        bookPriceTextView.setText("");
        bookQuantityTextView.setText("");
        SupplerNameTextView.setText("");
        SupplerPhoneTextView.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_details_edit: {
                Intent intent = new Intent(DetailActivity.this, BooksActivity.class);
                intent.setData(mCurrentBookUri);
                startActivity(intent);
                return true;
            }

            case R.id.action_details_delete:
                showDeleteConfirmationDialog();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void deleteBook() {
        if (mCurrentBookUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_Book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_Book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}
