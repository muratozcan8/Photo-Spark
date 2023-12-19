package com.muratozcan.instaclone.view

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import com.muratozcan.instaclone.databinding.FragmentShareBinding
import com.squareup.picasso.Transformation
import java.util.UUID

class ShareFragment : Fragment(), LocationListener {

    private lateinit var binding: FragmentShareBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncherForLocation: ActivityResultLauncher<String>
    private lateinit var latitude: String
    private lateinit var longitude: String


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShareBinding.inflate(inflater, container, false)


        registerLauncher()
        resisterLauncherForLocation()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationListener = LocationListener {
            p0 -> Log.e("Share", "location: ${p0.latitude} / ${p0.longitude}")
            stopLocationUpdates()
        }
        setLocation()

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
                    val lat = latitude;
                    val lng = longitude;

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
                            postMap.put("lat", lat)
                            postMap.put("lng", lng)
                            postMap.put("uid", auth.currentUser!!.uid)

                            firestore.collection("Post").add(postMap).addOnSuccessListener {

                                val intent = Intent(requireContext(), requireActivity().javaClass)
                                requireActivity().finish()
                                requireActivity().startActivity(intent)

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
        else {
            Toast.makeText(this.context, "Please select an image!", Toast.LENGTH_LONG).show()
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

    private fun setLocation() {
        if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                Snackbar.make(binding.root, "Permission needed for location", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") {
                    permissionLauncherForLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
            } else {
                permissionLauncherForLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, this)
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

    private fun resisterLauncherForLocation() {

        permissionLauncherForLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()) {result ->
            if (result) {
                if (ContextCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, this)
                }
            } else {
                Toast.makeText(this.context, "Permission needed for location!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onLocationChanged(p0: Location) {
        Log.e("Share", "location: ${p0.latitude} / ${p0.longitude}")
        latitude = p0.latitude.toString()
        longitude = p0.longitude.toString()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        locationManager.removeUpdates(this)
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e("Share", "Status")
    }

}