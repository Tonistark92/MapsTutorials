package com.iscoding.mapsxml

import android.content.Context
import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.concurrent.TimeUnit


class PlaceAutocompleteAdapter(
    context: Context,
    private var mBounds: com.google.android.libraries.places.api.model.RectangularBounds?
) : ArrayAdapter<AutocompletePrediction>(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1) {

    companion object {
        private const val TAG = "PlaceAutoCompleteAd"
        private val STYLE_BOLD: CharacterStyle = StyleSpan(Typeface.BOLD)
    }

    private var mResultList: ArrayList<AutocompletePrediction> = ArrayList()
    private var placesClient: PlacesClient = Places.createClient(context)
//    private var token: AutocompleteSessionToken = AutocompleteSessionToken.newInstance()

    init {
        // Initialize the SDK
        Places.initialize(context.applicationContext, BuildConfig.API_KEY)
    }

    fun setBounds(bounds: com.google.android.libraries.places.api.model.RectangularBounds?) {
        mBounds = bounds
    }

    override fun getCount(): Int {
        return mResultList.size
    }

    override fun getItem(position: Int): AutocompletePrediction {
        return mResultList[position]
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = super.getView(position, convertView, parent)
        val item = getItem(position)

        val textView1 = row.findViewById<TextView>(android.R.id.text1)
        val textView2 = row.findViewById<TextView>(android.R.id.text2)
        textView1.text = item.getPrimaryText(STYLE_BOLD)
        textView2.text = item.getSecondaryText(STYLE_BOLD)

        return row
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                var filterData = ArrayList<AutocompletePrediction>()

                if (!constraint.isNullOrBlank()) {
                    filterData = getAutocomplete(constraint) ?: ArrayList()
                }

                results.values = filterData
                results.count = filterData.size

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    mResultList = results.values as ArrayList<AutocompletePrediction>
                    notifyDataSetChanged()
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun convertResultToString(resultValue: Any?): CharSequence {
                return if (resultValue is AutocompletePrediction) {
                    resultValue.getFullText(null)
                } else {
                    super.convertResultToString(resultValue)
                }
            }
        }
    }

    private fun getAutocomplete(constraint: CharSequence): ArrayList<AutocompletePrediction>? {
        val request = FindAutocompletePredictionsRequest.builder()
            .setLocationBias(mBounds)
//            .setSessionToken(token)
            .setQuery(constraint.toString())
            .build()

        val predictions = placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            mResultList = ArrayList(response.autocompletePredictions)
            notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            Log.e(TAG, "Error getting autocomplete prediction API call", exception)
        }

        return mResultList
    }
}