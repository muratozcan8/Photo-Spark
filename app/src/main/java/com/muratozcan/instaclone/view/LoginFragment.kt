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
import com.muratozcan.instaclone.R
import com.muratozcan.instaclone.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = Firebase.auth
        val navController = findNavController()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.buttonSignIn.setOnClickListener{
            signIn()
        }

        binding.textViewSignUp.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        return binding.root
    }

    private fun signIn(){
        val email = binding.editTextEmailSignIn.text.toString()
        val password = binding.editTextPasswordSignIn.text.toString()

        if (email == "" || password == "") {
            Toast.makeText(this.context, "Enter email and password!", Toast.LENGTH_LONG).show()
        } else {
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val intent = Intent(activity, MainActivity::class.java)
                    startActivity(intent)
                }.addOnFailureListener {
                    Toast.makeText(this.context, it.localizedMessage, Toast.LENGTH_LONG).show()
                }
        }
    }

}