package com.ebata_shota.holdemstacktracker.infra.extension

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction

/**
 * FirebaseRealtimeDatabase複数同時編集をするための
 */
fun DatabaseReference.runTransaction(
    doTransaction: (currentData: MutableData) -> Unit,
) {
    runTransaction(object : Transaction.Handler{
        override fun doTransaction(currentData: MutableData): Transaction.Result {
            doTransaction.invoke(currentData)
            return Transaction.success(currentData)
        }

        override fun onComplete(
            error: DatabaseError?,
            committed: Boolean,
            currentData: DataSnapshot?,
        ) {
            // TODO:
        }
    })
}