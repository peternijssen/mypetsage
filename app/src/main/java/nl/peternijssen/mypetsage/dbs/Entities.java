package nl.peternijssen.mypetsage.dbs;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

public class Entities {
    @Entity(tableName = "pets")
    public static class Pet {

        @PrimaryKey
        @ColumnInfo(name = "_id")
        private long id;

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

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}