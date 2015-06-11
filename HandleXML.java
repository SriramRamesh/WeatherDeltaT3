package com.example.android.weatherapp;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DELL on 07-06-2015.
 */
public class HandleXML {
    private String urlString = null;
    private String country = "country";
    private String temperature = "temperature";
    private String humidity = "humidity";
    private String pressure = "pressure";
    private XmlPullParserFactory xmlFactoryObject;
    public volatile boolean parsingComplete = true;
    public String Imageicon;

    public HandleXML(String urlString) {
        this.urlString = urlString;
    }
    public String getTemperature(){
        return temperature;
    }
    public String getImageicon(){
        return Imageicon;
    }
    public void fetchXML(){
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection conn=(HttpURLConnection)url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream stream = conn.getInputStream();
                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    parseXMLAndStoreIt(myparser);
                    stream.close();

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }
    public void parseXMLAndStoreIt(XmlPullParser myParser) {
        int event;
        String text=null;

        try {
            event = myParser.getEventType();
            String temp;
            double f,f1;
            while (event != XmlPullParser.END_DOCUMENT) {
                String name=myParser.getName();

                switch (event){
                    case XmlPullParser.START_TAG:
                        break;

                    case XmlPullParser.TEXT:
                        text = myParser.getText();
                        break;

                    case XmlPullParser.END_TAG:
                        if(name.equals("country")){
                            country = text;
                        }


                        else if(name.equals("temperature")){
                            Log.d("sriram","found temperature tag");
                             temperature = myParser.getAttributeValue(null,"max");
                             f=1.0;
                             f=Double.valueOf(temperature)-273.15;
                             temperature=String.format("%.2f",f);
                              temp=myParser.getAttributeValue(null,"min");
                            f1=2.0;
                            f1=Double.valueOf(temp)-273.15;
                            temp=String.format("%.2f",f);
                            if(f!=f1)
                                temperature=temperature+'/'+temp;
                             temperature=temperature+"°C";
                        }
                        else if(name.equals("weather"))
                        {
                            Imageicon=myParser.getAttributeValue(null,"icon");
                        }

                        else{
                        }
                        break;
                }
                event = myParser.next();
            }
            parsingComplete = false;
        }

        catch (Exception e) {
            Log.d("sriram",e.toString());
        }
    }
};

