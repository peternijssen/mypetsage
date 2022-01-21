package nl.peternijssen.mypetsage;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PetActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "nl.peternijssen.mypetsage.EXTRA_ID";
    public static final String EXTRA_NAME = "nl.peternijssen.mypetsage.EXTRA_NAME";
    public static final String EXTRA_AVATAR = "nl.peternijssen.mypetsage.EXTRA_AVATAR";
    public static final String EXTRA_DATE_OF_BIRTH = "nl.peternijssen.mypetsage.EXTRA_DATE_OF_BIRTH";
    private static final int IMAGE = 100;
    private File avatarFile = null;
    private EditText nameEdt, dateOfBirthEdt;
    private Button saveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        nameEdt = findViewById(R.id.PetName);
        dateOfBirthEdt = findViewById(R.id.PetDateOfBirth);
        saveBtn = findViewById(R.id.SaveButton);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_ID)) {
            nameEdt.setText(intent.getStringExtra(EXTRA_NAME));
            dateOfBirthEdt.setText(intent.getStringExtra(EXTRA_DATE_OF_BIRTH));
            saveBtn.setText(R.string.action_edit_pet);
            setTitle(R.string.action_edit_pet);

            // Set avatar on edit
            avatarFile = new File(intent.getStringExtra(EXTRA_AVATAR));
            try {
                InputStream inputStream = new FileInputStream(avatarFile);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageButton avatarBtn = findViewById(R.id.PetAvatar);
                avatarBtn.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        dateOfBirthEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int years, int months, int days) {
                        String dates = days + "-" + (months + 1) + "-" + years;
                        dateOfBirthEdt.setText(dates);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEdt.getText().toString();
                String dateOfBirth = dateOfBirthEdt.getText().toString();
                if (name.isEmpty() || dateOfBirth.isEmpty()) {
                    Toast.makeText(PetActivity.this, R.string.pet_form_failed, Toast.LENGTH_SHORT).show();
                    return;
                }
                savePet(name, dateOfBirth);
            }
        });
    }

    private void savePet(String name, String dateOfBirth) {
        Intent data = new Intent();

        data.putExtra(EXTRA_NAME, name);
        if (avatarFile == null) {
            data.putExtra(EXTRA_AVATAR, "none");
        } else {
            data.putExtra(EXTRA_AVATAR, avatarFile.getAbsolutePath());
        }
        data.putExtra(EXTRA_DATE_OF_BIRTH, dateOfBirth);
        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra(EXTRA_ID, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    public void onClickAvatar(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(intent, IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE && resultCode == RESULT_OK && null != data) {
            Uri inputUri = data.getData();

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("avatars", Context.MODE_PRIVATE);
            String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
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
        }
    }

    private void openCropActivity(Uri sourceUri, Uri destinationUri) {
        UCrop.of(sourceUri, destinationUri)
                .withAspectRatio(1, 1)
                .start(this);
    }
}
