package ch.jasta.internationalizer.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import ch.jasta.internationalizer.R;

/**
 * Gives a overview over the Internationalization modes.
 * 
 * TODO implement
 * @author jacques
 */
public class OverviewActivity extends Activity {
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.overview);
    
    Button batchButton = (Button) findViewById(R.id.batch_button);
    batchButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), BatchInternationalizerActivity.class);
        startActivity(intent);
        
      }
    });
    
    View singleButton = findViewById(R.id.single_button);
    singleButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), SingleContactInternationalizerActivity.class);
        startActivity(intent);
        
      }
    });
  }
}
