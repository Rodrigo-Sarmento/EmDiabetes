package com.digo.emdiabetes.model

import android.os.Parcelable
import com.digo.emdiabetes.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Glycemia(
    var id: String = "",
    var glicemia: String = "",
    var descricao: String = ""
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}