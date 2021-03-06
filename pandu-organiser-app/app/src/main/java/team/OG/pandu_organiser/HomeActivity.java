package team.OG.pandu_organiser;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.zxing.BarcodeFormat;
import com.joaquimley.faboptions.FabOptions;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import team.OG.pandu_organiser.Managers.PandalManager;
import team.OG.pandu_organiser.Units.Pandal;


public class HomeActivity extends AppCompatActivity implements PandalManager {

    private static final String TAG = "HomeActivity";
    private static final String RUN_TAG = "LIVE TESTING";

    ImageView picture;
    TextView name;
    TextView theme;
    TextView location;
    FabOptions bottomMenu;
    TextView food;
    TextView pass;

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
        food = findViewById(R.id.pandalHeaderFood);
        pass = findViewById(R.id.pandalHeaderPass);

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
                case R.id.faboptions_help:
                    BaseUtility.show_popup(R.layout.dialog_help, this);
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
                case R.id.faboptions_qr:
                    try {
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.encodeBitmap("pandu-" + uid, BarcodeFormat.QR_CODE, 400, 400);
                        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pandu";
                        File dir = new File(file_path);
                        if(!dir.exists())
                            dir.mkdirs();
                        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(new Date());
                        File file = new File(dir, "QR_" + timeStamp + ".png");
                        FileOutputStream fOut = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
                        fOut.flush();
                        fOut.close();

                        Toast.makeText(getApplicationContext(), "New QR saved at " + file_path, Toast.LENGTH_SHORT).show();
                    } catch(Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(getApplicationContext(), "Could not save the QR.", Toast.LENGTH_SHORT).show();
                    }
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

        getInformation(uid);
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
        final TextInputEditText inputFood = promptsView.findViewById(R.id.textFood);
        final TextInputEditText inputPass = promptsView.findViewById(R.id.textPass);

        inputName.setText(currentPandal.getName());
        inputTheme.setText(currentPandal.getTheme());
        inputLocation.setText(currentPandal.getLocation());
        inputPicture.setText(currentPandal.getPicture());
        inputFood.setText(String.valueOf(currentPandal.getFood()));
        inputPass.setText(String.valueOf(currentPandal.getPass()));

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (DialogInterface dialog, int d_id) -> {
                            updateInformation(uid, new Pandal(
                                    inputName.getText().toString(),
                                    currentPandal.getPublicCount(),
                                    inputLocation.getText().toString(),
                                    inputTheme.getText().toString(),
                                    inputPicture.getText().toString(),
                                    Integer.parseInt(inputFood.getText().toString()),
                                    Integer.parseInt(inputPass.getText().toString())
                            ));
                        })
                .setNegativeButton("Cancel",
                        (DialogInterface dialog,int d_id) -> {
                            dialog.cancel();
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public void getRankingData() {}

    @Override
    public void fetchCurrentCrowdData(String oid) {}

    @Override
    public void updateInformation(String oid, Pandal pandal) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").document(oid)
                .set(pandal)
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
    }

    @Override
    public void getInformation(String oid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").document(oid)
                .get()
                .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                    if (task.isSuccessful()) {
                        BaseUtility.writeLog(TAG, "Pandel Info Access Successful");
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            name.setText(document.getString("name"));
                            theme.setText(document.getString("theme"));
                            location.setText(document.getString("location"));
                            food.setText(String.valueOf(document.getLong("food")));
                            pass.setText(String.valueOf(document.getLong("pass")));
                            BaseUtility.loadImageFromURL(this, document.getString("picture"), picture);

                            currentPandal = new Pandal(
                                    document.getString("name"),
                                    document.getLong("publicCount").intValue(),
                                    document.getString("location"),
                                    document.getString("theme"),
                                    document.getString("picture"),
                                    document.getLong("food").intValue(),
                                    document.getLong("pass").intValue()
                            );

                        } else {
                            BaseUtility.writeLog(TAG, "Pandel List Not found");

                            name.setText("[ Empty ]");
                            theme.setText("[ Empty ]");
                            location.setText("[ Empty ]");
                            food.setText("0");
                            pass.setText("0");
                            BaseUtility.loadImageFromURL(this, "https://semantic-ui.com/images/wireframe/image.png", picture);

                            currentPandal = new Pandal();

                            db.collection("pandels").document(oid)
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

    @Override
    public void getAllInformation() {}
}
