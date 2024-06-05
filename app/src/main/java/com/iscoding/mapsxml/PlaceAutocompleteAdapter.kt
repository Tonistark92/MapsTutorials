//package com.iscoding.mapsxml
//
//import android.content.Context
//import android.graphics.Typeface
//import android.text.style.CharacterStyle
//import android.text.style.StyleSpan
//import android.util.Log
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.Filter
//import android.widget.TextView
//import com.google.android.gms.common.api.GoogleApiClient
//import com.google.android.gms.common.data.DataBufferUtils
//import com.google.android.gms.maps.model.LatLngBounds
//import com.google.android.gms.tasks.Task
//import com.google.android.libraries.places.api.Places
//import com.google.android.libraries.places.api.model.AutocompletePrediction
//import java.util.concurrent.TimeUnit
//
//class PlaceAutocompleteAdapter(
//    context: Context,
//    googleApiClient: GoogleApiClient,
//    bounds: LatLngBounds?,
//    filter: AutocompleteFilter?
//) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1) {
//
//    companion object {
//        private const val TAG = "PlaceAutoCompleteAd"
//        private val STYLE_BOLD: CharacterStyle = StyleSpan(Typeface.BOLD)
//    }
//
//    private var mGoogleApiClient: GoogleApiClient = googleApiClient
//    private var mBounds: LatLngBounds? = bounds
//    private var mPlaceFilter: AutocompleteFilter? = filter
//    private var mResultList: ArrayList<AutocompletePrediction> = ArrayList()
//
//    init {
//        mGoogleApiClient.connect()
//    }
//
//    fun setBounds(bounds: LatLngBounds?) {
//        mBounds = bounds
//    }
//
//    override fun getCount(): Int {
//        return mResultList.size
//    }
//
//    override fun getItem(position: Int): AutocompletePrediction {
//        return mResultList[position]
//    }
//
//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        val row = super.getView(position, convertView, parent)
//        val item = getItem(position)
//
//        val textView1 = row.findViewById<TextView>(android.R.id.text1)
//        val textView2 = row.findViewById<TextView>(android.R.id.text2)
//        textView1.text = item.getPrimaryText(STYLE_BOLD)
//        textView2.text = item.getSecondaryText(STYLE_BOLD)
//
//        return row
//    }
//
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(constraint: CharSequence?): FilterResults {
//                val results = FilterResults()
//                var filterData = ArrayList<AutocompletePrediction>()
//
//                if (!constraint.isNullOrBlank()) {
//                    filterData = getAutocomplete(constraint) ?: ArrayList()
//                }
//
//                results.values = filterData
//                results.count = filterData.size
//
//                return results
//            }
//
//            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
//                if (results != null && results.count > 0) {
//                    mResultList = results.values as ArrayList<AutocompletePrediction>
//                    notifyDataSetChanged()
//                } else {
//                    notifyDataSetInvalidated()
//                }
//            }
//
//            override fun convertResultToString(resultValue: Any?): CharSequence {
//                return if (resultValue is AutocompletePrediction) {
//                    resultValue.getFullText(null)
//                } else {
//                    super.convertResultToString(resultValue)
//                }
//            }
//        }
//    }
//
//    private fun getAutocomplete(constraint: CharSequence?): ArrayList<AutocompletePrediction>? {
//        if (mGoogleApiClient.isConnected) {
//            Log.i(TAG, "Starting autocomplete query for: $constraint")
//
//            val results: Task<AutocompletePredictionBuffer> = Places.getGeoDataClient(context)
//                .getAutocompletePredictions(
//                    constraint.toString(),
//                    mBounds,
//                    mPlaceFilter
//                )
//
//            val autocompletePredictions = results.await(60, TimeUnit.SECONDS)
//
//            val status = autocompletePredictions.status
//            if (!status.isSuccess) {
//                Log.e(TAG, "Error getting autocomplete prediction API call: $status")
//                autocompletePredictions.release()
//                return null
//            }
//
//            Log.i(TAG, "Query completed. Received ${autocompletePredictions.count} predictions.")
//            return DataBufferUtils.freezeAndClose(autocompletePredictions)
//        }
//
//        Log.e(TAG, "Google API client is not connected for autocomplete query.")
//        return null
//    }
//
//    override fun notifyDataSetChanged() {
//        super.notifyDataSetChanged()
//    }
//
//    override fun notifyDataSetInvalidated() {
//        super.notifyDataSetInvalidated()
//    }
//
//    override fun clear() {
//        super.clear()
//    }
//}
