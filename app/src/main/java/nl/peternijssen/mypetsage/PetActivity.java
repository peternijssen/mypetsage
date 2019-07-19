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

import nl.peternijssen.mypetsage.database.PetsDataSource;

public class PetActivity extends AppCompatActivity {

    private static final int IMAGE = 100;

    private PetsDataSource datasource;

    private File avatarFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        Calendar calendar = Calendar.getInstance();

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        final EditText petDateOfBirth = findViewById(R.id.PetDateOfBirth);
        petDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(PetActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int years, int months, int days) {

                        String dates = days + "-" + (months + 1) + "-" + years;

                        petDateOfBirth.setText(dates);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        datasource = new PetsDataSource(this);
        datasource.open();
    }

    public void onClick(View view) {
        EditText petName = findViewById(R.id.PetName);
        EditText petDateOfBirth = findViewById(R.id.PetDateOfBirth);

        if (avatarFile == null) {
            datasource.createPet(petName.getText().toString(), "none", petDateOfBirth.getText().toString());
        } else {
            datasource.createPet(petName.getText().toString(), avatarFile.getAbsolutePath(), petDateOfBirth.getText().toString());
        }

        Toast.makeText(getApplicationContext(), String.format(getApplicationContext().getString(R.string.action_pet_created), petName.getText().toString()), Toast.LENGTH_SHORT).show();

        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
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
