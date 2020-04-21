package com.example.android.bookinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookinventory.data.BookContract.BookEntry;


public class BooksActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final int Book_LOADER = 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BooksActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        ListView bookListView = (ListView) findViewById(R.id.text_view_book);
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(BooksActivity.this, DetailActivity.class);

                Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, id);

                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(Book_LOADER, null, this);
    }


    private void insertBook() {

        ContentValues values = new ContentValues();
        values.put(BookEntry.COLUMN_book_NAME, "The Power");
        values.put(BookEntry.COLUMN_book_price, 20);
        values.put(BookEntry.COLUMN_book_Quantity, 100);
        values.put(BookEntry.COLUMN_book_suppler_name, "Naomi Alderman");
        values.put(BookEntry.COLUMN_book_suppler_phone, "01221214575");
        Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, getResources().getText(R.string.error_saved).toString(), Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, getResources().getText(R.string.book_saved).toString(), Toast.LENGTH_SHORT).show();

    }

    private void deleteAllBooks() {
        int rowsDeleted = getContentResolver().delete(BookEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;
            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_book_NAME,
                BookEntry.COLUMN_book_Quantity,
                BookEntry.COLUMN_book_price};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                BookEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}

