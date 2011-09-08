/**
 * 
 */
package ch.jasta.internationalizer.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.widget.Toast;

import ch.jasta.internationalizer.model.Contact;
import ch.jasta.internationalizer.model.Number;


/**
 * Creates fake contacts for testing.
 */
public class FakeContactCreater {
  
  private static final String TAG = FakeContactCreater.class.getSimpleName();
  
  private static int contactsCounter = 0;
  
  public static List<Contact> generateFakeContacts(ContentResolver cr, Context context, int nrOfContacts) {
    deleteAllContacts(cr);
    
    Account[] accounts = AccountManager.get(context).getAccounts();
    for (Account account : accounts) {
      Log.i(TAG, "Account: " + String.valueOf(account.name) + ", " + String.valueOf(account.type));
    }
    AuthenticatorDescription[] types = AccountManager.get(context).getAuthenticatorTypes();
    for (AuthenticatorDescription type : types) {
      Log.i(TAG, "AuthenticatorDescription: " + String.valueOf(type.type) + ", " + String.valueOf(type.toString()));
    }
    
    List<Contact> contactList = new LinkedList<Contact>();
    for (int i=0; i<nrOfContacts; i++) {
      contactList.add(addFakeContact(cr, context));
    }
    Log.d(TAG, nrOfContacts + " Contacts created.");
    return contactList;
  }
  
  private static void deleteAllContacts(ContentResolver cr) {
    int contactsDeleted = cr.delete(RawContacts.CONTENT_URI, null, null);
    Log.d(TAG, contactsDeleted + " Contacts deleted.");
    contactsCounter = 0;
  }
  
  private static Contact addFakeContact(ContentResolver cr, Context context) {
    String accountName = "";
    String accountType = "";
    String name = "TestContact" + ++contactsCounter;
    String phone = "076349070" + contactsCounter;
    String internationalPhone = InternationalizerCore.getInternationalNumber(phone, "CH");
    
    Contact contact = new Contact(String.valueOf(contactsCounter), name);
    contact.addNumber(new Number(contactsCounter, phone, "TELEX", internationalPhone));
    
    ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
    ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
            .withValue(RawContacts.ACCOUNT_TYPE, accountType)
            .withValue(RawContacts.ACCOUNT_NAME, accountName)
            .build());
    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
            .withValue(StructuredName.DISPLAY_NAME, name)
            .build());
    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
            .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
            .withValue(Phone.NUMBER, phone)
            .withValue(Phone.TYPE, Integer.valueOf(Phone.TYPE_HOME))
            .build());

    // Ask the Contact provider to create a new contact
    Log.i(TAG,"Selected account: " + accountName + " (" + accountType + ")");
    Log.i(TAG,"Creating contact: " + name);
    try {
        cr.applyBatch(ContactsContract.AUTHORITY, ops);
    } catch (Exception e) {
      // Display warning
      CharSequence txt = "Fail!";
      int duration = Toast.LENGTH_SHORT;
      Toast toast = Toast.makeText(context, txt, duration);
      toast.show();

      // Log exception
      Log.e(TAG, "Exceptoin encoutered while inserting contact: " + e);
    }
    return contact;
  }
}
