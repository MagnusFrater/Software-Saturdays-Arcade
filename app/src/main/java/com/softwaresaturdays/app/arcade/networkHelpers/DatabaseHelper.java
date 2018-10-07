package com.softwaresaturdays.app.arcade.networkHelpers;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.softwaresaturdays.app.arcade.models.User;

public class DatabaseHelper {

    public static final String KEY_USERS = "users";

    public static void uploadUserInfo(User user) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(KEY_USERS);

        colRef.document(user.getUid()).set(user);
    }
}
