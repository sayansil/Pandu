package team.OG.pandu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import team.OG.pandu.ListAdapters.ShopAdapter;
import team.OG.pandu.Units.LocalPass;
import team.OG.pandu.Units.Pass;

public class BuyActivity extends AppCompatActivity {
    private static final String TAG = "BuyActivity";

    ListView shopList;
    Button purchases;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_buy);

        shopList = findViewById(R.id.shopListView);
        purchases = findViewById(R.id.purchasesBtn);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels")
                .get()
                .addOnCompleteListener((@NonNull Task<QuerySnapshot> task) -> {
                    if (task.isSuccessful()) {

                        ArrayList<LocalPass> passList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(document.getLong("pass").intValue() > 0)
                                passList.add(new LocalPass("VIP Pass", document.getId(), document.getLong("pass").intValue()));
                            if(document.getLong("food").intValue() > 0)
                                passList.add(new LocalPass("Food Coupon", document.getId(), document.getLong("food").intValue()));
                        }

                        ShopAdapter mAdapter = new ShopAdapter(this, passList, FirebaseAuth.getInstance().getCurrentUser().getUid());
                        shopList.setAdapter(mAdapter);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });

        purchases.setOnClickListener((View view) -> {
            Intent intent = new Intent(BuyActivity.this, PurchasedActivity.class);
            startActivity(intent);
        });
    }
}
