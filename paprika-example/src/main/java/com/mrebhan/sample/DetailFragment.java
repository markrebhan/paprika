package com.mrebhan.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;

public class DetailFragment extends Fragment {

    private static final String ARG_SPICE_ID = "spice_id";

    private TextView name;
    private TextView description;
    private TextView scovilleValue;
    private TextView color;
    private TextView genus;
    private TextView species;
    private TextView calories;

    private Spice spice;

    private Listener listener;

    public static DetailFragment newFragment(long spiceId) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARG_SPICE_ID, spiceId);
        detailFragment.setArguments(bundle);

        return detailFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener) context;
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spice = Paprika.get(Spice.class, getArguments().getLong(ARG_SPICE_ID, 0));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        name = (TextView) view.findViewById(R.id.name);
        description = (TextView) view.findViewById(R.id.description);
        scovilleValue = (TextView) view.findViewById(R.id.scovilleValue);
        color = (TextView) view.findViewById(R.id.color);
        genus = (TextView) view.findViewById(R.id.genus);
        species = (TextView) view.findViewById(R.id.species);
        calories = (TextView) view.findViewById(R.id.calories);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        name.setText("Name:" + spice.getName());
        description.setText("Description:" + spice.getDescription());
        scovilleValue.setText("Scoville Value:" + Integer.toString(spice.getScovilleValue()));
        color.setText("Color:" + spice.getColor());

        if (spice.getSpiceScientificData() != null) {
            genus.setText("Genus:" + spice.getSpiceScientificData().getGenus());
            species.setText("Species:" + spice.getSpiceScientificData().getSpecies());
            calories.setText("Calories per 100g:" + Integer.toString(spice.getSpiceScientificData().getkCalPerHundredGrams()));
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                listener.onEdit(spice);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface Listener {
        void onEdit(Spice spice);
    }
}
