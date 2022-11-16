package com.example.clase15_terremotoapi

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "terremotos")
data class Terremoto(
    @PrimaryKey val id: String,
    val magnitude: Double,
    @ColumnInfo(name = "place") val lugar: String,
    val time: Long,
    val longitude: Double,
    val latitude: Double
): Parcelable
