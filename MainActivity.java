package com.example.android.weatherapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity  {

private String city="London";
private String country="United Kingdom";
	String url1="http://api.openweathermap.org/data/2.5/weather?q=";
	String url2="&mode=xml";
    String iurl1="http://openweathermap.org/img/w/";
    String iurl2=".png";
    public void setCity(Geocoder gcd,Location location)
    {
        try {
            Log.d("sriram","Inside try");
            List<Address> addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            for (Address adrs : addresses) {
                if (adrs != null) {

                    city = adrs.getLocality();
                    country=adrs.getCountryName();
                    if (!(city != null && !city.equals(""))) {
                        Log.d("sriram","City Not found");
                        Toast t=Toast.makeText(getApplicationContext(),"City Not Found",Toast.LENGTH_LONG);
                        t.show();
                    }
                   else{
                        Log.d("sriram","City found "+city);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void cityupdate()
    {
        final LocationManager locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        final String provider = locationManager.getBestProvider(criteria, false);
        final Geocoder gcd = new Geocoder(getApplicationContext(), Locale.getDefault());
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                LocationListener l=new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location1) {

                        Log.d("sriram","Loc update");
                        setCity(gcd,location1);

                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {

                    }

                    @Override
                    public void onProviderEnabled(String provider) {

                    }

                    @Override
                    public void onProviderDisabled(String provider) {

                    }
                };
                locationManager.requestLocationUpdates(provider,0,0,l);

                Location location = locationManager.getLastKnownLocation(provider);
                if(location!=null) {
                    Log.d("sriram","Inside if");
                    setCity(gcd,location);
                }
                Log.d("sriram","out if");


            }
        };
        runnable.run();
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void Temp(View view){
        cityupdate();
        ImageView imageView=(ImageView)findViewById(R.id.imageView);
        DownloadImageTask imageTask=new DownloadImageTask(imageView);
        com.example.android.weatherapp.HandleXML handleXML=new com.example.android.weatherapp.HandleXML(url1+city+url2);
		TextView textView2=(TextView)findViewById(R.id.textView2);
		Log.d("sriram","line1");
		handleXML.fetchXML();
		Log.d("sriram","line2");
	    while(handleXML.parsingComplete);
		Log.d("sriram","line3");
		textView2.setText(handleXML.getTemperature());
        imageTask.execute(iurl1+handleXML.getImageicon()+iurl2);

        TextView textView=(TextView)findViewById(R.id.textView);
        textView.setText(city+'\n'+country);
        Log.d("sriram","line4");
	}

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        cityupdate();

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
