package nl.peternijssen.mypetsage.dbs;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import android.util.Log;

import java.util.Date;

@Entity(tableName = "pets")
public class Pet {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "avatar")
    private String avatar;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "date_of_birth")
    private Date dateOfBirth;

    @ColumnInfo(name = "status", defaultValue = "alive")
    private String status;

    @TypeConverters({Converters.class})
    @ColumnInfo(name = "date_of_decease")
    private Date dateOfDecease;

    @Ignore
    public Pet() {

    }

    public Pet(String name, String avatar, Date dateOfBirth, String status, Date dateOfDecease) {
        this.name = name;
        this.avatar = avatar;
        this.dateOfBirth = dateOfBirth;
        this.status = status;
        if (status.equals("alive")) {
            this.dateOfDecease = null;
        } else {
            this.dateOfDecease = dateOfDecease;
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getStatus() {
        return status;
    }

    public Date getDateOfDecease() {
        return dateOfDecease;
    }

    public boolean isAlive() {
        return getStatus().equals("alive");
    }

    public boolean isDeceased() {
        return getStatus().equals("deceased");
    }

    public void setId(int id) {
        this.id = id;
    }
}
