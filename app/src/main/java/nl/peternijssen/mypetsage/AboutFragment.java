package nl.peternijssen.mypetsage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class AboutFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        TextView versionName = view.findViewById(R.id.appVersion);
        versionName.setText(BuildConfig.VERSION_NAME);

        TextView buildNumber = view.findViewById(R.id.appBuildNumber);
        buildNumber.setText(String.valueOf(BuildConfig.VERSION_CODE));

        TextView supportAddress = view.findViewById(R.id.appSupportAddress);
        supportAddress.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question regarding My Pet's Age");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, "mypetsage@peternijssen.nl");
            startActivity(emailIntent);
        });

        TextView privacyPolicy = view.findViewById(R.id.appPrivacyPolicy);
        privacyPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.peternijssen.nl/privacy_policy_app.html"));
            startActivity(browserIntent);
        });

        TextView translations = view.findViewById(R.id.appTranslations);
        translations.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.transifex.com/peter-nijssen/my-pets-age/"));
            startActivity(browserIntent);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}