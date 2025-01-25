package com.example.yeezlemobileapp.utils

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import android.util.Log
import com.example.yeezlemobileapp.data.models.Track

class TrackAdapter(context: Context, resource: Int, private val trackList: List<Track>) :
    ArrayAdapter<Track>(context, resource, trackList), Filterable {

    private val allTracks = trackList.toMutableList()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                return try {
                    if (constraint.isNullOrEmpty()) {
                        filterResults.count = allTracks.size
                        filterResults.values = allTracks
                    } else {
                        val filteredList = allTracks.filter {
                            it.name.contains(constraint, ignoreCase = true)
                        }.take(3)
                        filterResults.count = filteredList.size
                        filterResults.values = filteredList
                    }
                    filterResults
                } catch (e: Exception) {
                    Log.e("TrackAdapter", "Error during filtering: ${e.message}", e)
                    filterResults.apply {
                        count = 0
                        values = emptyList<Track>()
                    }
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                try {
                    if (results?.values != null) {
                        clear()
                        @Suppress("UNCHECKED_CAST")
                        addAll(results.values as List<Track>)
                        notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    Log.e("TrackAdapter", "Error publishing results: ${e.message}", e)
                }
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return try {
            val view = super.getView(position, convertView, parent)
            val track = getItem(position)

            (view as? TextView)?.text = track?.name ?: "Unknown Track"
            view
        } catch (e: Exception) {
            Log.e("TrackAdapter", "Error getting view for position $position: ${e.message}", e)
            super.getView(position, convertView, parent)
        }
    }

    fun getCustomTrackName(input: String): Track? {
        return try {
            allTracks.find { it.name.equals(input, ignoreCase = true) }
        } catch (e: Exception) {
            Log.e("TrackAdapter", "Error finding track by name: ${e.message}", e)
            null
        }
    }
}