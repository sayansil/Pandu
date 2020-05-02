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

import team.OG.pandu.ListAdapters.ShopAdapter;
import team.OG.pandu.Units.Pass;

public class BuyActivity extends AppCompatActivity {
    private static final String TAG = "BuyActivity";

    ListView shopList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy);

        shopList = findViewById(R.id.shopListView);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pass")
                .get()
                .addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {

                        ArrayList<Pass> passList = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            passList.add(new Pass(
                                    document.getLong("admit").intValue(),
                                    document.getString("info"),
                                    document.getString("pid"),
                                    document.getDouble("price")
                            ));
                        }

                        ShopAdapter mAdapter = new ShopAdapter(this, passList);
                        shopList.setAdapter(mAdapter);

                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }
}
