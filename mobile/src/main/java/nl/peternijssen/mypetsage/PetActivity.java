package nl.peternijssen.mypetsage;

import android.app.Activity;
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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.soundcloud.android.crop.Crop;

import nl.peternijssen.mypetsage.database.PetsDataSource;

public class PetActivity extends Activity {

    private static final int IMAGE = 1;

    private PetsDataSource datasource;

    private File avatarFile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        datasource = new PetsDataSource(this);
        datasource.open();
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

    public void onClick(View view) {
        EditText petName = (EditText)findViewById(R.id.PetName);
        DatePicker petDateOfBirth = (DatePicker)findViewById(R.id.PetDateOfBirth);

        CharSequence date = "" + petDateOfBirth.getDayOfMonth() + "-" + "" + (petDateOfBirth.getMonth() + 1) + "-" + "" + petDateOfBirth.getYear();

        if(avatarFile == null) {
            datasource.createPet(petName.getText().toString(), "none", date.toString());
        } else {
            datasource.createPet(petName.getText().toString(), avatarFile.getAbsolutePath(), date.toString());
        }

        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    public void onClickAvatar(View view) {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            cropImage(selectedImage);
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK && null != data) {
            Bitmap avatarBitmap = BitmapFactory.decodeFile(avatarFile.getAbsolutePath());
            ImageButton avatarBtn = (ImageButton) findViewById(R.id.PetAvatar);
            avatarBtn.setImageBitmap(avatarBitmap);
        }
    }

    public void cropImage(Uri source) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("avatars", Context.MODE_PRIVATE);
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        avatarFile = new File(directory, timeStamp + ".jpg");

        Uri outputUri = Uri.fromFile(avatarFile);
        new Crop(source).output(outputUri).asSquare().start(this);
    }
}
