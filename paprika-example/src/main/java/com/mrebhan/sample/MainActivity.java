package com.mrebhan.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mrebhan.R;
import com.mrebhan.paprika.Paprika;
import com.mrebhan.sample.data.Spice;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        adapter = new Adapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Spice> spices = Paprika.getList(Spice.class);
        adapter.addSpices(spices);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                startActivity(SpiceDetailsActivity.getIntent(this));
                return true;
        }

        return false;
    }

    private class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Spice> spiceList = new ArrayList<>();

        public void addSpices(List<Spice> spices) {
            spiceList.clear();
            spiceList.addAll(spices);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_spice, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bind(spiceList.get(position));
        }

        @Override
        public int getItemCount() {
            return spiceList.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView flavor;
        private TextView tastiness;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            flavor = (TextView) itemView.findViewById(R.id.flavor);
            tastiness = (TextView) itemView.findViewById(R.id.tastiness);
        }

        public void bind(final Spice spice) {
            name.setText(spice.getName());
            flavor.setText(spice.getFlavorString());
            tastiness.setText(Integer.toString(spice.getTastiness()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(SpiceDetailsActivity.getIntent(itemView.getContext(), spice.getId()));
                }
            });
        }
    }
 }
