package com.example.top10downloader

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.top10downloader.R.id.*
import kotlinx.android.synthetic.main.activity_main.*
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name = ""
    var artist = ""
    var releaseDate = ""
    var summary = ""
    var imageURL = ""
    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            imageURL = $imageURL
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val FEED_URL: String = "http://ax.itunes.apple.com"
    private val FEED_LIMIT: Int = 10

    private var feedCachedUrl = "INVALIDATED"
    private val STATE_URL = "feedUrl"
    private val STATE_LIMIT = "feedLimit"

    var feedUrl: String =
        "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
    private var feedLimit = 10

    private var downloadData: DownloadData? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            feedUrl = savedInstanceState.getString(STATE_URL).toString()
            feedLimit = savedInstanceState.getInt(STATE_LIMIT)
        }

        downloadUrl(feedUrl.format(feedLimit))
        Log.d(TAG, "onCreate: done!")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.feeds_menu, menu)

        if (feedLimit == 10) {
            menu?.findItem(mnu10)?.isChecked = true     // menu is a nullable type so we use ?
        } else {
            menu?.findItem(mnu25)?.isChecked = true
        }
        return true
    }

    private fun downloadUrl(feedUrl: String) {
        if (feedUrl != feedCachedUrl) {
            Log.d(TAG, "downloadUrl starting AsyncTask")
            downloadData = DownloadData(this, xmlListView)
            downloadData?.execute(feedUrl)
            feedCachedUrl = feedUrl
            Log.d(TAG, "downloadUrl done!")
        } else {
            Log.d(TAG, "downloadUrl - URL not changed!")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            mnuFree -> {
                feedUrl =  "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml"
            }
            mnuPaid -> {
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml"
            }
            mnuSongs -> {
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml"
            }
            mnu10, mnu25 -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    feedLimit = 35 - feedLimit
                }
            }
            mnuRefresh -> {
                feedCachedUrl = "INVALIDATED"
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }

        downloadUrl(feedUrl.format(feedLimit))
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(
            context: Context,
            listView: ListView
        ) :
            AsyncTask<String, Void, String>() {

            private val TAG = "DownloadData"

            var propContext: Context by Delegates.notNull()
            var propListView: ListView by Delegates.notNull()

            init {
                propContext = context
                propListView = listView
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                val parseApplications =
                    ParseApplications()
                parseApplications.parse(result)

                val feedAdapter =
                    FeedAdapter(propContext, R.layout.list_record, parseApplications.applications)
                propListView.adapter = feedAdapter
            }

            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "doInBackground: starts with ${url[0]}")
                val rssFeed =
                    downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: Error downloading!")
                }
                return rssFeed
            }

            private fun downloadXML(urlPath: String?): String {
                return URL(urlPath).readText()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_URL, feedUrl)
        outState.putInt(STATE_LIMIT, feedLimit)
    }
}
