package ch.jasta.internationalizer.activities;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import ch.jasta.internationalizer.R;

/**
 * Gives a short introduction about the application and asks for the country.
 * Only shown on first start of application.
 * 
 * @author jacques
 */
public class IntroActivity extends Activity {
  
  private final String TAG = this.getClass().getSimpleName();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.i(TAG, "Creating " + TAG + "...");
    setContentView(R.layout.country_selection);
    
    List<String> isoCountries = Arrays.asList(Locale.getISOCountries());
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
        this, android.R.layout.simple_spinner_item, isoCountries);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    
    Spinner s = (Spinner) findViewById(R.id.country_selection_spinner);
    s.setAdapter(adapter);
    
    Button internationalizButton = (Button) findViewById(R.id.internationalize_button);
    internationalizButton.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), ContactListActivity.class);
        Spinner s = (Spinner) findViewById(R.id.country_selection_spinner);
        String selectedCountry = (String) s.getSelectedItem();
        intent.putExtra(ContactListActivity.EXTRA_COUNTRY, selectedCountry);
        startActivity(intent);
      }
      
    });
  }
  
}
