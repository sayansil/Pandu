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
    TextView food;
    TextView pass;

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
        food = findViewById(R.id.pandalHeaderFood);
        pass = findViewById(R.id.pandalHeaderPass);

        name.setText(getIntent().getStringExtra("name"));
        theme.setText(getIntent().getStringExtra("theme"));
        location.setText(getIntent().getStringExtra("location"));
        food.setText(getIntent().getStringExtra("food"));
        pass.setText(getIntent().getStringExtra("pass"));
        BaseUtility.loadImageFromURL(getApplicationContext(), getIntent().getStringExtra("picture"), picture);
    }

}
