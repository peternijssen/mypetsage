package nl.peternijssen.mypetsage.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.Weeks;
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nl.peternijssen.mypetsage.R;
import nl.peternijssen.mypetsage.database.PetsDataSource;
import nl.peternijssen.mypetsage.model.Pet;

public class PetWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class PetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mAppWidgetId;

    private PetsDataSource datasource;

    private List<Pet> petList = new ArrayList<Pet>();

    public PetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public int getCount() {
        return petList.size();
    }

    @Override
    public long getItemId(int position) {
        return petList.get(position).getId();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.list_element);

        Pet p = petList.get(position);

        remoteView.setTextViewText(R.id.PetName, p.getName());
        remoteView.setTextViewText(R.id.PetAge, calculateAge(p.getDateOfBirth()));

        File imgFile = new File(p.getAvatar());
        if(imgFile.exists()){
            Bitmap avatarBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            remoteView.setImageViewBitmap(R.id.PetAvatar, avatarBitmap);
        }
        return remoteView;
    }

    public void onCreate() {
        datasource = new PetsDataSource(mContext);
        datasource.open();

        petList = datasource.getAllPets();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        datasource = new PetsDataSource(mContext);
        datasource.open();

        petList = datasource.getAllPets();
    }

    @Override
    public void onDestroy() {
    }

    private String calculateAge(String dateOfBirth) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd-MM-yyyy");
        DateTime start = formatter.parseDateTime(dateOfBirth);
        DateTime end = DateTime.now();
        Period period = new Period(start, end);

        Integer ageDisplay = Integer.parseInt(sharedPrefs.getString("ageDisplay", "0"));

        String text;

        switch(ageDisplay) {
            case 1:
                Days days = Days.daysBetween(start, end);
                text = mContext.getResources().getString(R.string.pet_age_days);
                text = String.format(text, days.getDays());
                break;
            case 2:
                Weeks weeks = Weeks.weeksBetween(start, end);
                text = mContext.getResources().getString(R.string.pet_age_weeks);
                text = String.format(text, weeks.getWeeks());
                break;
            case 3:
                Months months = Months.monthsBetween(start, end);
                text = mContext.getResources().getString(R.string.pet_age_months);
                text = String.format(text, months.getMonths());
                break;
            case 4:
                Years years = Years.yearsBetween(start, end);
                text = mContext.getResources().getString(R.string.pet_age_years);
                text = String.format(text, years.getYears());
                break;
            default:
                text = mContext.getResources().getString(R.string.pet_age_regular);
                text = String.format(text, period.getYears(), period.getMonths(), period.getWeeks(), period.getDays());
                break;
        }

        return text;
    }
}
