package com.mrebhan.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;

import static com.mrebhan.sample.data.Spice.SAVORY;

public class SpiceDetailsActivity extends AppCompatActivity {

    private static final String ARG_SPICE_MODEL = "SpiceDetailsActivity.name";
    private static final String ARG_SPICE_ID = "SpiceDetailsActivity.id";
    private static final String ARG_IS_EDIT = "SpiceDetailsActivity.isEdit";

    private EditText name;
    private EditText description;
    private Button addButton;

    private Spice spice;
    private boolean isEdit;

    public static Intent getIntent(Context context) {
        return new Intent(context, SpiceDetailsActivity.class);
    }

    public static Intent getIntent(Context context, long id) {
        return new Intent(context, SpiceDetailsActivity.class).putExtra(ARG_SPICE_ID, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText) findViewById(R.id.name);
        description = (EditText) findViewById(R.id.description);
        addButton = (Button) findViewById(R.id.add_button);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                spice.setName(name.getText().toString());
                spice.setDescription(description.getText().toString());

                if (spice.getName() != null) {
                    Paprika.createOrUpdate(spice, spice.getId());
                    startActivity(new Intent(SpiceDetailsActivity.this, MainActivity.class));
                }
            }
        });

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(ARG_SPICE_ID)) {
                spice = Paprika.get(Spice.class, getIntent().getLongExtra(ARG_SPICE_ID, 0));
                isEdit = true;
            } else {
                spice = new Spice();
                spice.setFlavor(SAVORY);
                spice.setTastiness(1);
            }
        } else {
            spice = savedInstanceState.getParcelable(ARG_SPICE_MODEL);
            isEdit = savedInstanceState.getBoolean(ARG_IS_EDIT);
        }

        name.setText(spice.getName());
        description.setText(spice.getDescription());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) {
            getMenuInflater().inflate(R.menu.menu_details, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                Paprika.delete(Spice.class, spice.getId());
                finish();
                return true;
        }

        return false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_SPICE_MODEL, spice);
        outState.putBoolean(ARG_IS_EDIT, isEdit);
    }
}
