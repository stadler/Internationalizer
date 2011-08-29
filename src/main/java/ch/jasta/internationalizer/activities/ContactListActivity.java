package ch.jasta.internationalizer.activities;

import java.util.List;

import ch.jasta.internationalizer.R;
import ch.jasta.internationalizer.core.InternationalizerCore;
import ch.jasta.internationalizer.model.Contact;

import android.app.Activity;
import android.os.Bundle;
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
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.contact_list);

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
        int updatedNumbers = InternationalizerCore.updateContact(getContentResolver(), selectedContact, "CH");
        // Notify View and user
        listAdapter.notifyDataSetChanged();
        String message = "Internationalized " + updatedNumbers + " Numbers.";
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
      case R.id.update_all:
          // Update all contacts
          int updatedNumbers = InternationalizerCore.updateAllContacts(getContentResolver(), "CH");
          // Update View
          ListView lv = (ListView) findViewById(R.id.contact_list_view);
          BaseAdapter listAdapter = (BaseAdapter) lv.getAdapter();
          listAdapter.notifyDataSetChanged();
          // Notify User
          String message = "Updated " + updatedNumbers + " numbers";
          Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
          return true;
      default:
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

}
