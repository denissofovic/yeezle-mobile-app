package com.example.yeezlemobileapp.utils

import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.example.yeezlemobileapp.activities.GameActivity
import com.example.yeezlemobileapp.data.models.Track

class TrackAdapter(context: GameActivity, resource: Int, private val trackList: List<Track>) :
    ArrayAdapter<Track>(context, resource, trackList), Filterable {

    private val allTracks = trackList.toMutableList()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (constraint.isNullOrEmpty()) {
                    filterResults.count = allTracks.size
                    filterResults.values = allTracks
                } else {
                    val filteredList = allTracks.filter { it.name.contains(constraint, ignoreCase = true) }.take(3)
                    filterResults.count = filteredList.size
                    filterResults.values = filteredList
                }

                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    clear()
                    addAll(results.values as List<Track>)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
        val view = super.getView(position, convertView, parent)
        val track = getItem(position)

        (view as TextView).text = track?.name.toString()
        return view
    }

    fun getCustomTrackName(input: String): Track? {
        return allTracks.find { it.name.equals(input, ignoreCase = true) }
    }
}

