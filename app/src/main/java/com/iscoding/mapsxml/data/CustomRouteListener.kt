package com.iscoding.mapsxml.data

import android.graphics.Color
import android.util.Log
import com.codebyashish.googledirectionapi.ErrorHandling
import com.codebyashish.googledirectionapi.RouteInfoModel
import com.codebyashish.googledirectionapi.RouteListener
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap

class CustomRouteListener(private val map: GoogleMap) : RouteListener {
    private var polylines: MutableList<Polyline>? = null

    override fun onRouteFailure(e: ErrorHandling) {
        Log.w("TAG", "onRoutingFailure: $e")
    }

    override fun onRouteStart() {
        Log.d("TAG", "yes started")
    }

    override fun onRouteSuccess(routeInfoModelArrayList: ArrayList<RouteInfoModel>, routeIndexing: Int) {
        polylines?.forEach { it.remove() }
        polylines = mutableListOf()

        val polylineOptions = PolylineOptions()
            .color(Color.YELLOW)
            .width(12f)
            .startCap(RoundCap())
            .endCap(RoundCap())

        routeInfoModelArrayList.getOrNull(routeIndexing)?.let { routeInfo ->
            Log.e("TAG", "onRoutingSuccess: routeIndexing ${routeInfo.toString()}")

            polylineOptions.addAll(routeInfo.getPoints())
            val polyline = map.addPolyline(polylineOptions)
            polylines?.add(polyline)
        }

        Log.e("TAG", "onRoutingSuccess: routeIndexing $routeIndexing")
    }

    override fun onRouteCancelled() {
        Log.d("TAG", "route canceled")
        // Restart your route drawing
    }
}