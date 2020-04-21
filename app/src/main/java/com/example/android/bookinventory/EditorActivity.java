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
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract.BookEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int EXISTING_Book_LOADER = 0;
    private Uri mCurrentBookUri;

    private EditText bookName;
    private EditText bookPrice;
    private EditText bookQuantity;
    private EditText SupplerPhone;
    private EditText SupplerName;
    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_Book));

            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_Book));

            getLoaderManager().initLoader(EXISTING_Book_LOADER, null, this);
        }


        bookName = findViewById(R.id.edit_books_name);
        bookPrice = findViewById(R.id.edit_books_price);
        bookQuantity = findViewById(R.id.edit_books_quantity);
        SupplerName = findViewById(R.id.edit_books_suppler_name);
        SupplerPhone = findViewById(R.id.edit_suppler_phone);
        bookName.setOnTouchListener(mTouchListener);
        bookPrice.setOnTouchListener(mTouchListener);

        bookQuantity.setOnTouchListener(mTouchListener);

        SupplerName.setOnTouchListener(mTouchListener);

        SupplerPhone.setOnTouchListener(mTouchListener);


    }

    private void saveBook() {
        String nameString = bookName.getText().toString().trim();
        String QuantityString = bookQuantity.getText().toString().trim();
        String priceString = bookPrice.getText().toString().trim();
        String supplerNameString = SupplerName.getText().toString().trim();
        String supplerPhoneString = SupplerPhone.getText().toString().trim();

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(QuantityString) &&
                TextUtils.isEmpty(priceString) && TextUtils.isEmpty(supplerNameString) && TextUtils.isEmpty(supplerPhoneString)) {
            return;
        }

        if (nameString.isEmpty() || bookQuantity.length() == 0 || bookPrice.length() == 0 || supplerNameString.isEmpty() || supplerPhoneString.length() == 0) {
            Toast.makeText(this, getString(R.string.data_missing),
                    Toast.LENGTH_SHORT).show();

        } else {
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_book_NAME, nameString);
            values.put(BookEntry.COLUMN_book_price, priceString);
            values.put(BookEntry.COLUMN_book_Quantity, QuantityString);
            values.put(BookEntry.COLUMN_book_suppler_name, supplerNameString);
            values.put(BookEntry.COLUMN_book_suppler_phone, supplerPhoneString);

            int price = 0;
            if (!TextUtils.isEmpty(priceString)) {
                price = Integer.parseInt(priceString);
            }
            values.put(BookEntry.COLUMN_book_price, price);

            if (mCurrentBookUri == null) {

                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.editor_insert_Book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.Book_saved),
                            Toast.LENGTH_SHORT).show();
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.editor_update_Book_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.editor_update_Book_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save: {
                saveBook();
                return true;
            }

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);

                    return true;
                }


                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_book_NAME,
                BookEntry.COLUMN_book_price,
                BookEntry.COLUMN_book_Quantity,
                BookEntry.COLUMN_book_suppler_name,
                BookEntry.COLUMN_book_suppler_phone};

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
            int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_book_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_book_price);
            int QuantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_book_Quantity);
            int supplerNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_book_suppler_name);
            int supplerPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_book_suppler_phone);


            String name = cursor.getString(nameColumnIndex);
            String supplerName = cursor.getString(supplerNameColumnIndex);
            String supplerphone = cursor.getString(supplerPhoneColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(QuantityColumnIndex);

            bookName.setText(name);
            bookPrice.setText(Integer.toString(price));
            bookQuantity.setText(Integer.toString(quantity));
            SupplerPhone.setText(supplerphone);
            SupplerName.setText(supplerName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        bookName.setText("");
        bookPrice.setText("");
        bookQuantity.setText("");
        SupplerName.setText("");
        SupplerPhone.setText("");
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

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                Toast.makeText(this, getString(R.string.editor_delete_Book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        finish();
    }

}
