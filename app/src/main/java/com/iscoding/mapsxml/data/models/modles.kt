package com.iscoding.mapsxml.data.models

import com.google.gson.annotations.SerializedName

data class DirectionsResponse(
    @SerializedName("routes") val routes: List<Route>
)

data class Route(
    @SerializedName("overview_polyline") val overviewPolyline: OverviewPolyline
)

data class OverviewPolyline(
    @SerializedName("points") val points: String
)