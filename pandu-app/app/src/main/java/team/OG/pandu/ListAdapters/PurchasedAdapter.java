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

import team.OG.pandu.R;
import team.OG.pandu.Units.PurchasedPass;

public class PurchasedAdapter extends ArrayAdapter<PurchasedPass> {

    private static final String TAG = "PurchasedAdapter";

    private Context mContext;
    private List<PurchasedPass> itemList;

    public PurchasedAdapter(@NonNull Context context, ArrayList<PurchasedPass> list) {
        super(context, 0, list);
        this.mContext = context;
        this.itemList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext)
                    .inflate(R.layout.item_purchased, parent, false);
        }

        PurchasedPass currentItem = itemList.get(position);

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

        TextView id = listItem.findViewById(R.id.passID);
        id.setText(String.valueOf(currentItem.getId()));

        return listItem;
    }
}
