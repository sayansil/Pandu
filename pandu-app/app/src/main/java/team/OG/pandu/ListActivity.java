package team.OG.pandu;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.joaquimley.faboptions.FabOptions;

import java.util.ArrayList;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import team.OG.pandu.Barcode.BarcodeCaptureActivity;
import team.OG.pandu.ListAdapters.PandalAdapter;
import team.OG.pandu.Managers.FeedbackManager;
import team.OG.pandu.Managers.PandalManager;
import team.OG.pandu.Units.Feedback;
import team.OG.pandu.Units.Pandal;

public class ListActivity extends AppCompatActivity implements FeedbackManager, PandalManager {

    ListView pandalListView;
    FabOptions bottomMenu;

    private static final int BARCODE_READER_REQUEST_CODE = 7;
    private static final String TAG = "ListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_list);

        pandalListView = findViewById(R.id.pandalListView);
        bottomMenu = findViewById(R.id.optionsHome);

        bottomMenu.setOnClickListener((View view) -> {
            switch(view.getId()) {
                case R.id.faboptions_scan:
                    Intent bc_intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
                    startActivityForResult(bc_intent, BARCODE_READER_REQUEST_CODE);
                    break;
                case R.id.faboptions_help:
                    BaseUtility.show_popup(R.layout.dialog_help, this);
                    break;
                case R.id.faboptions_info:
                    Intent info_intent = new Intent(ListActivity.this, InfoActivity.class);
                    startActivity(info_intent);
                    break;
                case R.id.faboptions_logout:
                    FirebaseAuth.getInstance().signOut();
                    Intent logout_intent = new Intent(ListActivity.this, SplashActivity.class);
                    startActivity(logout_intent);
                    finish();
                    break;
                case R.id.faboptions_rank:
                    Intent rank_intent = new Intent(ListActivity.this, RankingActivity.class);
                    startActivity(rank_intent);
                    break;
                case R.id.faboptions_buy:
                    Intent shop_intent = new Intent(ListActivity.this, BuyActivity.class);
                    startActivity(shop_intent);
                    break;
            }
        });

        getAllInformation();
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

//                    progressbar.setVisibility(View.VISIBLE);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("pandels").document(qrid)
                            .get()
                            .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();

                                    send_feedback(qrid, document.getString("name"));
                                } else {
                                    Log.w(TAG, "Error fetching document");
//                                    progressbar.setVisibility(View.GONE);
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

    private void send_feedback(String qrid, String name) {

        if (null == name) {
            Toast.makeText(getApplicationContext(), "Invalid QR", Toast.LENGTH_SHORT).show();
            return;
        }

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
        final ColorRatingBar ratingBar = promptsView.findViewById(R.id.ratingBar);

        pname.setText(name);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        (DialogInterface dialog, int d_id) -> {
                            String feedback = fbInput.getText().toString().trim();
                            double ratingValue = ratingBar.getRating();

                            addFeedback(feedback, ratingValue, uid, qrid);
                        })
                .setNegativeButton("Cancel",
                        (DialogInterface dialog,int d_id) -> {
                            dialog.cancel();
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public void addFeedback(String text, double rating, String uid, String oid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels/" + oid + "/reviews").document(uid)
                .set(new Feedback(
                        text,
                        rating,
                        uid
                ))
                .addOnSuccessListener((Void aVoid) -> {
                    Log.d(TAG, "Document added.");
                    BaseUtility.show_popup(R.layout.dialog_done, this);
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    Log.w(TAG, "Error adding document", e);
                });
    }

    @Override
    public void getFeedbackData(String oid) {}

    @Override
    public void analyzeFeedback() {}

    @Override
    public void getRankingData() {}

    @Override
    public void fetchCurrentCrowdData(String oid) {}

    @Override
    public void updateInformation(String oid, Pandal pandal) {}

    @Override
    public void getInformation(String oid) {}

    @Override
    public void getAllInformation() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels")
                .get()
                .addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {

                        ArrayList<Pandal> pandalList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            pandalList.add(new Pandal(
                                    document.getString("name"),
                                    document.getLong("publicCount").intValue(),
                                    document.getString("location"),
                                    document.getString("theme"),
                                    document.getString("picture")));
                        }

                        PandalAdapter mAdapter = new PandalAdapter(this, pandalList);
                        pandalListView.setAdapter(mAdapter);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

}
