package team.OG.pandu;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PandalHomeActivity extends AppCompatActivity {


    ImageView picture;
    TextView name;
    TextView theme;
    TextView location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pandal);

        picture = findViewById(R.id.pandalHeaderImage);
        name = findViewById(R.id.pandalHeaderName);
        theme = findViewById(R.id.pandalHeaderTheme);
        location = findViewById(R.id.pandalHeaderLocation);

        name.setText(getIntent().getStringExtra("name"));
        theme.setText(getIntent().getStringExtra("theme"));
        location.setText(getIntent().getStringExtra("location"));
        BaseUtility.loadImageFromURL(getApplicationContext(), getIntent().getStringExtra("picture"), picture);
    }

}
