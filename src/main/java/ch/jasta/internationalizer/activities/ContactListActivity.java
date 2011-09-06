package ch.jasta.internationalizer.activities;

import java.util.List;

import ch.jasta.internationalizer.R;
import ch.jasta.internationalizer.core.FakeContactCreater;
import ch.jasta.internationalizer.core.InternationalizerCore;
import ch.jasta.internationalizer.model.Contact;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class ContactListActivity extends Activity {
  
  private final String TAG = this.getClass().getSimpleName();
  
  static final String EXTRA_COUNTRY = "COUNTRY";
  
  private String country;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "Creating " + TAG + "...");
    setContentView(R.layout.contact_list);
    this.country = getIntent().getStringExtra(EXTRA_COUNTRY);

    List<Contact> contacts = InternationalizerCore.getContacts(getContentResolver());
    ListView lv = (ListView) findViewById(R.id.contact_list_view);
    lv.setAdapter(new ArrayAdapter<Contact>(this, R.layout.contact_list_item, contacts));
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(new OnItemClickListener() {
      
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Find and update contact numbers
        ListView lv = (ListView) findViewById(R.id.contact_list_view);
        BaseAdapter listAdapter = (BaseAdapter) lv.getAdapter();
        Contact selectedContact = (Contact) listAdapter.getItem(position);
        int updatedNumbers = InternationalizerCore.updateContact(getContentResolver(), selectedContact, country);
        // Update View
        updateListAdapter(updatedNumbers);
      }
    });
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.contact_list_menu, menu);
      return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
      // Handle item selection
      switch (item.getItemId()) {
      case R.id.update_all_menu_entry:
          Log.v(TAG, "Update all clicked.");
          // Update all contacts
          int updatedNumbers = InternationalizerCore.updateAllContacts(getContentResolver(), this.country);
          // Update View
          updateListAdapter(updatedNumbers);
          return true;
      case R.id.country_menu_entry:
        Log.v(TAG, "Country clicked.");
        return false;
      case R.id.donate_menu_entry:
        Log.v(TAG, "Donate clicked.");
        return false;
        
      case R.id.test_data_menu_entry:
        Log.v(TAG, "Generate Test Data clicked.");
        int nrOfContacts = 5;
        FakeContactCreater.generateFakeContacts(getContentResolver(), this, nrOfContacts);
        // Update View
        updateListAdapter(nrOfContacts);
        return true;
      
      default:
        Log.w(TAG, "Clicked non existing Menu Item!");
        return super.onOptionsItemSelected(item);
      }
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

  private void updateListAdapter(int updatedNumbers) {
    // Notify View and user
    ListView lv = (ListView) findViewById(R.id.contact_list_view);
    BaseAdapter listAdapter = (BaseAdapter) lv.getAdapter();
    listAdapter.notifyDataSetChanged();
    String message = "Internationalized " + updatedNumbers + " Numbers.";
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }

}
