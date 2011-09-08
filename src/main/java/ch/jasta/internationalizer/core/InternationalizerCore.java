package ch.jasta.internationalizer.core;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts;
import android.util.Log;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

import ch.jasta.internationalizer.model.Contact;
import ch.jasta.internationalizer.model.Number;

/**
 * Does the actual Internationalisation and updating of the contacts.
 * 
 * @author jacques
 */
public class InternationalizerCore {
  
  private static final String TAG = InternationalizerCore.class.getSimpleName();
  
  public static List<Contact> getContacts(ContentResolver cr, String countryCode) {
    List<Contact> contacts = new LinkedList<Contact>();
    Cursor contactsCursor = cr.query(Contacts.CONTENT_URI, null, null, null, null);
    if (contactsCursor.getCount() > 0) {

      // Loop over all contacts
      while (contactsCursor.moveToNext()) {

        String id = contactsCursor.getString(contactsCursor.getColumnIndex(Contacts._ID));
        String displayName = contactsCursor.getString(contactsCursor.getColumnIndex(Contacts.DISPLAY_NAME));
        Contact contact = new Contact(id, displayName);
        contacts.add(contact);
        if (Integer.parseInt(contactsCursor.getString(contactsCursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER))) > 0) {
          Cursor phonesCursor = cr.query(Phone.CONTENT_URI, null, Phone.CONTACT_ID + " = ?", new String[]{id},
              null);
          // Loop over all numbers
          while (phonesCursor.moveToNext()) {
            long phoneId = phonesCursor.getLong(phonesCursor.getColumnIndex(Phone._ID));
            String number = phonesCursor.getString(phonesCursor.getColumnIndex(Phone.NUMBER));
            String type = phonesCursor.getString(phonesCursor.getColumnIndex(Phone.TYPE));
            String internationalNumber = getInternationalNumber(number, countryCode);
            contact.addNumber(new Number(phoneId, number, type, internationalNumber));
          }
          phonesCursor.close();
        }
      }
    }
    return contacts;
  }
  
  
  public static int updateContact(ContentResolver cr, Contact contact) {
    int totalUpdatedRows = 0;
    for (Number currentNumber : contact.getNumbers()) {
      // Write to content provider
      ContentValues newValues = new ContentValues();
      newValues.put(Phone.NUMBER, currentNumber.getInternationalNumber());
      Uri currentPhoneUri = ContentUris.withAppendedId(Phone.CONTENT_URI, currentNumber.getId());
      totalUpdatedRows += cr.update(currentPhoneUri, newValues, null, null);
      currentNumber.setNumber(currentNumber.getInternationalNumber());
    }
    return totalUpdatedRows;
  }
  
  public static int updateAllContacts(ContentResolver cr, List<Contact> contacts) {
    int totalUpdatedNumbers = 0;
    for (Contact contact : contacts) {
      totalUpdatedNumbers += updateContact(cr, contact);
    }
    return totalUpdatedNumbers;
  }
  
  public static String getInternationalNumber(String nationalNumber, String countryCode) {
    PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
    PhoneNumber phoneNumber;
    try {
      phoneNumber = phoneUtil.parse(nationalNumber, countryCode);
    } catch (NumberParseException e) {
      Log.i(TAG, "Number " + nationalNumber + " could not be parsed for country code " + countryCode);
      return nationalNumber;
    }
    if (!phoneUtil.isValidNumber(phoneNumber)) {
      Log.w(TAG, "Number " + nationalNumber + " is not valid for country code " + countryCode);
      return nationalNumber;
    }
    String internationalNumber = phoneUtil.format(phoneNumber, PhoneNumberFormat.INTERNATIONAL);
    return internationalNumber;
  }
}
