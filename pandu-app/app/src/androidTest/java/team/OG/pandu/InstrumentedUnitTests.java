package team.OG.pandu;

import android.content.Context;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedUnitTests {

    private boolean[] ctrl;
    private int actual_db_size;

    @Before
    public void init() {
        int n_test = 3;
        actual_db_size = 6;
        ctrl = new boolean[n_test];
    }

    @Test // # 1
    public void useAppContext() {
        final int test_number = 1;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("team.OG.pandu", appContext.getPackageName());
    }


    @Test // # 2
    public void accessFirebaseTrue() {
        final int test_number = 2;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    count++;
                }

                assertEquals(count, actual_db_size);
            }
            ctrl[test_number - 1] = true;
        });

        while (!ctrl[test_number - 1]);

    }

    @Test // # 3
    public void accessFirebaseFalse() {
        final int test_number = 3;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = 0;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    count++;
                }

                assertNotEquals(count, actual_db_size + 1);
            }
            ctrl[test_number - 1] = true;
        });

        while (!ctrl[test_number - 1]);

    }
}
