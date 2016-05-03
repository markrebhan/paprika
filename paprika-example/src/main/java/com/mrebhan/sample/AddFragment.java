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
import android.widget.Button;
import android.widget.EditText;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;
import com.mrebhan.sample.data.SpiceScientificData;

public class AddFragment extends Fragment {

    private static final String ARG_SPICE = "spice";

    private EditText name;
    private EditText description;
    private EditText scovilleValue;
    private EditText color;
    private EditText genus;
    private EditText species;
    private EditText calories;
    private Button addButton;

    private Listener listener;

    private Spice spice;

    public static AddFragment newFragment() {
        return new AddFragment();
    }
    public static AddFragment newFragment(Spice spice) {
        AddFragment addFragment = new AddFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_SPICE, spice);
        addFragment.setArguments(bundle);

        return addFragment;
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

        if (getArguments() != null) {
            spice = getArguments().getParcelable(ARG_SPICE);
        } else {
            spice = new Spice();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (getArguments() != null) {
            inflater.inflate(R.menu.menu_add, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                Paprika.delete(spice);
                getActivity().finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        name = (EditText) view.findViewById(R.id.name);
        description = (EditText) view.findViewById(R.id.description);
        scovilleValue = (EditText) view.findViewById(R.id.scovilleValue);
        color = (EditText) view.findViewById(R.id.color);
        genus = (EditText) view.findViewById(R.id.genus);
        species = (EditText) view.findViewById(R.id.species);
        calories = (EditText) view.findViewById(R.id.calories);
        addButton = (Button) view.findViewById(R.id.add_button);

        name.setText(spice.getName());
        description.setText(spice.getDescription());
        scovilleValue.setText(Integer.toString(spice.getScovilleValue()));
        color.setText(spice.getColor());

        if (spice.getSpiceScientificData() != null) {
            genus.setText(spice.getSpiceScientificData().getGenus());
            species.setText(spice.getSpiceScientificData().getSpecies());
            calories.setText(Integer.toString(spice.getSpiceScientificData().getkCalPerHundredGrams()));
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText() != null) {
                    spice.setName(name.getText().toString());
                    spice.setDescription(description.getText().toString());
                    spice.setScovilleValue(Integer.parseInt(scovilleValue.getText().toString()));
                    spice.setColor(color.getText().toString());
                    spice.setSpiceScientificData(new SpiceScientificData());
                    spice.getSpiceScientificData().setGenus(genus.getText().toString());
                    spice.getSpiceScientificData().setSpecies(species.getText().toString());
                    spice.getSpiceScientificData().setkCalPerHundredGrams(Integer.parseInt(calories.getText().toString()));
                    listener.onAdd(spice);
                }
            }
        });
    }

    public interface Listener {
        void onAdd(Spice spice);
    }
}
