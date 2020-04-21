package com.example.android.bookinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lenovo on 8/3/2018.
 */

public class BookContract {

    private BookContract() {
    }

    public static final String CONTENT_AUTHORITY = "com.example.android.bookinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    public static final class BookEntry implements BaseColumns {
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_book_NAME = "name";

        public final static String COLUMN_book_price = "price";


        public final static String COLUMN_book_Quantity = "Quantity";

        public final static String COLUMN_book_suppler_name = "SupplerName";

        public final static String COLUMN_book_suppler_phone = "Supplerphone";


    }
}
