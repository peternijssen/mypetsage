package nl.peternijssen.mypetsage.dbs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@TypeConverters({Converters.class})
@Database(entities = {Pet.class}, version = 4)
public abstract class PetDatabase extends RoomDatabase {
    private static PetDatabase instance;

    public abstract PetDao petDao();

    public static synchronized PetDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), PetDatabase.class, "pets.db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                    .allowMainThreadQueries()
                    .build();
        }

        return instance;
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pets RENAME TO petstemp");
            database.execSQL("CREATE TABLE pets (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, avatar TEXT, dateofbirth TEXT)");
            database.execSQL("INSERT INTO pets (_id, name, avatar, dateofbirth) SELECT _id, name, avatar, dateofbirth FROM petstemp");
            database.execSQL("DROP TABLE petstemp");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pets RENAME COLUMN _id TO id");
            database.execSQL("ALTER TABLE pets RENAME COLUMN dateofbirth TO date_of_birth");
            database.execSQL("ALTER TABLE pets ADD COLUMN date_of_decease TEXT");
            database.execSQL("ALTER TABLE pets ADD COLUMN status TEXT DEFAULT 'alive'");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pets RENAME TO petstemp");
            database.execSQL("CREATE TABLE pets (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, avatar TEXT, date_of_birth INTEGER, date_of_decease INTEGER DEFAULT NULL, status TEXT DEFAULT 'alive')");

            Cursor cursor = database.query("SELECT * FROM petstemp");

            while (cursor.moveToNext()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("id", cursor.getInt(cursor.getColumnIndex("id")));
                contentValues.put("name", cursor.getString(cursor.getColumnIndex("name")));
                contentValues.put("avatar", cursor.getString(cursor.getColumnIndex("avatar")));
                contentValues.put("status", cursor.getString(cursor.getColumnIndex("status")));
                String dob = cursor.getString(cursor.getColumnIndex("date_of_birth"));
                String dod = cursor.getString(cursor.getColumnIndex("date_of_decease"));

                Date dateOfBirth = new Date();
                Date dateOfDecease = null;

                DateFormat df = new SimpleDateFormat("d-M-y", Locale.getDefault());
                try {
                    if (dob != null) {
                        dateOfBirth = df.parse(dob);
                    }
                }  catch (ParseException e) {
                    // Do nothing
                }

                try {
                    if (dod != null) {
                        dateOfDecease = df.parse(dod);
                    }
                }  catch (ParseException e) {
                    // Do nothing
                }

                if (dateOfBirth != null) {
                    contentValues.put("date_of_birth", dateOfBirth.getTime());
                }

                if (dateOfDecease != null) {
                    contentValues.put("date_of_decease", dateOfDecease.getTime());
                }

                database.insert("pets", SQLiteDatabase.CONFLICT_REPLACE, contentValues);
            }
            database.execSQL("DROP TABLE petstemp");
        }
    };
}