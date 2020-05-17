package team.OG.pandu;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import team.OG.pandu.ListAdapters.RankAdapter;
import team.OG.pandu.Managers.FeedbackManager;
import team.OG.pandu.Managers.PandalManager;
import team.OG.pandu.Units.Feedback;
import team.OG.pandu.Units.Pandal;

public class RankingActivity extends AppCompatActivity implements PandalManager {

    private static final String TAG = "RankingActivity";

    ListView rankList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_ranking);

        rankList = findViewById(R.id.rankListView);

        getRankingData();
    }

    @Override
    public void getRankingData() {

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

                        Collections.sort(pandalList);
                        Collections.reverse(pandalList);

                        RankAdapter mAdapter = new RankAdapter(this, pandalList);
                        rankList.setAdapter(mAdapter);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    @Override
    public void fetchCurrentCrowdData(String oid) {}

    @Override
    public void updateInformation(String oid, Pandal pandal) {}

    @Override
    public void getInformation(String oid) {}

    @Override
    public void getAllInformation() {}

}
