package com.mrebhan.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;

public class SpiceDetailsActivity extends AppCompatActivity implements AddFragment.Listener, DetailFragment.Listener {

    private static final String ARG_SPICE_ID = "SpiceDetailsActivity.id";

    public static Intent getIntent(Context context) {
        return new Intent(context, SpiceDetailsActivity.class);
    }

    public static Intent getIntent(Context context, long id) {
        return new Intent(context, SpiceDetailsActivity.class).putExtra(ARG_SPICE_ID, id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {
            if (getIntent().hasExtra(ARG_SPICE_ID)) {
                navigateToFragment(DetailFragment.newFragment(getIntent().getLongExtra(ARG_SPICE_ID, 0)));
            } else {
                navigateToFragment(AddFragment.newFragment());
            }
        }
    }

    private void navigateToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    @Override
    public void onAdd(Spice spice) {
        Paprika.createOrUpdate(spice);
        finish();
    }

    @Override
    public void onEdit(Spice spice) {
        navigateToFragment(AddFragment.newFragment(spice));
    }
}
