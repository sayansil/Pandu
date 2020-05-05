package team.OG.pandu;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    LottieAnimationView qr;
    LottieAnimationView valid;

    TextView noNetText;
    LottieAnimationView noNetAnim;


    final int startDelay = 0;
    final int delay = 2000;

    Handler handler = new Handler();
    Runnable runnable;

    private static final int RC_SIGN_IN = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        qr = findViewById(R.id.qranim);
        valid = findViewById(R.id.validanim);

        noNetText = findViewById(R.id.nonetText);
        noNetText.setVisibility(View.GONE);

        noNetAnim = findViewById(R.id.nonetAnim);
        noNetAnim.setVisibility(View.GONE);

        valid.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                valid.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        qr.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                qr.setVisibility(View.GONE);

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null)
                    forward();
                else {
                    handler.postDelayed(runnable = () -> {
                        if (isNetworkConnected(getApplicationContext())) {
                            attemptLogin();
                            List<AuthUI.IdpConfig> providers = Arrays.asList(
                                    new AuthUI.IdpConfig.GoogleBuilder().build());
                            startActivityForResult(
                                    AuthUI.getInstance()
                                            .createSignInIntentBuilder()
                                            .setAvailableProviders(providers)
                                            .build(),
                                    RC_SIGN_IN);

                        } else {
                            noNetText.setText(getString(R.string.no_internet));
                            noNetText.setVisibility(View.VISIBLE);
                            noNetAnim.setVisibility(View.VISIBLE);
                        }
                        handler.postDelayed(runnable, delay);
                    }, startDelay);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                forward();
            }
        }
    }

    private void attemptLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
            forward();
    }

    private void forward() {
        Intent intent = new Intent(SplashActivity.this, ListActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up, 0);
        finish();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }
}
