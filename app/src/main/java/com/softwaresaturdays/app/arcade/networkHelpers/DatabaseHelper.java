package com.softwaresaturdays.app.arcade.networkHelpers;

import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.softwaresaturdays.app.arcade.models.Message;
import com.softwaresaturdays.app.arcade.models.TextMessage;
import com.softwaresaturdays.app.arcade.models.User;

import java.util.ArrayList;

public class DatabaseHelper {

    public static final String KEY_USERS = "users";
    public static final String KEY_MESSAGES = "messages";
    private static final String TAG = "DATABASE_HELPER:";

    public static void uploadUserInfo(User user) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(KEY_USERS);

        colRef.document(user.getUid()).set(user);
    }

    public static void uploadMessage(Message message) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(KEY_MESSAGES);

        colRef.document(message.getTimestamp() + "").set(message);
    }

    public static void fetchMessages(final OnDatabaseFetchListener listener) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference colRef = db.collection(KEY_MESSAGES);

        colRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots,
                                @javax.annotation.Nullable FirebaseFirestoreException e) {

                if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                    Log.d(TAG, "Current data: " + queryDocumentSnapshots.size() + " messages");

                    ArrayList<Message> messages = new ArrayList<>();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.get("type").equals(Message.TYPE_TEXT_MESSAGE)) {
                            try {
                                TextMessage text = documentSnapshot.toObject(TextMessage.class);
                                messages.add(text);
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }

                        }
                    }

                    listener.onMessagesFetched(messages);

                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public interface OnDatabaseFetchListener {
        void onMessagesFetched(ArrayList<Message> messages);
    }
}
