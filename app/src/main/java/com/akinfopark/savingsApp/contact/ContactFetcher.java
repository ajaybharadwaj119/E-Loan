package com.akinfopark.savingsApp.contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.akinfopark.savingsApp.db.DatabaseHelper;


public class ContactFetcher {
    private Context context;
    DatabaseHelper databaseHelper;

    public ContactFetcher(Context context) {
        this.context = context;
    }

    public void fetchContacts() {
        ContentResolver contentResolver = context.getContentResolver();
        databaseHelper = new DatabaseHelper(context.getApplicationContext());

        // Define the projection (columns to retrieve)
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        // Define the selection criteria (none for all contacts)
        String selection = null;
        String[] selectionArgs = null;

        // Define the sort order (optional)
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        // Perform the query
        Cursor cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        if (cursor != null) {
            try {
                // Iterate through the cursor to get contact names and phone numbers
                while (cursor.moveToNext()) {
                    String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    databaseHelper.addOne(contactName, phoneNumber);
                    // Do something with the contact name and phone number (e.g., log or display)
                    Log.d("ContactFetcher", "Name: " + contactName + ", Phone: " + phoneNumber);
                }
            } finally {
                cursor.close(); // Don't forget to close the cursor to avoid memory leaks
            }
        }
    }
}
