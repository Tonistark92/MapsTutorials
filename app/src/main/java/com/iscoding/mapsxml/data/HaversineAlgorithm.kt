package com.iscoding.mapsxml.data

import kotlin.math.*


//// u gice it your location and then the distinations u want what of them is the nearest and it returns it
fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371e3 // Earth radius in meters
    val phi1 = lat1 * Math.PI / 180 // Convert latitude from degrees to radians
    val phi2 = lat2 * Math.PI / 180
    val deltaPhi = (lat2 - lat1) * Math.PI / 180
    val deltaLambda = (lon2 - lon1) * Math.PI / 180

    val a = sin(deltaPhi / 2).pow(2) + cos(phi1) * cos(phi2) * sin(deltaLambda / 2).pow(2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c // Distance in meters
}

data class Location(val latitude: Double, val longitude: Double)

fun findNearestStation(userLocation: Location, stations: List<Location>): Location {
    var nearestStation = stations[0]
    var minDistance = haversine(userLocation.latitude, userLocation.longitude, nearestStation.latitude, nearestStation.longitude)

    for (station in stations) {
        val distance = haversine(userLocation.latitude, userLocation.longitude, station.latitude, station.longitude)
        if (distance < minDistance) {
            nearestStation = station
            minDistance = distance
        }
    }

    return nearestStation
}