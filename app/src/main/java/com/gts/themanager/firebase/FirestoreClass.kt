package com.gts.themanager.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gts.themanager.activities.SignInActivity
import com.gts.themanager.activities.SignUpActivity
import com.gts.themanager.models.User
import com.gts.themanager.utils.Constants

class FirestoreClass {

    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userInfo: User){

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()

            }.addOnFailureListener{
                e->
                Log.e(activity.javaClass.simpleName, "Errrrorrrr")
            }
    }

    fun signInUser(activity: SignInActivity){

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)
                if (loggedInUser != null)
                activity.signInSuccess(loggedInUser)

            }.addOnFailureListener{
                    e->
                Log.e("SignInUser", "Errrrorrrr")
            }
    }

     fun getCurrentUserId(): String{
         return FirebaseAuth.getInstance().currentUser!!.uid
     }
}