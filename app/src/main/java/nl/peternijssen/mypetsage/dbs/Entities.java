package nl.peternijssen.mypetsage.dbs;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class Entities {
    @Entity(tableName = "pets")
    public static class Pet {

        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        private int id;

        @ColumnInfo(name = "name")
        private String name;

        @ColumnInfo(name = "avatar")
        private String avatar;

        @ColumnInfo(name = "dateofbirth")
        private String dateOfBirth;

        @Ignore
        public Pet() {

        }

        public Pet(String name, String avatar, String dateOfBirth) {
            this.name = name;
            this.avatar = avatar;
            this.dateOfBirth = dateOfBirth;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
