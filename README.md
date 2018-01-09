# Android Contact-loader + AsyncTaskLoader

## Android Permission 
* Add permission to your app/src/main/AndroidManifest.xml file
~~~
    <uses-permission android:name="android.permission.READ_CONTACTS" />
~~~

## Retrieving a Cursor of Contacts 

~~~
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

~~~

## AsyncTaskLoader 
* loadInBackground 
~~~
public class ContactLoader extends AsyncTaskLoader<List<Contact>> {

    public ContactLoader(Context context) {
        super(context);
    }

    @Override
    public List<Contact> loadInBackground() {
    ...
    
    }
  }
}
~~~

