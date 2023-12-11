package com.muratozcan.instaclone.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.FragmentLoginBinding
import com.muratozcan.instaclone.databinding.FragmentShareBinding
import java.util.UUID

class ShareFragment : Fragment() {

    private lateinit var binding: FragmentShareBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShareBinding.inflate(inflater, container, false)


        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.imageView.setOnClickListener {
            selectImage(it)
        }

        binding.buttonShare.setOnClickListener {
            upload(it)
        }


        return binding.root
    }

    private fun upload(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                //download url -> image
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()


                    var username = ""
                    var lat = 0;
                    var lng = 0;

                    if (auth.currentUser != null) {
                        val docRef = firestore.collection("User").document(auth.currentUser!!.uid)
                        docRef.addSnapshotListener { value, error ->
                            if (error != null) {
                                Log.e("Error", "Listen Failed.", error)
                                return@addSnapshotListener
                            }

                            if (value != null && value.exists()) {
                                Log.e("Success", value.data?.get("username").toString())
                                username = value.data?.get("username").toString()
                            } else {
                                Log.d("Error", "Current data: null")
                            }

                            val postMap = hashMapOf<String, Any>()
                            postMap.put("downloadUrl", downloadUrl)
                            postMap.put("username", username)
                            postMap.put("comment", binding.editTextComment.text.toString())
                            postMap.put("date", Timestamp.now())
                            postMap.put("lat", lat.toString())
                            postMap.put("lng", lng.toString())
                            postMap.put("uid", auth.currentUser!!.uid)

                            firestore.collection("Post").add(postMap).addOnSuccessListener {

                                /*val navController = findNavController()
                                navController.navigate(R.id.action_homePageFragment_to_searchFragment)

                                 */

                            }.addOnFailureListener {
                                Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                            }
                        }


                    }

                }

            }.addOnFailureListener {
                Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun selectImage(view: View) {

        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Log.e("Data","snackbar")
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            } else {
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if (intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture?.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else {
                //permission denied
                Toast.makeText(this.context, "Permission needed!", Toast.LENGTH_LONG).show()
            }
        }
    }

}