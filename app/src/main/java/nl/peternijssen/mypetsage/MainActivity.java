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

import nl.peternijssen.mypetsage.dbs.DAOs;
import nl.peternijssen.mypetsage.dbs.Databases;
import nl.peternijssen.mypetsage.dbs.Entities;

public class MainActivity extends AppCompatActivity {

    private DAOs.PetDao petDao;

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

        Databases.PetDatabase petDatabase =
                Databases.PetDatabase.getPetDatabase(this);
        petDao = petDatabase.petDao();

        List<Entities.Pet> pets = petDao.getAll();

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
                adapter.setPets(petDao.getAll());
                adapter.notifyDataSetChanged();
            }
        }

        handleVisibility();
    }

    public void handleVisibility() {
        RecyclerView recyclerView = findViewById(R.id.PetList);
        TextView emptyView = findViewById(R.id.EmptyView);

        if (petDao.getAll().isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }
}
