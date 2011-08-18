package ch.jasta.internationalizer.activities;

import java.util.LinkedList;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ch.jasta.internationalizer.R;
import ch.jasta.internationalizer.model.Contact;
import ch.jasta.internationalizer.model.Number;


/**
 * A list of all the contacts, with numbers that lets you update each contact
 * by taping it.
 * 
 * @author jacques
 */
public class SingleContactInternationalizerActivity extends ListActivity {

  private static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
  private static final String CONTACT_ID = ContactsContract.Contacts._ID;
  private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
  private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
  
  private static final Uri PHONES_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
  private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
  private static final String PHONE_NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
  private static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
  private static final String PHONE_ID = ContactsContract.CommonDataKinds.Phone._ID;


  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);

    this.setListAdapter(new ArrayAdapter<Contact>(this, R.layout.contact_list_item, getContacts()));
    
    ListView lv = getListView();
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(new OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Find and update contact numbers
        BaseAdapter listAdapter = (BaseAdapter) getListAdapter();
        Contact selectedContact = (Contact) listAdapter.getItem(position);
        int updatedNumbers = updateContact(selectedContact, "CH");
        // Notify View and user
        listAdapter.notifyDataSetChanged();
        String message = "Internationalized " + updatedNumbers + " Numbers.";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  protected void onStart() {
      super.onStart();
      // The activity is about to become visible.
  }
  @Override
  protected void onResume() {
      super.onResume();
      // The activity has become visible (it is now "resumed").
  }
  @Override
  protected void onPause() {
      super.onPause();
      // Another activity is taking focus (this activity is about to be "paused").
  }
  @Override
  protected void onStop() {
      super.onStop();
      // The activity is no longer visible (it is now "stopped")
  }
  @Override
  protected void onDestroy() {
      super.onDestroy();
      // The activity is about to be destroyed.
  }
  
  private List<Contact> getContacts() {
    List<Contact> contacts = new LinkedList<Contact>();
    ContentResolver cr = getContentResolver();
    Cursor contactsCursor = cr.query(CONTACTS_URI, null, null, null, null);
    if (contactsCursor.getCount() > 0) {

      // Loop over all contacts
      while (contactsCursor.moveToNext()) {

        String id = contactsCursor.getString(contactsCursor.getColumnIndex(CONTACT_ID));
        String displayName = contactsCursor.getString(contactsCursor.getColumnIndex(DISPLAY_NAME));
        Contact contact = new Contact(id, displayName);
        contacts.add(contact);
        if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(HAS_PHONE_NUMBER))) > 0) {
          Cursor phonesCursor = cr.query(PHONES_URI, null, PHONE_CONTACT_ID + " = ?", new String[]{id},
              null);
          // Loop over all numbers
          while (phonesCursor.moveToNext()) {
            long phoneId = phonesCursor.getLong(phonesCursor.getColumnIndex(PHONE_ID));
            String number = phonesCursor.getString(phonesCursor.getColumnIndex(PHONE_NUM));
            String type = phonesCursor.getString(phonesCursor.getColumnIndex(PHONE_TYPE));
            contact.addNumber(new Number(phoneId, number, type));
          }
          phonesCursor.close();
        }
      }
    }
    return contacts;
  }
  
  
  private int updateContact(Contact contact, String countryCode) {
    int totalUpdatedRows = 0;
    for (Number currentNumber : contact.getNumbers()) {
      // Write to contact
      String internationalNumber = currentNumber.getInternationalNumber(countryCode);
      currentNumber.setNumber(internationalNumber);
      
      // Write to content provider
      ContentValues newValues = new ContentValues();
      newValues.put(PHONE_NUM, internationalNumber);
      Uri currentPhoneUri = ContentUris.withAppendedId(Phone.CONTENT_URI, currentNumber.getId());
      totalUpdatedRows += getContentResolver().update(currentPhoneUri, newValues, null, null);
    }
    return totalUpdatedRows;
  }
  
}
