package com.gts.themanager.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.gts.themanager.activities.*
import com.gts.themanager.models.Board
import com.gts.themanager.models.User
import com.gts.themanager.utils.Constants
import java.sql.Array

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

    fun createBoard(activity: CreateBoardActivity, board: Board){

        mFirestore.collection(Constants.BOARDS)
            .document()
            .set(board, SetOptions.merge())
            .addOnSuccessListener {
                Log.e(activity.javaClass.simpleName, "Board Created Successfully!")
                Toast.makeText(activity, "Board Created Successfully!", Toast.LENGTH_SHORT).show()
                activity.boardCreatedSuccessfully()
            }.addOnFailureListener{
                exception ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating a Board.",
                    exception
                )
            }

    }

    fun getBoardsList(activity: MainActivity){
        mFirestore.collection(Constants.BOARDS)
            .whereArrayContains(Constants.ASSIGNED_TO, getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                Log.i(activity.javaClass.simpleName, document.documents.toString())
                val boardList: ArrayList<Board> = ArrayList()
                for(i in document.documents){
                    val board = i.toObject(Board::class.java)!!
                    board.documentId = i.id
                    boardList.add(board)
                }

                activity.populateBoardsListToUI(boardList)

            }.addOnFailureListener{ e ->

                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while creating a board.", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity,
                              userHashMap: HashMap<String, Any>){
        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile Data Updated Successfully!")
                Toast.makeText(activity, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                activity.profileUpdateSuccess()
            }.addOnFailureListener{
                e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName, "Error while creating a board.",
                    e
                )
                Toast.makeText(activity, "Error while Updating Profile!!!", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadUserdata(activity: Activity, readBoardsList: Boolean = false) {

        mFirestore.collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener { document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser, readBoardsList)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }

            }.addOnFailureListener { e ->

                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }
                Log.e("SignInUser", "Errrrorrrr", e)
            }
    }

     fun getCurrentUserId(): String{

         var currentUser = FirebaseAuth.getInstance().currentUser
         var currentUserId = ""
         if (currentUser != null){
             currentUserId = currentUser.uid
         }
         return currentUserId
     }
}