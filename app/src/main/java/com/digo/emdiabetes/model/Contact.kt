package com.digo.emdiabetes.model

import android.os.Parcelable
import com.digo.emdiabetes.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    var id: String = "",
    var nome: String = "",
    var numero: Int = 0
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}