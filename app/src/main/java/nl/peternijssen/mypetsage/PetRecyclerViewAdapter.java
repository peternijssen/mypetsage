package nl.peternijssen.mypetsage;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.peternijssen.mypetsage.dbs.Entities;

public class PetRecyclerViewAdapter extends ListAdapter<Entities.Pet, PetRecyclerViewAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Entities.Pet> DIFF_CALLBACK = new DiffUtil.ItemCallback<Entities.Pet>() {
        @Override
        public boolean areItemsTheSame(Entities.Pet oldItem, Entities.Pet newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(Entities.Pet oldItem, Entities.Pet newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getAvatar().equals(newItem.getAvatar()) &&
                    oldItem.getDateOfBirth().equals(newItem.getDateOfBirth());
        }
    };
    private Context context;
    private OnItemClickListener listener;

    PetRecyclerViewAdapter(Context context) {
        super(DIFF_CALLBACK);
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_layout, parent, false);

        return new ViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Entities.Pet pet = getPetAt(holder.getAdapterPosition());

        DateFormat df = new SimpleDateFormat("d-M-y", Locale.getDefault());
        String dateOfBirth = pet.getDateOfBirth();

        try {
            Date dob = df.parse(pet.getDateOfBirth());
            df = new SimpleDateFormat("d MMM y", Locale.getDefault());
            dateOfBirth = df.format(dob);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.name.setText(pet.getName());
        holder.dateOfBirth.setText(dateOfBirth);
        holder.age.setText(calculateAge(pet.getDateOfBirth()));

        File imgFile = new File(pet.getAvatar());
        if (imgFile.exists()) {
            Bitmap avatarBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.avatar.setImageBitmap(avatarBitmap);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    private Entities.Pet getPetAt(int position) {
        return getItem(position);
    }

    private String calculateAge(String dateOfBirth) {
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);

            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
            DateTime start = formatter.parseDateTime(dateOfBirth);
            DateTime end = DateTime.now();
            Period period = new Period(start, end);

            int ageDisplay = Integer.parseInt(sharedPrefs.getString("ageDisplay", "0"));

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
                    text = String.format(text, period.getYears(), period.getMonths(), ((period.getWeeks() * 7) + period.getDays()));
                    break;
            }
            return text;
        } catch (IllegalArgumentException exception) {
            return dateOfBirth;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Entities.Pet pet, TextView options);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, age, dateOfBirth, options;
        ImageView avatar;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.PetName);
            age = itemView.findViewById(R.id.PetAge);
            dateOfBirth = itemView.findViewById(R.id.PetDateOfBirth);
            avatar = itemView.findViewById(R.id.PetAvatar);
            options = itemView.findViewById(R.id.OptionButton);

            options.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position), options);
                }
            });
        }
    }
}