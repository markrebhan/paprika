package com.mrebhan.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;

import static com.mrebhan.sample.data.Spice.SAVORY;

public class AddSpiceActivity extends AppCompatActivity {

    private static final String ARG_SPICE_MODEL = "AddSpiceActivity.name";

    private EditText name;
    private Button addButton;

    private Spice spice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText) findViewById(R.id.name);
        addButton = (Button) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spice.setName(name.getText().toString());

                if (spice.getName() != null) {
                    Paprika.save(spice);
                    startActivity(new Intent(AddSpiceActivity.this, MainActivity.class));
                }
            }
        });

        if (savedInstanceState == null) {
            spice = new Spice();
            spice.setFlavor(SAVORY);
            spice.setTastiness(1);
        } else {
            spice = savedInstanceState.getParcelable(ARG_SPICE_MODEL);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_SPICE_MODEL, spice);
    }
}
