package com.example.user.ui.profile

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.user.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import android.content.Intent
import android.widget.Button
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.user.login.LoginActivity
import com.example.user.ui.profile.sidesheet.TentangCentral
import com.example.user.ui.profile.sidesheet.TentangCentyActivity
import com.example.user.ui.profile.sidesheet.UmpanBalik
import com.google.android.material.imageview.ShapeableImageView


class ProfileFragment : Fragment() {
    private lateinit var nameTextView: TextView
    private lateinit var profile_image: ShapeableImageView
    private lateinit var emailTextView: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var card1: CardView
    private lateinit var card2: CardView
    private lateinit var card3: CardView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize FirebaseAuth instance
        auth = FirebaseAuth.getInstance()

        // Get references to TextViews
        nameTextView = view.findViewById(R.id.name_textview)
        emailTextView = view.findViewById(R.id.email_textview)
        profile_image = view.findViewById(R.id.profile_image)

 // Get references cardview
        card1 = view.findViewById(R.id.card1)
        card2 = view.findViewById(R.id.card2)
        card3 = view.findViewById(R.id.card3)

        card1.setOnClickListener {
            val intent = Intent(requireContext(), TentangCentyActivity::class.java)
            startActivity(intent)
        }
        card2.setOnClickListener {
            val intent = Intent(requireContext(), TentangCentral::class.java)
            startActivity(intent)
        }
        card3.setOnClickListener {
            val intent = Intent(requireContext(), UmpanBalik::class.java)
            startActivity(intent)
        }

        // Get current user
        val currentUser = auth.currentUser

        // Mengambil URL gambar profil pengguna dari Firebase Authentication
        val photoUrl = currentUser?.photoUrl

        // Memuat gambar profil menggunakan Glide
        Glide.with(this)
            .load(photoUrl)
            .placeholder(R.drawable.userprofile) // Placeholder image saat gambar sedang dimuat
            .error(R.drawable.userprofile) // Gambar yang akan ditampilkan jika terjadi kesalahan
            .into(profile_image)

        // Set user name and email to TextViews if the user is logged in
        currentUser?.let { user ->
            // Check if user signed in with Google
            if (user.providerData.any { it.providerId == GoogleAuthProvider.PROVIDER_ID }) {
                // Get Google sign-in user profile
                val googleUser = user.providerData.first { it.providerId == GoogleAuthProvider.PROVIDER_ID }

                // Set user name and email to TextViews
                nameTextView.text = googleUser.displayName
                emailTextView.text = googleUser.email
            } else {
                // For users not signed in with Google, display basic user information
                nameTextView.text = user.displayName
                emailTextView.text = user.email
            }
        }
        // Logout button click listener
        val logoutButton = view.findViewById<Button>(R.id.logout_button)
        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setTitle("Konfirmasi Logout")
        alertDialogBuilder.setMessage("Anda yakin ingin logout?")
        alertDialogBuilder.setPositiveButton("Ya") { dialogInterface: DialogInterface, _: Int ->
            // Sign out the user
            auth.signOut()
            // Redirect to LoginActivity
            val intent = Intent(activity, LoginActivity::class.java)
            startActivity(intent)
            activity?.finish() // Optionally, finish the current activity
            dialogInterface.dismiss()
        }
        alertDialogBuilder.setNegativeButton("Batal") { dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}
