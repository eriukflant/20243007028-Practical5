package com.example.practicalfive;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Municipality extends AppCompatActivity {
    public static final String EXTRA_MUNICIPALITY = "com.example.practicalfive.MUNICIPALITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_municipality);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.municipalityRoot), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        String name = new TextProcessor().processMunicipality(
                getIntent().getStringExtra(EXTRA_MUNICIPALITY));
        if (name == null) {
            Toast.makeText(this, R.string.missing_municipality, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showMunicipality(name);
    }

    private void showMunicipality(String name) {
        int index;
        int image;
        int color;
        int website;
        switch (name) {
            case "Beijing":
                index = 0;
                image = R.drawable.beijing;
                color = R.color.municipality_beijing;
                website = R.string.website_beijing;
                break;
            case "Shanghai":
                index = 1;
                image = R.drawable.shanghai;
                color = R.color.municipality_shanghai;
                website = R.string.website_shanghai;
                break;
            case "Chongqing":
                index = 2;
                image = R.drawable.chongqing;
                color = R.color.municipality_chongqing;
                website = R.string.website_chongqing;
                break;
            case "Tianjin":
                index = 3;
                image = R.drawable.tianjin;
                color = R.color.municipality_tianjin;
                website = R.string.website_tianjin;
                break;
            default:
                finish();
                return;
        }

        int accent = ContextCompat.getColor(this, color);
        int textColor = ContextCompat.getColor(this, name.equals("Chongqing")
                ? R.color.color_text_dark : R.color.color_text_light);
        TextView title = findViewById(R.id.municipalityName);
        title.setText(name);
        title.setBackgroundColor(accent);
        title.setTextColor(textColor);

        ImageView photo = findViewById(R.id.municipalityImage);
        photo.setImageResource(image);
        photo.setContentDescription(getString(R.string.city_photo, name));

        TextView information = findViewById(R.id.municipalityInfo);
        information.setText(getResources().getStringArray(R.array.municipalities_array)[index]);
        information.setTextColor(accent);
        if (name.equals("Chongqing")) {
            information.setBackgroundColor(ContextCompat.getColor(this, R.color.color_text_dark));
            int padding = (int) (12 * getResources().getDisplayMetrics().density);
            information.setPadding(padding, padding, padding, padding);
        }

        Button visitSite = findViewById(R.id.visitSiteButton);
        visitSite.setBackgroundTintList(ColorStateList.valueOf(accent));
        visitSite.setTextColor(textColor);
        visitSite.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(website)));
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException exception) {
                Toast.makeText(this, R.string.no_browser, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
