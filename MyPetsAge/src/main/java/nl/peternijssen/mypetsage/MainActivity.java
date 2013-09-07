package nl.peternijssen.mypetsage;

import android.app.ListActivity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

import java.util.List;

import nl.peternijssen.mypetsage.database.PetsDataSource;
import nl.peternijssen.mypetsage.model.Pet;
import nl.peternijssen.mypetsage.widget.PetWidgetProvider;

public class MainActivity extends ListActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private PetsDataSource datasource;

    private Boolean isPreferenceChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isPreferenceChanged = false;

        datasource = new PetsDataSource(this);
        datasource.open();

        List<Pet> values = datasource.getAllPets();

        ListAdapter adapter = new ListAdapter(values, this);
        setListAdapter(adapter);

        getListView().setEmptyView(findViewById(R.id.empty_list));

        registerForContextMenu(getListView());

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_add_pet:
                startActivityForResult(new Intent(this, PetActivity.class), 1);
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();

        switch (item.getItemId()) {
            case R.id.action_delete_pet:
                datasource.deletePet(datasource.getAllPets().get(info.position));
                ListAdapter adapter = (ListAdapter) getListAdapter();
                adapter.setPets(datasource.getAllPets());
                adapter.notifyDataSetChanged();
                updateAllWidgets();
                return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
                datasource.open();
                ListAdapter adapter = (ListAdapter) getListAdapter();
                adapter.setPets(datasource.getAllPets());
                adapter.notifyDataSetChanged();
                updateAllWidgets();
            }
            if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    protected void onStart() {
        datasource.open();
        super.onStart();

        if(isPreferenceChanged)  {
            ((ListAdapter)getListAdapter()).notifyDataSetChanged();
            isPreferenceChanged = false;
        }
    }

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();

        if(isPreferenceChanged)  {
            ((ListAdapter)getListAdapter()).notifyDataSetChanged();
            isPreferenceChanged = false;
        }
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        updateAllWidgets();
        isPreferenceChanged = true;
    }

    public void updateAllWidgets() {
        Intent intent = new Intent(this,PetWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), PetWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
