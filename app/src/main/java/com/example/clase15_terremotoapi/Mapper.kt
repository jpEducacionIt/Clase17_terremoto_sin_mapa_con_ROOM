package com.example.clase15_terremotoapi

fun Feature.toTerromoto() = Terremoto(
    id,
    properties.mag,
    properties.place,
    properties.time,
    geometry.longitude,
    geometry.latitude
)
