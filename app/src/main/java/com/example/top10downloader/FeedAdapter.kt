package com.example.top10downloader

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class ViewHolder(v: View) {
    val tvName = v.findViewById<TextView>(R.id.tvName)
    val tvArtist = v.findViewById<TextView>(R.id.tvArtist)
    val tvSummary = v.findViewById<TextView>(R.id.tvSummary)
}


class FeedAdapter(
    context: Context,
    private val resource: Int,
    private val applications: List<FeedEntry>
) : ArrayAdapter<FeedEntry>(
    context,    // context basically holds the state of the application or activity while it's running
    resource
) {
    private val inflater =
        LayoutInflater.from(context)     /* The inflater takes the xml resource available to it
                                            and populates the widgets in the UI
                                            (UI here is list_record.xml). Also here we pass in the
                                             context as a parameter */

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view: View
        val viewHolder: ViewHolder
        if (convertView == null) {
            view = inflater.inflate(resource, parent, false)
            viewHolder = ViewHolder(view)       //creating a viewHolder object and then
            view.tag = viewHolder       // storing it in the view's tag using the "set tag" method
        } else {
            view = convertView
            viewHolder = view.tag as ViewHolder     /* if we have been given back an existing view
                                                        by the ListView then we're retrieving
                                                        viewHolder from its tag using the "get tag"
                                                        method. Now the tag is an object so we have
                                                         to cast it to a ViewHolder. We're then
                                                         retrieving the application record from the
                                                         list as before and setting its values to
                                                         widgets that are stored in the viewHolder*/
        }

        val currentApp = applications[position]

        viewHolder.tvName.text = currentApp.name
        viewHolder.tvArtist.text = currentApp.artist
        viewHolder.tvSummary.text = currentApp.summary
        return view
    }

    override fun getCount(): Int {      /* If we don't override the getCount() method,
                                            the list view won't display any records since it needs
                                            to know how many items there are so that it can create
                                            the list of rows */
        return applications.size
    }
}