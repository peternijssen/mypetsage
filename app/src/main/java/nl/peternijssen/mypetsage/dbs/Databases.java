package nl.peternijssen.mypetsage.dbs;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Databases {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE pets RENAME TO petstemp");
            database.execSQL("CREATE TABLE pets (_id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, name TEXT, avatar TEXT, dateofbirth TEXT)");
            database.execSQL("INSERT INTO pets (_id, name, avatar, dateofbirth) SELECT _id, name, avatar, dateofbirth FROM petstemp");
            database.execSQL("DROP TABLE petstemp");
        }
    };

    @Database(entities = {Entities.Pet.class}, version = 2)
    public abstract static class PetDatabase extends RoomDatabase {
        private static PetDatabase INSTANCE;

        public abstract DAOs.PetDao petDao();

        public static PetDatabase getPetDatabase(Context context){
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        PetDatabase.class, "pets.db").addMigrations(MIGRATION_1_2).allowMainThreadQueries().build();
            }
            return INSTANCE;
        }

        public static void destroyInstance(){
            INSTANCE = null;
        }
    }

}