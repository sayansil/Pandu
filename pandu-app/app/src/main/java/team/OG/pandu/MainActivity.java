package team.OG.pandu;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.thekhaeng.pushdownanim.PushDownAnim;

import team.OG.pandu.Barcode.BarcodeCaptureActivity;

import static com.thekhaeng.pushdownanim.PushDownAnim.MODE_SCALE;

public class MainActivity extends AppCompatActivity {

    private ImageView camBtn;
    private ImageView helpBtn;
    private ImageView logoutBtn;
    private ImageView infoBtn;

    private ConstraintLayout progressbar;

    private Vibrator vibrator;

    private static final int BARCODE_READER_REQUEST_CODE = 7;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.GONE);

        camBtn = findViewById(R.id.camBtn);
        PushDownAnim.setPushDownAnimTo(camBtn)
                .setScale( MODE_SCALE, 0.89f  )
                .setOnClickListener( ( View view ) -> {
                    vibrate();
                    Intent intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                    startActivityForResult(intent, BARCODE_READER_REQUEST_CODE);
                });

        helpBtn = findViewById(R.id.helpBtn);
        PushDownAnim.setPushDownAnimTo(helpBtn)
                .setScale( MODE_SCALE, 0.89f  )
                .setOnClickListener( ( View view ) -> {
                    vibrate();
                    show_popup(R.layout.dialog_help);
                });

        logoutBtn = findViewById(R.id.logoutBtn);
        PushDownAnim.setPushDownAnimTo(logoutBtn)
                .setScale( MODE_SCALE, 0.89f  )
                .setOnClickListener( ( View view ) -> {
                    vibrate();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(MainActivity.this, SplashActivity.class);
                    startActivity(intent);
                    finish();
                });

        infoBtn = findViewById(R.id.infoBtn);
        PushDownAnim.setPushDownAnimTo(infoBtn)
                .setScale( MODE_SCALE, 0.89f  )
                .setOnClickListener( ( View view ) -> {
                    vibrate();
                    Intent intent = new Intent(MainActivity.this, InfoActivity.class);
                    startActivity(intent);
                });

    }

    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }

    private void send_feedback(String qrid, String name) {
        if (name.length() > 20) {
            name = name.substring(0, 20) + '\u2026';
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.dialog_send, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final TextInputEditText fbInput = promptsView.findViewById(R.id.textfb);
        final TextView pname = promptsView.findViewById(R.id.pnameText);
        pname.setText(name);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (DialogInterface dialog,int d_id) -> {
                            String feedback = fbInput.getText().toString().trim();

                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("pandels/" + qrid + "/reviews")
                                    .add(new ReviewUnit(
                                            feedback,
                                            uid
                                    ))
                                    .addOnSuccessListener((DocumentReference documentReference) -> {
                                        Log.d(TAG, "Document added.");
                                        progressbar.setVisibility(View.GONE);
                                        show_popup(R.layout.dialog_done);
                                    })
                                    .addOnFailureListener((@NonNull Exception e) -> {
                                        Log.w(TAG, "Error adding document", e);
                                        progressbar.setVisibility(View.GONE);
                                    });
                        })
                .setNegativeButton("Cancel",
                        (DialogInterface dialog,int d_id) -> {
                            dialog.cancel();
                            progressbar.setVisibility(View.GONE);
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);

                    String id = barcode.displayValue;
                    if (id.length() < 6 || !id.substring(0, 6).equals("pandu-")) {
                        Toast.makeText(getApplicationContext(), "Invalid QR code", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String qrid = id.substring(6);

                    progressbar.setVisibility(View.VISIBLE);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("pandels").document(qrid)
                            .get()
                            .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    send_feedback(qrid, document.getString("name"));
                                } else {
                                    Log.w(TAG, "Error adding document");
                                    progressbar.setVisibility(View.GONE);
                                }
                            });
                }
                else {
                    Toast.makeText(getApplicationContext(), "No QR code captured", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Log.e("Barcode error", String.format("Error reading barcode: %1$s",
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(50);
        }
    }

    private void show_popup(int resource) {
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(resource, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder.setCancelable(true);

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();

    }
}
