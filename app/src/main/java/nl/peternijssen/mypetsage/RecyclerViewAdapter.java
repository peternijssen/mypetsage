package nl.peternijssen.mypetsage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.List;

import nl.peternijssen.mypetsage.database.PetsDataSource;
import nl.peternijssen.mypetsage.model.Pet;

public class RecyclerViewAdapter extends RecyclerView.Adapter<PetHolder> {

    private List<Pet> petList;
    private Context context;

    RecyclerViewAdapter(List<Pet> list, Context context) {
        this.petList = list;
        this.context = context;
    }

    public void setPets(List<Pet> pets) {
        this.petList = pets;
    }

    @Override
    public PetHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        return new PetHolder(v);
    }

    @Override
    public void onBindViewHolder(final PetHolder holder, final int position) {
        final Pet p = petList.get(position);

        holder.name.setText(p.getName());
        holder.age.setText(calculateAge(p.getDateOfBirth()));

        File imgFile = new File(p.getAvatar());
        if (imgFile.exists()) {
            Bitmap avatarBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.avatar.setImageBitmap(avatarBitmap);
        }

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(context, holder.options);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.deletePet:
                                PetsDataSource datasource = new PetsDataSource(context);
                                datasource.open();
                                datasource.deletePet(datasource.getAllPets().get(position));
                                setPets(datasource.getAllPets());
                                datasource.close();

                                Toast.makeText(context, String.format(context.getString(R.string.action_pet_deleted), p.getName()), Toast.LENGTH_SHORT).show();

                                notifyDataSetChanged();
                                return true;
                            default:
                                return false;
                        }
                    }
                });

                popup.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private String calculateAge(String dateOfBirth) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime start = formatter.parseDateTime(dateOfBirth);
        DateTime end = DateTime.now();
        Period period = new Period(start, end);

        Integer ageDisplay = Integer.parseInt(sharedPrefs.getString("ageDisplay", "0"));

        String text;

        switch (ageDisplay) {
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