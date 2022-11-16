package com.example.clase15_terremotoapi

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TerremotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(terremotos: MutableList<Terremoto>)

    @Query("SELECT * FROM terremotos")
    fun getAllTerremotos(): MutableList<Terremoto>

    @Query("SELECT * FROM terremotos WHERE magnitude > :mag")
    fun getAllTerremotosByMagnitude(mag: Double): MutableList<Terremoto>

    @Update
    fun update(terremoto: Terremoto)

    @Delete
    fun delete(terremoto: Terremoto)
}