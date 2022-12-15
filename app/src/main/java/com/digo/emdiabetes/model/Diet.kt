package com.digo.emdiabetes.model

import android.os.Parcelable
import com.digo.emdiabetes.helper.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Diet(
    var id: String = "",
    var refeicao: String = "",
    var alimento: String = "",
) : Parcelable {
    init {
        this.id = FirebaseHelper.getDatabase().push().key ?: ""
    }
}