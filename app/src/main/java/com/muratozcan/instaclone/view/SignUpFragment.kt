package com.muratozcan.instaclone.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        firestore = Firebase.firestore

        binding.button.setOnClickListener {
            singUp()
        }

        return binding.root
    }

    private fun singUp(){

        val email = binding.editTextEmailSignUp.text.toString()
        val password = binding.editTextPasswordSignUp.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this.context, "Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    val postMap = hashMapOf<String, Any>()
                    postMap.put("email", auth.currentUser?.email.toString())
                    postMap.put("username", binding.editTextUserNameSignUp.text.toString())

                    firestore.collection("User").document(auth.currentUser?.uid.toString()).set(postMap).addOnSuccessListener {
                        val navController = findNavController()
                        navController.navigate(R.id.action_signUpFragment_to_loginFragment)
                    }


                }.addOnFailureListener {
                    Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

}