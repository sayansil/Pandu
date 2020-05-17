package team.OG.pandu_organiser;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hyogeun.github.com.colorratingbarlib.ColorRatingBar;
import team.OG.pandu_organiser.Managers.FeedbackManager;
import team.OG.pandu_organiser.Managers.PandalManager;
import team.OG.pandu_organiser.Units.Feedback;
import team.OG.pandu_organiser.Units.Pandal;

public class AnalyzeActivity extends AppCompatActivity implements FeedbackManager, PandalManager {

    private static final String TAG = "AnalyzeActivity";

    TextView crowdCount;
    TextView positiveRate;
    ColorRatingBar starRate;

    String uid;

    ArrayList<Feedback> fbList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_analyze);

        crowdCount = findViewById(R.id.textCC);
        positiveRate = findViewById(R.id.ratingPos);
        starRate = findViewById(R.id.ratingStar);

        uid = getIntent().getStringExtra("uid");

        fetchCurrentCrowdData(uid);

        fbList = new ArrayList<>();
        getFeedbackData(uid);
    }

    @Override
    public void addFeedback(String text, double rating, String uid, String oid) {}

    @Override
    public void getRanking() {}

    @Override
    public void analyzeFeedback() {}

    @Override
    public void getFeedbackData(String oid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("pandels/" + oid + "/reviews")
            .get()
            .addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                if (task.isSuccessful()) {
                    fbList = new ArrayList<>();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        fbList.add(new Feedback(
                                document.getString("text"),
                                document.getDouble("rating"),
                                document.getString("uid")
                        ));
                    }

                    refresh_star();
                    refresh_pos();

                } else {
                    Log.w(TAG, "Error getting documents.", task.getException());
                }
            });
    }

    @Override
    public void getRankingData() {}

    @Override
    public void fetchCurrentCrowdData(String oid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("pandels").document(oid)
                .get()
                .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document.exists()) {
                            crowdCount.setText(document.getLong("publicCount").intValue() + "");
                        }
                    } else {
                        Log.e(TAG, "Error fetching document");
                    }
                });

    }

    @Override
    public void updateInformation(String oid, Pandal pandal) {}

    @Override
    public void getInformation(String oid) {}

    private void refresh_star() {
        double star_rating = 0;
        double count = 0;

        for (Feedback fb: fbList) {
            star_rating += fb.getRating();
            count++;
        }

        if(count!=0)
            star_rating /= count;

        starRate.setRating((float)star_rating);
    }

    private void refresh_pos() {
        double positive_rating = 0.0;

        // todo

        positiveRate.setText(positive_rating + "%");
    }
}
