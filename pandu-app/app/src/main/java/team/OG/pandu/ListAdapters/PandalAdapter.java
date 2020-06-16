package team.OG.pandu.ListAdapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import team.OG.pandu.BaseUtility;
import team.OG.pandu.PandalHomeActivity;
import team.OG.pandu.R;
import team.OG.pandu.Units.Pandal;

public class PandalAdapter extends ArrayAdapter<Pandal> {

    private static final String TAG = "PandalAdapter";

    private Context mContext;
    private List<Pandal> itemList;

    public PandalAdapter(@NonNull Context context, ArrayList<Pandal> list) {
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
                    .inflate(R.layout.item_pandal, parent, false);
        }

        Pandal currentItem = itemList.get(position);

        TextView name = listItem.findViewById(R.id.pandalName);
        name.setText(currentItem.getName());

        TextView theme = listItem.findViewById(R.id.pandalTheme);
        theme.setText(currentItem.getTheme());

        ImageView picture = listItem.findViewById(R.id.pandalImage);
        BaseUtility.loadImageFromURL(mContext, currentItem.getPicture(), picture);


        CardView pandal = listItem.findViewById(R.id.pandalCard);
        pandal.setOnClickListener((View view) -> {
            Intent intent = new Intent(mContext, PandalHomeActivity.class);
            intent.putExtra("name", currentItem.getName());
            intent.putExtra("theme", currentItem.getTheme());
            intent.putExtra("location", currentItem.getLocation());
            intent.putExtra("picture", currentItem.getPicture());
            intent.putExtra("food", String.valueOf(currentItem.getFood()));
            intent.putExtra("pass", String.valueOf(currentItem.getPass()));
            mContext.startActivity(intent);
        });

        return listItem;
    }
}
