package team.OG.pandu.ListAdapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import team.OG.pandu.R;
import team.OG.pandu.Units.Pass;

public class ShopAdapter extends ArrayAdapter<Pass> {

    private static final String TAG = "RankAdapter";

    private Context mContext;
    private List<Pass> itemList;

    public ShopAdapter(@NonNull Context context, ArrayList<Pass> list) {
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
                    .inflate(R.layout.item_pass, parent, false);
        }

        Pass currentItem = itemList.get(position);

        TextView price = listItem.findViewById(R.id.passPrice);
        price.setText("₹" + (int)currentItem.getPrice());

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

        TextView admit = listItem.findViewById(R.id.passAdmit);
        admit.setText(String.valueOf(currentItem.getAdmit()));

        return listItem;
    }
}
