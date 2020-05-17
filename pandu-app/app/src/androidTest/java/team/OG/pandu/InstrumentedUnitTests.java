package team.OG.pandu;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.platform.app.InstrumentationRegistry;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import team.OG.pandu.Units.Feedback;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4ClassRunner.class)
public class InstrumentedUnitTests {

    private boolean[] ctrl;
    private int actual_db_size;
    private int test_publicCount;
    private String test_name;
    private String test_theme;
    private String test_location;
    private String test_picture;

    @Before
    public void init() {
        int n_test = 7;
        actual_db_size = 6;
        test_publicCount = 100;
        test_name = "Test Pandal";
        test_theme = "test456";
        test_location = "";
        test_picture = "https://201758-624029-raikfcquaxqncofqfm.stackpathdns.com/wp-content/uploads/2017/03/img-placeholder.png";
        ctrl = new boolean[n_test];
    }

    @Test // # 1
    public void useAppContext() {
        final int test_number = 1;
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        ctrl[test_number - 1] = true;
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

    @Test // # 4
    public void accessPublicFields() {
        final int test_number = 4;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels")
                .document("4VCrJukancYOegKkncB409HhE363")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            assert(document.contains("publicCount"));
                            assert(document.contains("theme"));
                            assert(document.contains("location"));
                            assert(document.contains("name"));
                            assert(document.contains("picture"));
                        } else {
                            fail();
                        }
                    }

                    ctrl[test_number - 1] = true;
                });

        while (!ctrl[test_number - 1]);
    }

    @Test // # 5
    public void getPublicFields() {
        final int test_number = 5;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels")
                .document("4VCrJukancYOegKkncB409HhE363")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            assertEquals(test_publicCount, document.getLong("publicCount").intValue());
                            assertEquals(test_theme, document.getString("theme"));
                            assertEquals(test_name, document.getString("name"));
                            assertEquals(test_location, document.getString("location"));
                            assertEquals(test_picture, document.getString("picture"));
                        } else {
                            fail();
                        }
                    }

                    ctrl[test_number - 1] = true;
                });

        while (!ctrl[test_number - 1]);
    }

    @Test // # 6
    public void addFeedback() {
        final int test_number = 6;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels/4VCrJukancYOegKkncB409HhE363/reviews")
                .document("test-user")
                .set(new Feedback(
                        "test1",
                        5,
                        "test-user"
                ))
                .addOnSuccessListener((Void aVoid) -> {
                    assert (true);
                    ctrl[test_number - 1] = true;
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    fail();
                    ctrl[test_number - 1] = true;
                });

        while (!ctrl[test_number - 1]);
    }

    @Test // # 7
    public void checkFeedback() {
        final int test_number = 7;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("pandels/4VCrJukancYOegKkncB409HhE363/reviews")
                .document("test-user")
                .set(new Feedback(
                        "test123",
                        5,
                        "test-user"
                ))
                .addOnSuccessListener((Void aVoid) -> {
                    db.collection("pandels/4VCrJukancYOegKkncB409HhE363/reviews")
                            .document("test-user")
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        assertEquals("test123", document.getString("text"));
                                        ctrl[test_number - 1] = true;
                                    } else {
                                        fail();
                                        ctrl[test_number - 1] = true;
                                    }
                                }
                            });
                })
                .addOnFailureListener((@NonNull Exception e) -> {
                    fail();
                    ctrl[test_number - 1] = true;
                });

        while (!ctrl[test_number - 1]);
    }
}
