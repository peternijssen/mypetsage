package nl.peternijssen.mypetsage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PetActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final String EXTRA_ID = "nl.peternijssen.mypetsage.EXTRA_ID";
    public static final String EXTRA_NAME = "nl.peternijssen.mypetsage.EXTRA_NAME";
    public static final String EXTRA_AVATAR = "nl.peternijssen.mypetsage.EXTRA_AVATAR";
    public static final String EXTRA_DATE_OF_BIRTH = "nl.peternijssen.mypetsage.EXTRA_DATE_OF_BIRTH";
    public static final String EXTRA_STATUS = "nl.peternijssen.mypetsage.EXTRA_STATUS";
    public static final String EXTRA_DATE_OF_DECEASE = "nl.peternijssen.mypetsage.EXTRA_DATE_OF_DECEASE";
    private static final int IMAGE = 100;
    private File avatarFile = null;
    private EditText nameEdt, dateOfBirthEdt, dateOfDeceaseEdt;
    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameEdt = findViewById(R.id.PetName);
        dateOfBirthEdt = findViewById(R.id.PetDateOfBirth);
        dateOfDeceaseEdt = findViewById(R.id.PetDateOfDecease);
        ImageButton avatarBtn = findViewById(R.id.PetAvatar);

        Spinner spinner = (Spinner) findViewById(R.id.petStatus);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.petStatus, android.R.layout.simple_spinner_item);
        spinner.setOnItemSelectedListener(this);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            setTitle(R.string.action_edit_pet);

            nameEdt.setText(intent.getStringExtra(EXTRA_NAME));

            Date dateOfBirth = new Date();
            dateOfBirth.setTime(intent.getLongExtra(PetActivity.EXTRA_DATE_OF_BIRTH, -1));
            dateOfBirthEdt.setText(dateOfBirth.toString());

            Date dateOfDecease = new Date();
            dateOfBirth.setTime(intent.getLongExtra(PetActivity.EXTRA_DATE_OF_DECEASE, -1));
            dateOfDeceaseEdt.setText(dateOfDecease.toString());

            String petStatus = intent.getStringExtra(EXTRA_STATUS);
            if (petStatus.equals("deceased")) {
                spinner.setSelection(1);
            }

            // Set avatar on edit
            avatarFile = new File(intent.getStringExtra(EXTRA_AVATAR));
            if (avatarFile.exists()) {
                try {
                    InputStream inputStream = new FileInputStream(avatarFile);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    avatarBtn.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateOfBirthEdt.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PetActivity.this, (datePicker, years, months, days) -> {
                String dates = days + "-" + (months + 1) + "-" + years;
                dateOfBirthEdt.setText(dates);
            }, year, month, day);
            datePickerDialog.show();
        });

        dateOfDeceaseEdt.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(PetActivity.this, (datePicker, years, months, days) -> {
                String dates = days + "-" + (months + 1) + "-" + years;
                dateOfDeceaseEdt.setText(dates);
            }, year, month, day);
            datePickerDialog.show();
        });

        avatarBtn.setOnClickListener(v -> {
            Intent avatarIntent = new Intent(Intent.ACTION_PICK);
            avatarIntent.setType("image/*");
            String[] mimeTypes = {"image/jpeg", "image/png"};
            avatarIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            startActivityForResult(avatarIntent, IMAGE);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pet_save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                String name = nameEdt.getText().toString();
                String dob = dateOfBirthEdt.getText().toString();
                String dod = dateOfDeceaseEdt.getText().toString();
                Date dateOfBirth = new Date();
                Date dateOfDecease = new Date();

                DateFormat df = new SimpleDateFormat("d-M-y", Locale.getDefault());
                try {
                    dateOfBirth = df.parse(dob);
                }  catch (ParseException e) {
                    // Do nothing
                }

                try {
                    dateOfDecease = df.parse(dod);
                }  catch (ParseException e) {
                    // Do nothing
                }

                if (name.isEmpty() || dob.isEmpty()) {
                    Toast.makeText(PetActivity.this, R.string.pet_form_failed, Toast.LENGTH_SHORT).show();
                } else {
                    savePet(name, dateOfBirth, dateOfDecease);
                }
                break;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePet(String name, Date dateOfBirth, Date dateOfDecease) {
        Intent data = new Intent();

        data.putExtra(EXTRA_NAME, name);
        if (avatarFile == null) {
            data.putExtra(EXTRA_AVATAR, "none");
        } else {
            data.putExtra(EXTRA_AVATAR, avatarFile.getAbsolutePath());
        }
        data.putExtra(EXTRA_DATE_OF_BIRTH, dateOfBirth.getTime());
        data.putExtra(EXTRA_STATUS, status);
        data.putExtra(EXTRA_DATE_OF_DECEASE, dateOfDecease.getTime());
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE && resultCode == RESULT_OK && null != data) {
            Uri inputUri = data.getData();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("avatars", Context.MODE_PRIVATE);
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm", Locale.getDefault()).format(new Date());
            avatarFile = new File(directory, timeStamp + ".jpg");
            Uri outputUri = Uri.fromFile(avatarFile);

            openCropActivity(inputUri, outputUri);
        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = UCrop.getOutput(data);

            File file = new File(uri.getPath());
            try {
                InputStream inputStream = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageButton avatarBtn = findViewById(R.id.PetAvatar);
                avatarBtn.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        status = String.valueOf(parent.getItemAtPosition(pos)).toLowerCase();

        if (status.equals("deceased")) {
            dateOfDeceaseEdt.setVisibility(View.VISIBLE);
        } else {
            dateOfDeceaseEdt.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .start(this);
    }
}
