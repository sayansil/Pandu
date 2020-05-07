package team.OG.pandu_organiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.crashlytics.internal.common.CrashlyticsCore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.joaquimley.faboptions.FabOptions;

import team.OG.pandu_organiser.Units.Pandal;


public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String RUN_TAG = "LIVE TESTING";

    ImageView picture;
    TextView name;
    TextView theme;
    TextView location;
    FabOptions bottomMenu;

    Pandal currentPandal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        picture = findViewById(R.id.pandalHeaderImage);
        name = findViewById(R.id.pandalHeaderName);
        theme = findViewById(R.id.pandalHeaderTheme);
        location = findViewById(R.id.pandalHeaderLocation);

        bottomMenu = findViewById(R.id.optionsHome);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        currentPandal = new Pandal();

        bottomMenu.setOnClickListener((View view) -> {
            switch(view.getId()) {
                case R.id.faboptions_analyze:
                    BaseUtility.writeLog(RUN_TAG, "hello");
                    Intent intent = new Intent(HomeActivity.this, AnalyzeActivity.class);
                    intent.putExtra("uid", uid);
                    startActivity(intent);
                    break;
                case R.id.faboptions_edit:
                    update_profile();
                    break;
                case R.id.faboptions_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent logout_intent = new Intent(HomeActivity.this, SplashActivity.class);
                    startActivity(logout_intent);
                    finish();
                    break;
                case R.id.faboptions_info:
                    Intent info_intent = new Intent(HomeActivity.this, InfoActivity.class);
                    startActivity(info_intent);
                    break;
            }
        });

        refresh();

        if(checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

    }

    private void refresh() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").document(uid)
                .get()
                .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                    if (task.isSuccessful()) {
                        BaseUtility.writeLog(TAG, "Pandel Info Access Successful");
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            name.setText(document.getString("name"));
                            theme.setText(document.getString("theme"));
                            location.setText(document.getString("location"));
                            BaseUtility.loadImageFromURL(this, document.getString("picture"), picture);

                            currentPandal = new Pandal(
                                    document.getString("name"),
                                    document.getLong("publicCount").intValue(),
                                    document.getString("location"),
                                    document.getString("theme"),
                                    document.getString("picture")
                            );

                        } else {
                            BaseUtility.writeLog(TAG, "Pandel List Not found");

                            name.setText("[ Empty ]");
                            theme.setText("[ Empty ]");
                            location.setText("[ Empty ]");
                            BaseUtility.loadImageFromURL(this, "https://semantic-ui.com/images/wireframe/image.png", picture);

                            currentPandal = new Pandal();

                            db.collection("pandels").document(uid)
                                    .set(currentPandal)
                                    .addOnSuccessListener((Void aVoid) -> {
                                        Log.d(TAG, "Document added.");
//                                        progressbar.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener((@NonNull Exception e) -> {
                                        Log.w(TAG, "Error adding document", e);
//                                        progressbar.setVisibility(View.GONE);
                                    });
                        }

                    } else {
                        Log.e(TAG, "Error fetching document");
                    }
                });
    }

    private void update_profile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_update, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final TextInputEditText inputName = promptsView.findViewById(R.id.textName);
        final TextInputEditText inputTheme = promptsView.findViewById(R.id.textTheme);
        final TextInputEditText inputLocation = promptsView.findViewById(R.id.textLocation);
        final TextInputEditText inputPicture = promptsView.findViewById(R.id.textPicture);

        inputName.setText(currentPandal.getName());
        inputTheme.setText(currentPandal.getTheme());
        inputLocation.setText(currentPandal.getLocation());
        inputPicture.setText(currentPandal.getPicture());

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (DialogInterface dialog, int d_id) -> {
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("pandels").document(uid)
                                    .set(new Pandal(
                                            inputName.getText().toString(),
                                            currentPandal.getPublicCount(),
                                            inputLocation.getText().toString(),
                                            inputTheme.getText().toString(),
                                            inputPicture.getText().toString()
                                    ))
                                    .addOnSuccessListener((Void aVoid) -> {
                                        BaseUtility.writeLog(TAG, "Pandel Update Successful");
                                        Log.d(TAG, "Document added.");
//                                        progressbar.setVisibility(View.GONE);

                                        refresh();
                                    })
                                    .addOnFailureListener((@NonNull Exception e) -> {
                                        BaseUtility.writeLog(TAG, "Pandel Update Unsuccessful");
                                        Log.w(TAG, "Error adding document", e);
//                                        progressbar.setVisibility(View.GONE);
                                    });
                        })
                .setNegativeButton("Cancel",
                        (DialogInterface dialog,int d_id) -> {
                            dialog.cancel();
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }
}
