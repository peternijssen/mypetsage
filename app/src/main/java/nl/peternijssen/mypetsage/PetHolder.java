package nl.peternijssen.mypetsage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PetHolder extends RecyclerView.ViewHolder {

    TextView name, age, options;
    ImageView avatar;

    PetHolder(View v) {
        super(v);

        name = v.findViewById(R.id.PetName);
        age = v.findViewById(R.id.PetAge);
        options = v.findViewById(R.id.OptionButton);
        avatar = v.findViewById(R.id.PetAvatar);
    }
}
