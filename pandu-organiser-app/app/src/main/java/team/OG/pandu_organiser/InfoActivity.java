package team.OG.pandu_organiser;

import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class InfoActivity extends AppCompatActivity {

    private static final String TAG = "InfoActivity";

    private ListView devListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_info);

        TextView textVersion = findViewById(R.id.textVersion);
        devListView = findViewById(R.id.devList);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            String versionText = "Version: " + version;
            textVersion.setText(versionText);
        } catch (Exception e) {
            Log.w(TAG, "Could not get version number");
        }

        ArrayList<String> devs = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.dev_names)));
        ArrayAdapter mAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,devs){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView tv = (TextView) super.getView(position,convertView,parent);
                tv.setGravity(Gravity.CENTER);

                return tv;
            }
        };

        devListView.setAdapter(mAdapter);
    }

}

