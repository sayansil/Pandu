package team.OG.pandu.ListAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import team.OG.pandu.ListActivity;
import team.OG.pandu.R;
import team.OG.pandu.SplashActivity;
import team.OG.pandu.Units.LocalPass;
import team.OG.pandu.Units.Pandal;
import team.OG.pandu.Units.Pass;

public class ShopAdapter extends ArrayAdapter<LocalPass> {

    private static final String TAG = "RankAdapter";

    private Context mContext;
    private List<LocalPass> itemList;
    private String uid;
    private Pandal passPandal;

    public ShopAdapter(@NonNull Context context, ArrayList<LocalPass> list, String uid) {
        super(context, 0, list);
        this.mContext = context;
        this.itemList = list;
        this.uid = uid;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_pass, parent, false);
        }

        LocalPass currentItem = itemList.get(position);

        TextView info = listItem.findViewById(R.id.passInfo);
        info.setText(currentItem.getInfo());



        TextView pandal = listItem.findViewById(R.id.passPandal);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").document(currentItem.getPid())
                .get()
                .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        pandal.setText(document.getString("name"));
                    } else {
                        Log.w(TAG, "Error adding document");
                    }
                });

        TextView count = listItem.findViewById(R.id.passCount);
        count.setText(String.valueOf(currentItem.getRemaining()));

        listItem.setOnClickListener(view -> {
            db.collection("pass")
                    .add(new Pass(
                            1,
                            currentItem.getInfo(),
                            currentItem.getPid(),
                            uid,
                            0))
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Pass added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));


            db.collection("pandels").document(currentItem.getPid())
                    .get()
                    .addOnCompleteListener((@NonNull Task<DocumentSnapshot> task) -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            pandal.setText(document.getString("name"));

                            passPandal = new Pandal(document.getString("name"),
                                    document.getLong("publicCount").intValue(),
                                    document.getString("location"),
                                    document.getString("theme"),
                                    document.getString("picture"),
                                    document.getLong("food").intValue(),
                                    document.getLong("pass").intValue());
                            if(currentItem.getInfo().equals("VIP Pass")) {
                                passPandal.setPass(passPandal.getPass() - 1);
                            } else {
                                passPandal.setFood(passPandal.getFood() - 1);
                            }

                            db.collection("pandels").document(currentItem.getPid())
                                    .set(passPandal)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "Document added.");
                                        Intent intent = new Intent(mContext, ListActivity.class);
                                        mContext.startActivity(intent);
                                    })
                                    .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));

                        } else {
                            Log.w(TAG, "Error adding document");
                        }
                    });

        });

        return listItem;
    }
}
