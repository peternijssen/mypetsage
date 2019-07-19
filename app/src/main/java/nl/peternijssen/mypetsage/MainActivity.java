package nl.peternijssen.mypetsage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import nl.peternijssen.mypetsage.database.PetsDataSource;
import nl.peternijssen.mypetsage.model.Pet;

public class MainActivity extends AppCompatActivity {

    public PetsDataSource datasource;
    private RecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, PetActivity.class), 1);
            }
        });

        androidx.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        datasource = new PetsDataSource(this);
        datasource.open();

        List<Pet> pets = datasource.getAllPets();

        RecyclerView recyclerView = findViewById(R.id.PetList);
        adapter = new RecyclerViewAdapter(pets, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        handleVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                datasource.open();

                adapter.setPets(datasource.getAllPets());
                adapter.notifyDataSetChanged();
            }
        }

        handleVisibility();
    }

    @Override
    protected void onStart() {
        datasource.open();
        super.onStart();
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void handleVisibility() {
        RecyclerView recyclerView = findViewById(R.id.PetList);
        TextView emptyView = findViewById(R.id.EmptyView);

        if (datasource.getAllPets().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
