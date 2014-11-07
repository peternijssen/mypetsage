package nl.peternijssen.mypetsage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.*;
import org.joda.time.format.*;

import java.io.File;
import java.util.List;

import nl.peternijssen.mypetsage.model.Pet;

/**
 * Created by peter on 8/28/13.
 */
public class ListAdapter extends BaseAdapter {

    private List<Pet> petList;
    private Context context;

    public ListAdapter (List<Pet> t, Context co) {
        petList = t;
        context = co;
    }

    public void setPets(List<Pet> pets) {
        this.petList = pets;
    }

    public int getCount() {
        return petList.size();
    }

    public Object getItem(int position) {
        return petList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null)
        {
            LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_element, null);
        }

        TextView nameView = (TextView)v.findViewById(R.id.PetName);
        TextView ageView = (TextView)v.findViewById(R.id.PetAge);

        Pet p = petList.get(position);

        nameView.setText(p.getName());
        ageView.setText(calculateAge(p.getDateOfBirth()));
        File imgFile = new File(p.getAvatar());
        if(imgFile.exists()){
            Bitmap avatarBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            ImageView avatarView = (ImageView)v.findViewById(R.id.PetAvatar);
            avatarView.setImageBitmap(avatarBitmap);
        }

        return v;
    }

    private String calculateAge(String dateOfBirth) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime start = formatter.parseDateTime(dateOfBirth);
        DateTime end = DateTime.now();
        Period period = new Period(start, end);

        Integer ageDisplay = Integer.parseInt(sharedPrefs.getString("ageDisplay", "0"));

        String text;

        switch(ageDisplay) {
            case 1:
                Days days = Days.daysBetween(start, end);
                text = context.getResources().getString(R.string.pet_age_days);
                text = String.format(text, days.getDays());
                break;
            case 2:
                Weeks weeks = Weeks.weeksBetween(start, end);
                text = context.getResources().getString(R.string.pet_age_weeks);
                text = String.format(text, weeks.getWeeks());
                break;
            case 3:
                Months months = Months.monthsBetween(start, end);
                text = context.getResources().getString(R.string.pet_age_months);
                text = String.format(text, months.getMonths());
                break;
            case 4:
                Years years = Years.yearsBetween(start, end);
                text = context.getResources().getString(R.string.pet_age_years);
                text = String.format(text, years.getYears());
                break;
            default:
                text = context.getResources().getString(R.string.pet_age_regular);
                text = String.format(text, period.getYears(), period.getMonths(), period.getWeeks(), period.getDays());
                break;
        }

        return text;
    }
}
