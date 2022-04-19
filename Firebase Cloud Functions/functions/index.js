const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

// {docId} -> to represent any document
// onCreate -> whenever a document is created on this path, it triggers and run the code below to send notifications to user
exports.androidPushNotification = functions.firestore.document("UOW_detection/{docId}").onCreate(
    (snapshot, context) => {
        admin.messaging().sendToTopic(
            "intrusion_detected",
            {
                notification:{
                    title: "Intrusion Alert",
                    body: "A new intrusion is detected. Check your intrusion notifications now!"
                }
            }
        );
    }
);