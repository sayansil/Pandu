package team.OG.pandu.ListAdapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import team.OG.pandu.R;
import team.OG.pandu.Units.Pandal;

public class RankAdapter extends ArrayAdapter<Pandal> {

    private static final String TAG = "RankAdapter";

    private Context mContext;
    private List<Pandal> itemList;

    public RankAdapter(@NonNull Context context, ArrayList<Pandal> list) {
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
                    .inflate(R.layout.item_crowd, parent, false);
        }

        Pandal currentItem = itemList.get(position);

        TextView name = listItem.findViewById(R.id.pandalName);
        name.setText(currentItem.getName());

        TextView rank = listItem.findViewById(R.id.pandalRank);
        rank.setText(String.valueOf(position + 1));

        TextView crowdStatus = listItem.findViewById(R.id.pandelCrowdStatus);
        int crowdCount = currentItem.getPublicCount();

        if (crowdCount > 200) {
            crowdStatus.setText("High");
            crowdStatus.setTextColor(Color.parseColor("#eb0c0c"));
        } else if (crowdCount > 100) {
            crowdStatus.setText("Moderate");
            crowdStatus.setTextColor(Color.parseColor("#ff8c00"));
        } else {
            crowdStatus.setText("Low");
            crowdStatus.setTextColor(Color.parseColor("#17c200"));
        }

        return listItem;
    }
}
