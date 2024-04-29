package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import java.util.UUID

class UserViewModel : ViewModel() {
    private var credential: GoogleIdTokenCredential? = null
    val token get() = "Bearer ${credential?.idToken}"

    var userUuid: UUID? = null

    fun setCredential(credential: GoogleIdTokenCredential) {
        this.credential = credential
    }
}
