package com.contact.adapters;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.contact.pojo.Contact;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ContactLoader extends AsyncTaskLoader<List<Contact>> {

    public ContactLoader(Context context) {
        super(context);
    }

    @Override
    public List<Contact> loadInBackground() {
        /* contact list to be returned */
        List<Contact> contacts = new ArrayList<>();

		/* Map which removes the duplicate values */
        Map<String, Contact> map = new LinkedHashMap<>();

		/* creating the projection which indicates what all data we want from our contacts database */
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI
        };

        Cursor contact_cursor = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        try {
            contact_cursor.moveToFirst();
            do {
                    Contact contact = new Contact();
                    contact.set_id(contact_cursor.getString(0));
                    contact.setContactId(contact_cursor.getString(1));
                    contact.setDisplayName(contact_cursor.getString(2));
                    contact.setNumber(contact_cursor.getString(3));
                    contact.setEmailAddress(contact_cursor.getString(4));
                    contact.setUserThumbnail(contact_cursor.getString(5));
                    map.put(contact.getNumber(), contact);
            } while (contact_cursor.moveToNext());

            contact_cursor.close();
            contacts.addAll(map.values());

            return contacts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
