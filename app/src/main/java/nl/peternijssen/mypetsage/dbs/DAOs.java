package nl.peternijssen.mypetsage.dbs;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

public class DAOs {
    @Dao
    public interface PetDao {

        @Query("SELECT * FROM pets")
        LiveData<List<Entities.Pet>> getAll();

        @Insert
        void insert(Entities.Pet pet);

        @Update
        void update(Entities.Pet pet);

        @Delete
        void delete(Entities.Pet pet);
    }
}
