package nl.peternijssen.mypetsage;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Question regarding My Pet's Age");
            emailIntent.putExtra(Intent.EXTRA_EMAIL, Uri.parse("mailto:mypetsage@peternijssen.nl"));

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });

        TextView privacyPolicy = view.findViewById(R.id.appPrivacyPolicy);
        privacyPolicy.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.peternijssen.nl/privacy_policy_app.html"));
            startActivity(Intent.createChooser(browserIntent, "Open Privacy Policy"));
        });

        TextView translations = view.findViewById(R.id.appTranslations);
        translations.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.transifex.com/peter-nijssen/my-pets-age/"));
            startActivity(Intent.createChooser(browserIntent, "Open Translations"));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_about, container, false);
    }
}