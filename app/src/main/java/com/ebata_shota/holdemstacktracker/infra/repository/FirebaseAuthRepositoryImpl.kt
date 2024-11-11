package com.ebata_shota.holdemstacktracker.infra.repository

import android.util.Log
import com.ebata_shota.holdemstacktracker.di.annotation.ApplicationScope
import com.ebata_shota.holdemstacktracker.domain.repository.FirebaseAuthRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class FirebaseAuthRepositoryImpl
@Inject
constructor(
    private val firebaseAuth: FirebaseAuth,
    @ApplicationScope
    private val appCoroutineScope: CoroutineScope,
) : FirebaseAuthRepository {

    private val _uidFlow = MutableSharedFlow<String>(replay = 1)
    override val uidFlow: SharedFlow<String> = _uidFlow.asSharedFlow()

    /**
     * 匿名ログイン
     */
    override fun signInAnonymously() {
        val listener: (Task<AuthResult>) -> Unit = { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user: FirebaseUser? = firebaseAuth.currentUser
                val uid = user?.uid
                Log.d("hoge", "signInAnonymously:success $uid")
                uid?.let {
                    appCoroutineScope.launch {
                        _uidFlow.emit(it)
                    }
                }
            } else {
                // If sign in fails, display a message to the user.
                Log.w("hoge", "signInAnonymously:failure", task.exception)
            }
        }
        firebaseAuth.signInAnonymously()
            .addOnCompleteListener(listener)
    }
}