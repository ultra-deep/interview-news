package com.seven.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize data class News(
    @SerializedName("description") var description : String?,
    @SerializedName("url") var url : String?,
    @SerializedName("id") var id : Int?,
    @SerializedName("type") var type : String?,
    @SerializedName("text") var text : String?
) : Parcelable {

//    constructor() : this("","",0, TYPE_TEXT , "")

    companion object {
        const val  TYPE_IMAGE = "IMAGE"
        const val  TYPE_AD = "AD"
        const val  TYPE_TEXT = "TEXT"
    }

    fun isTextType() =  TYPE_TEXT.equals(type)
    fun isImageType() =  TYPE_IMAGE.equals(type)
    fun isAdvertiseType() =  TYPE_AD.equals(type)
}