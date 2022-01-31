package nl.peternijssen.mypetsage;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import nl.peternijssen.mypetsage.dbs.DAOs;
import nl.peternijssen.mypetsage.dbs.Databases;
import nl.peternijssen.mypetsage.dbs.Entities;

public class MainActivity extends AppCompatActivity {

    private static final int ADD_PET_REQUEST = 1;
    private static final int EDIT_PET_REQUEST = 2;
    private DAOs.PetDao petDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PetActivity.class);
            startActivityForResult(intent, ADD_PET_REQUEST);
        });

        androidx.preference.PreferenceManager
                .setDefaultValues(this, R.xml.preferences, false);

        Databases.PetDatabase petDatabase =
                Databases.PetDatabase.getPetDatabase(this);
        petDao = petDatabase.petDao();

        RecyclerView petRecyclerView = findViewById(R.id.PetList);
        petRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        petRecyclerView.setHasFixedSize(true);
        final PetRecyclerViewAdapter adapter = new PetRecyclerViewAdapter(getApplicationContext());
        petRecyclerView.setAdapter(adapter);

        petDao.getAll().observe(this, adapter::submitList);

        adapter.setOnItemClickListener((pet, options) -> {
            PopupMenu popup = new PopupMenu(getApplicationContext(), options);
            popup.inflate(R.menu.options_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.deletePet:
                        petDao.delete(pet);

                        Toast.makeText(getApplicationContext(), String.format(getApplicationContext().getString(R.string.action_pet_deleted), pet.getName()), Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.editPet:
                        Intent intent = new Intent(MainActivity.this, PetActivity.class);
                        intent.putExtra(PetActivity.EXTRA_ID, pet.getId());
                        intent.putExtra(PetActivity.EXTRA_NAME, pet.getName());
                        intent.putExtra(PetActivity.EXTRA_DATE_OF_BIRTH, pet.getDateOfBirth());
                        intent.putExtra(PetActivity.EXTRA_AVATAR, pet.getAvatar());

                        startActivityForResult(intent, EDIT_PET_REQUEST);
                        return true;
                    default:
                        return false;
                }
            });

            popup.show();
        });
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
        } else if (id == R.id.action_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PET_REQUEST && resultCode == RESULT_OK) {
            String name = data.getStringExtra(PetActivity.EXTRA_NAME);
            String avatar = data.getStringExtra(PetActivity.EXTRA_AVATAR);
            String dateOfBirth = data.getStringExtra(PetActivity.EXTRA_DATE_OF_BIRTH);
            Entities.Pet pet = new Entities.Pet(name, avatar, dateOfBirth);
            petDao.insert(pet);
            Toast.makeText(this, String.format(getApplicationContext().getString(R.string.action_pet_created), name), Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_PET_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(PetActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, R.string.action_pet_update_failed, Toast.LENGTH_SHORT).show();
                return;
            }
            String name = data.getStringExtra(PetActivity.EXTRA_NAME);
            String avatar = data.getStringExtra(PetActivity.EXTRA_AVATAR);
            String dateOfBirth = data.getStringExtra(PetActivity.EXTRA_DATE_OF_BIRTH);
            Entities.Pet pet = new Entities.Pet(name, avatar, dateOfBirth);
            pet.setId(id);
            petDao.update(pet);
            Toast.makeText(this, String.format(getApplicationContext().getString(R.string.action_pet_updated), name), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.action_pet_not_saved, Toast.LENGTH_SHORT).show();
        }
    }
}
