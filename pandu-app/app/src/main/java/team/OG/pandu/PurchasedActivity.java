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

import team.OG.pandu.ListAdapters.PurchasedAdapter;
import team.OG.pandu.Units.PurchasedPass;

public class PurchasedActivity extends AppCompatActivity {
    private static final String TAG = "PurchasedActivity";

    ListView pListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_purchased);

        pListView = findViewById(R.id.purchasedListView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pass")
                .get()
                .addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {

                        ArrayList<PurchasedPass> pList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {

                            pList.add(new PurchasedPass(
                                    document.getString("info"),
                                    document.getString("pid"),
                                    document.getId()
                            ));
                        }

                        PurchasedAdapter mAdapter = new PurchasedAdapter(this, pList);
                        pListView.setAdapter(mAdapter);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

    }
}
