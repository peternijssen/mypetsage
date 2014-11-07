package nl.peternijssen.mypetsage.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import nl.peternijssen.mypetsage.model.Pet;

public class PetsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private PetsDatabaseHelper dbHelper;
    private String[] allColumns = { PetsDatabaseHelper.COLUMN_ID, PetsDatabaseHelper.COLUMN_NAME, PetsDatabaseHelper.COLUMN_AVATAR, PetsDatabaseHelper.COLUMN_DATEOFBIRTH };

    public PetsDataSource(Context context) {
        dbHelper = new PetsDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Pet createPet(String name, String avatar, String dateofbirth) {
        ContentValues values = new ContentValues();
        values.put(PetsDatabaseHelper.COLUMN_NAME, name);
        values.put(PetsDatabaseHelper.COLUMN_AVATAR, avatar);
        values.put(PetsDatabaseHelper.COLUMN_DATEOFBIRTH, dateofbirth);
        long insertId = database.insert(PetsDatabaseHelper.TABLE_PETS, null, values);
        Cursor cursor = database.query(PetsDatabaseHelper.TABLE_PETS, allColumns, PetsDatabaseHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        Pet newPet = cursorToPet(cursor);
        cursor.close();
        return newPet;
    }

    public void deletePet(Pet pet) {
        long id = pet.getId();
        database.delete(PetsDatabaseHelper.TABLE_PETS, PetsDatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<Pet> getAllPets() {
        List<Pet> pets = new ArrayList<Pet>();

        Cursor cursor = database.query(PetsDatabaseHelper.TABLE_PETS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Pet pet = cursorToPet(cursor);
            pets.add(pet);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return pets;
    }

    private Pet cursorToPet(Cursor cursor) {
        Pet pet = new Pet();
        pet.setId(cursor.getLong(0));
        pet.setName(cursor.getString(1));
        pet.setAvatar(cursor.getString(2));
        pet.setDateOfBirth(cursor.getString(3));
        return pet;
    }
}
