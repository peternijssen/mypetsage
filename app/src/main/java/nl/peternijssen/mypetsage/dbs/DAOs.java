package nl.peternijssen.mypetsage.dbs;

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
        List<Entities.Pet> getAll();

        @Query("SELECT * FROM pets WHERE _id IN (:petIds)")
        List<Entities.Pet> loadAllByIds(int[] petIds);

        @Insert
        void insert(Entities.Pet pet);

        @Update
        void update(Entities.Pet pet);

        @Delete
        void delete(Entities.Pet pet);
    }
}
