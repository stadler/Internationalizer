package ch.jasta.internationalizer.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ch.jasta.internationalizer.R;
import ch.jasta.internationalizer.core.InternationalizerCore;
import ch.jasta.internationalizer.model.Contact;


/**
 * A list of all the contacts, with numbers that lets you update each contact
 * by taping it.
 * 
 * @author jacques
 */
public class SingleContactInternationalizerActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.single_update);
    
    ListView lv = (ListView) findViewById(R.id.single_update_list_view);
    List<Contact> contacts = InternationalizerCore.getContacts(getContentResolver());
    lv.setAdapter(new ArrayAdapter<Contact>(this, R.layout.contact_list_item, contacts));
    lv.setTextFilterEnabled(true);
    lv.setOnItemClickListener(new OnItemClickListener() {
      
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Find and update contact numbers
        ListView lv = (ListView) findViewById(R.id.single_update_list_view);
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
