package com.example.madhusudan.locationshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;


public class MainActivityHome extends Activity implements LocationListener  {


    Criteria criteria;
    String errorToast="";
    ProgressDialog pd1;
    TextView bigLocation;
    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_home);

        SharedPreferences shared=getApplicationContext().getSharedPreferences("com.example.madhusudan.locationshare",getApplicationContext().MODE_PRIVATE);

        if(shared.getString("Username","").equals("")){
          //  Toast.makeText(getApplicationContext(), "sdsvs", Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this,NameInit.class);
            startActivity(intent);
        }


        Button sender=(Button)findViewById(R.id.button3);


        String address;
        ConnectivityManager manager= (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();



        LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        criteria=new Criteria();
        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria, false), 0, 0, this);
        Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        if(loc== null){
            Toast.makeText(getApplicationContext(),"Trying to get location. Try again.",Toast.LENGTH_LONG).show();

            //onLocationChanged(loc);
            

        }
        else{
            //Toast.makeText(getApplicationContext(),loc.toString(),Toast.LENGTH_LONG).show();
            onLocationChanged(loc);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_home, menu);
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

    @Override
    protected void onResume() {
        super.onResume();
        criteria=new Criteria();
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        //LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
       // locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, this);
        //locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria,false),30000,10,this);
    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onLocationChanged(Location location) {


        double longitude= location.getLongitude();

        double latitude = location.getLatitude();
        String latlng = Double.toString(latitude);
        latlng = latlng.concat(",");
        latlng=latlng.concat(Double.toString(longitude));

        Uri uri= Uri.parse("http://maps.googleapis.com/maps/api/geocode/json?").buildUpon().appendQueryParameter("latlng",latlng).build();

       // Toast.makeText(getApplicationContext(),uri.toString(),Toast.LENGTH_LONG).show();
        RestURLJSONCreator creator=new RestURLJSONCreator();
        creator.execute(uri.toString());
    }







    private class RestURLJSONCreator extends AsyncTask<String ,Void,Void> {

        String resp="";
        ProgressDialog dialog=new ProgressDialog(MainActivityHome.this);

        @Override
        protected Void doInBackground(String... params) {

            try {
                URL url = new URL(params[0]);
                URLConnection connection=url.openConnection();


                //  connection.setDoOutput(true);
                //  OutputStreamWriter streamWriter=new OutputStreamWriter(connection.getOutputStream());
                //  streamWriter.write(resp);
                //  streamWriter.flush();


                BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer sbuffer=new StringBuffer();
                while(true){
                    String temp=bufferedReader.readLine();
                    if(temp==null){
                        break;
                    }
                    sbuffer.append(temp);
                }
                resp=sbuffer.toString();

            }
            catch (Exception e){
              //  errorToast=e.toString();
              //  Toast.makeText(getApplicationContext(),"Exception:"+e,Toast.LENGTH_LONG).show();

                Log.d("Loc", e.toString());

            }






            return null;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
         //   Toast.makeText(getApplicationContext(),"Loading..",Toast.LENGTH_LONG).show();
            dialog.setMessage("Loading location");
            dialog.show();


        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //bigLocation=(TextView)findViewById(R.id.);
            //  Toast.makeText(getApplicationContext(),"Error:"+errorToast,Toast.LENGTH_LONG).show();
            bigLocation=(TextView)findViewById(R.id.textView8);
            //bigLocation.setText(errorToast);
            JSONObject jsonObject;
            try {
                jsonObject= new JSONObject(resp);
                String name = (String)jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                // bigLocation.setText(resp);
                bigLocation.setText(name);
               // Toast.makeText(getApplicationContext(),name,Toast.LENGTH_LONG);
                dialog.dismiss();


            }
            catch (Exception e){
                bigLocation.setText(e.toString());
                dialog.dismiss();
            }


        }
    }
    public void sendData(View v){
        SharedPreferences sharedP= getApplicationContext().getSharedPreferences("com.example.madhusudan.locationshare",getApplicationContext().MODE_PRIVATE);
        Intent it=new Intent();
        it.setAction(Intent.ACTION_SEND);
        it.putExtra(Intent.EXTRA_TEXT,sharedP.getString("Username","")+" is near\n "+ bigLocation.getText().toString());
        it.setType("text/plain");
        startActivity(Intent.createChooser(it,"Share Locationv via"));
    }

}
