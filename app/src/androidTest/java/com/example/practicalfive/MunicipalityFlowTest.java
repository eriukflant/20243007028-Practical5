package com.example.practicalfive;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class MunicipalityFlowTest {
    private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
    private final AtomicReference<Intent> openedIntent = new AtomicReference<>();
    private Instrumentation.ActivityMonitor monitor;

    @Before
    public void monitorNavigation() {
        monitor = new Instrumentation.ActivityMonitor() {
            @Override
            public Instrumentation.ActivityResult onStartActivity(Intent intent) {
                openedIntent.set(intent);
                return new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null);
            }
        };
    }

    @After
    public void removeMonitor() {
        instrumentation.removeMonitor(monitor);
    }

    @Test
    public void goPassesTheNormalizedNameInAnExplicitIntent() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            instrumentation.addMonitor(monitor);
            scenario.onActivity(activity -> {
                EditText input = activity.findViewById(R.id.municipalityInput);
                input.setText("  bEiJiNg  ");
                input.onEditorAction(EditorInfo.IME_ACTION_GO);
            });
            Intent intent = openedIntent.get();
            assertNotNull(intent);
            assertEquals(Municipality.class.getName(), intent.getComponent().getClassName());
            assertEquals("Beijing", intent.getStringExtra(Municipality.EXTRA_MUNICIPALITY));
        }
    }

    @Test
    public void invalidInputShowsAnErrorWithoutNavigation() {
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            instrumentation.addMonitor(monitor);
            scenario.onActivity(activity -> {
                EditText input = activity.findViewById(R.id.municipalityInput);
                for (String value : new String[]{"", "   ", "Guangzhou"}) {
                    input.setText(value);
                    input.onEditorAction(EditorInfo.IME_ACTION_GO);
                    assertNotNull(input.getError());
                    assertNull(openedIntent.get());
                }
            });
        }
    }

    @Test
    public void eachCityShowsItsResourcesAndOpensItsOfficialWebsite() {
        String[] names = {"Beijing", "Shanghai", "Chongqing", "Tianjin"};
        int[] colors = {R.color.municipality_beijing, R.color.municipality_shanghai,
                R.color.municipality_chongqing, R.color.municipality_tianjin};
        String[] sites = {"https://english.beijing.gov.cn/", "https://english.shanghai.gov.cn/",
                "https://english.cq.gov.cn/", "https://en.tj.gov.cn/"};
        Context context = instrumentation.getTargetContext();

        for (int i = 0; i < names.length; i++) {
            final int index = i;
            Intent launch = new Intent(context, Municipality.class);
            launch.putExtra(Municipality.EXTRA_MUNICIPALITY, names[i]);
            try (ActivityScenario<Municipality> scenario = ActivityScenario.launch(launch)) {
                instrumentation.addMonitor(monitor);
                openedIntent.set(null);
                scenario.onActivity(activity -> {
                    TextView title = activity.findViewById(R.id.municipalityName);
                    TextView info = activity.findViewById(R.id.municipalityInfo);
                    ImageView photo = activity.findViewById(R.id.municipalityImage);
                    Button button = activity.findViewById(R.id.visitSiteButton);
                    int accent = activity.getColor(colors[index]);

                    assertEquals(names[index], title.getText().toString());
                    assertEquals(accent, ((ColorDrawable) title.getBackground()).getColor());
                    assertEquals(accent, info.getCurrentTextColor());
                    assertEquals(accent, button.getBackgroundTintList().getDefaultColor());
                    String summary = info.getText().toString();
                    assertTrue(summary.startsWith(names[index] + " "));
                    assertTrue(summary.split("\\s+").length >= 80);
                    assertTrue(summary.split("\\s+").length <= 120);
                    assertNotNull(photo.getDrawable());
                    assertEquals(names[index] + " city view", photo.getContentDescription().toString());
                    button.performClick();
                });
                Intent website = openedIntent.get();
                assertNotNull(website);
                assertEquals(Intent.ACTION_VIEW, website.getAction());
                assertNull(website.getComponent());
                assertEquals(sites[index], website.getDataString());
                instrumentation.removeMonitor(monitor);
            }
        }
    }
}
