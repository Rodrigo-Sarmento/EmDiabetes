package com.digo.emdiabetes.model

import android.os.Parcelable
import com.digo.emdiabetes.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Medication(
    var id: String = "",
    var nome: String = "",
    var dosagem: String = ""
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}