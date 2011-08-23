package ch.jasta.internationalizer.core;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import ch.jasta.internationalizer.model.Contact;
import ch.jasta.internationalizer.model.Number;

/**
 * Does the actual Internationalisation and updating of the contacts.
 * 
 * @author jacques
 */
public class InternationalizerCore {
  
  private static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;
  private static final String CONTACT_ID = ContactsContract.Contacts._ID;
  private static final String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
  private static final String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;
  
  private static final Uri PHONES_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
  private static final String PHONE_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
  private static final String PHONE_NUM = ContactsContract.CommonDataKinds.Phone.NUMBER;
  private static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;
  private static final String PHONE_ID = ContactsContract.CommonDataKinds.Phone._ID;
  
  public static List<Contact> getContacts(ContentResolver cr) {
    List<Contact> contacts = new LinkedList<Contact>();
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
  
  
  public static int updateContact(ContentResolver cr, Contact contact, String countryCode) {
    int totalUpdatedRows = 0;
    for (Number currentNumber : contact.getNumbers()) {
      // Write to contact
      String internationalNumber = currentNumber.getInternationalNumber(countryCode);
      currentNumber.setNumber(internationalNumber);
      
      // Write to content provider
      ContentValues newValues = new ContentValues();
      newValues.put(PHONE_NUM, internationalNumber);
      Uri currentPhoneUri = ContentUris.withAppendedId(Phone.CONTENT_URI, currentNumber.getId());
      totalUpdatedRows += cr.update(currentPhoneUri, newValues, null, null);
    }
    return totalUpdatedRows;
  }
  
  public static int updateAllContacts(ContentResolver cr, String contryCode) {
    List<Contact> contacts = getContacts(cr);
    int totalUpdatedNumbers = 0;
    for (Contact contact : contacts) {
      totalUpdatedNumbers += updateContact(cr, contact, contryCode);
    }
    return totalUpdatedNumbers;
  }
}
