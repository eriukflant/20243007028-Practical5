package com.example.practicalfive;

import android.os.Bundle;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets keyboard = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,
                    Math.max(systemBars.bottom, keyboard.bottom));
            return insets;
        });

        EditText input = findViewById(R.id.municipalityInput);
        input.setOnEditorActionListener((view, actionId, event) -> {
            boolean enter = event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE || enter) {
                if (enter && event.getAction() != KeyEvent.ACTION_DOWN) {
                    return true;
                }
                String name = new TextProcessor().processMunicipality(input.getText().toString());
                if (name == null) {
                    input.setError(getString(R.string.input_error));
                    return true;
                }
                input.setError(null);
                Intent intent = new Intent(this, Municipality.class);
                intent.putExtra(Municipality.EXTRA_MUNICIPALITY, name);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }
}
