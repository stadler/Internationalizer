/**
 * 
 */
package ch.jasta.internationalizer.core;

import java.util.ArrayList;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.widget.Toast;


/**
 * Creates fake contacts for testing.
 */
public class FakeContactCreater {
  
  private static final String TAG = FakeContactCreater.class.getSimpleName();
  
  private static int contactsCounter = 0;
  
  public static void generateFakeContacts(ContentResolver cr, Context context, int nrOfContacts) {
    deleteAllContacts(cr);
    
    Account[] accounts = AccountManager.get(context).getAccounts();
    for (Account account : accounts) {
      Log.i(TAG, "Account: " + String.valueOf(account.name) + ", " + String.valueOf(account.type));
    }
    AuthenticatorDescription[] types = AccountManager.get(context).getAuthenticatorTypes();
    for (AuthenticatorDescription type : types) {
      Log.i(TAG, "AuthenticatorDescription: " + String.valueOf(type.type) + ", " + String.valueOf(type.toString()));
    }
    
    for (int i=0; i<nrOfContacts; i++) {
      addFakeContact(cr, context);
    }
    Log.d(TAG, nrOfContacts + " Contacts created.");
  }
  
  private static void deleteAllContacts(ContentResolver cr) {
    int contactsDeleted = cr.delete(RawContacts.CONTENT_URI, null, null);
    Log.d(TAG, contactsDeleted + " Contacts deleted.");
    contactsCounter = 0;
  }
  
  private static void addFakeContact(ContentResolver cr, Context context) {
    String accountName = "";
    String accountType = "";
    String name = "TestContact" + ++contactsCounter;
    String phone = "076349070" + contactsCounter;
    
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
//    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
//            .withValue(ContactsContract.Data.MIMETYPE,
//                    ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
//            .withValue(ContactsContract.CommonDataKinds.Email.DATA, email)
//            .withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType)
//            .build());

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
  }
  
  /**
   * insert a new data item first we need to get a raw contact corresponding to
   * the contact. 
   */
  private void stackoverflow(ContentResolver cr, long contactId) {
    
    Cursor rawCur = cr.query(RawContacts.CONTENT_URI,
        new String[]{RawContacts._ID}, RawContacts.CONTACT_ID + "=?",
        new String[]{String.valueOf(contactId)}, null);

    long rawContactId = -1;
    for (boolean moreRaws = rawCur.moveToFirst(); moreRaws; moreRaws = rawCur.moveToNext()) {
      rawContactId = rawCur.getLong(rawCur.getColumnIndex(RawContacts._ID));
    }

    ArrayList ops = new ArrayList();

    ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
        .withValue(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
        .withValue(ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, "y123-456-7890")
        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME)
        .build());

    try {
      ContentProviderResult[] results = cr.applyBatch(ContactsContract.AUTHORITY, ops);
      Log.i(TAG, "result: " + results[0]);
    } catch (UnsupportedOperationException ex) {
      ex.printStackTrace();
    } catch (RemoteException ex) {
      ex.printStackTrace();
    } catch (OperationApplicationException ex) {
      ex.printStackTrace();
    }
  }
}
