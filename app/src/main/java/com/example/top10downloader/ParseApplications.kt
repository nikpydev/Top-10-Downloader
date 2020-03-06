package com.example.top10downloader

import android.util.Log
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.util.*
import kotlin.collections.ArrayList

class ParseApplications {
    private val TAG = "ParseApplications"
    val applications =
        ArrayList<FeedEntry>()       // an ArrayList of type FeedEntry is created under the name of applications (since we're using top 10 apps from the apple rss feed)

    fun parse(xmlData: String): Boolean {
        Log.d(TAG, "parse called with $xmlData")
        var status = true       // status will be set to false if the try block below doesn't work
        var inEntry =
            false     // used to check if the current tag under consideration is <entry/> or not
        var textValue = ""      // used to hold the text within the tag (if any) under consideration

        try {
            val factory =
                XmlPullParserFactory.newInstance()        // XmlPullParserFactory contains a number of classes which have a capabilities of parsing XML so here and in the next two lines we are just trying to get any one which can parse XML
            factory.isNamespaceAware = true
            val xpp =
                factory.newPullParser()       // ultimately xpp (xml pull parser) is our forged weapon that we'll use for parsing the XML
            xpp.setInput(xmlData.reader())      // The XML content is given to the xpp for parsing. Now we have to exploit it
            var eventType =
                xpp.eventType       // eventType stores the situation like, whether it is the end of the document or the start of a tag or end of a tag etc.
            var currentRecord = FeedEntry()     // creates a new instance of FeedEntry() class
            while (eventType != XmlPullParser.END_DOCUMENT) {       // while we haven't reached the end of the document (XML data passed in by the MainActivity) do the following
                val tagName =
                    xpp.name?.toLowerCase(Locale.ROOT)      // save the name of the tag in tagName. "?" is a safe-call operator. It makes sure we don't call any function on a null type. Because xpp.name will be null initially.
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        if (tagName == "entry") {
                            inEntry = true
                        }
                    }

                    XmlPullParser.TEXT -> {
                        textValue = xpp.text
                    }

                    XmlPullParser.END_TAG -> {
                        if (inEntry) {
                            when (tagName) {
                                "entry" -> {
                                    applications.add(currentRecord)
                                    inEntry = false
                                    currentRecord = FeedEntry()     // Create a new object
                                }

                                "name" -> {
                                    currentRecord.name = textValue
                                }

                                "artist" -> {
                                    currentRecord.artist = textValue
                                }

                                "releasedate" -> {
                                    currentRecord.releaseDate = textValue
                                }

                                "summary" -> {
                                    currentRecord.summary = textValue
                                }

                                "image" -> {
                                    currentRecord.imageURL = textValue
                                }
                            }
                        }
                    }
                }
                // Nothing else to do
                eventType = xpp.next()
            }

//            for (app in applications) {
//                Log.d(TAG, "**************")
//                Log.d(TAG, app.toString())
//            }

        } catch (e: Exception) {
            e.printStackTrace()
            status = false
        }
        return status
    }
}