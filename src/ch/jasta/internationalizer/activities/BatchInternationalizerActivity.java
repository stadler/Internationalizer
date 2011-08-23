package ch.jasta.internationalizer.activities;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ch.jasta.internationalizer.R;
import ch.jasta.internationalizer.core.InternationalizerCore;
import ch.jasta.internationalizer.model.Contact;


/**
 * Shows list of all updates that will be done and a button to really do it.
 * TODO implement
 * @author jacques
 */
public class BatchInternationalizerActivity extends Activity {

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.batch_update);
    
    List<Contact> contacts = InternationalizerCore.getContacts(getContentResolver());
    ListView lv = (ListView) findViewById(R.id.batch_update_list_view);
    lv.setAdapter(new ArrayAdapter<Contact>(this, R.layout.contact_list_item, contacts));
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.batch_menu, menu);
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
          ListView lv = (ListView) findViewById(R.id.batch_update_list_view);
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
}
