package nl.peternijssen.mypetsage;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;

import org.w3c.dom.Comment;

import java.util.List;
import java.util.Random;

import nl.peternijssen.mypetsage.database.PetsDataSource;
import nl.peternijssen.mypetsage.model.Pet;
import nl.peternijssen.mypetsage.widget.PetWidgetProvider;

public class PetActivity extends Activity {

    private PetsDataSource datasource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        datasource = new PetsDataSource(this);
        datasource.open();
    }

    public void onClick(View view) {
        EditText petName = (EditText)findViewById(R.id.PetName);
        DatePicker petDateOfBirth = (DatePicker)findViewById(R.id.PetDateOfBirth);

        CharSequence date = "" + petDateOfBirth.getDayOfMonth() + "-" + "" + (petDateOfBirth.getMonth() + 1) + "-" + "" + petDateOfBirth.getYear();

        datasource.createPet(petName.getText().toString(), "none", date.toString());

        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
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
}
