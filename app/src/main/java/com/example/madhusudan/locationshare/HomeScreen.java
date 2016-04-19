package com.example.madhusudan.locationshare;

import android.app.Activity;
import android.app.Activity.*;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import org.apache.http.client.HttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class HomeScreen extends Activity implements LocationListener{



    TextView tv,tv2,locationText,bigLocation;
    Criteria criteria;
    String errorToast="";
    ProgressDialog pd1;
    private LocationManager locationManager;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        tv= (TextView) findViewById(R.id.textView);
        tv2= (TextView) findViewById(R.id.textView2);
        locationText=(TextView)findViewById(R.id.textView4);
        bigLocation=(TextView)findViewById(R.id.textView3);
        locationText.setText("You are near to  ");

        SharedPreferences shared=getApplicationContext().getSharedPreferences("com.example.madhusudan.locationshare",getApplicationContext().MODE_PRIVATE);

        if(shared.getString("Username","").equals("")){
            Toast.makeText(getApplicationContext(),"sdsvs",Toast.LENGTH_LONG).show();
            Intent intent= new Intent(this,NameInit.class);
            startActivity(intent);
        }

        TextView bigLocation2=(TextView)findViewById(R.id.textView5);
        bigLocation2.append(" "+shared.getString("Username",""));

        ConnectivityManager cm= (ConnectivityManager)getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();

        if(cm.getActiveNetworkInfo()==null || !cm.getActiveNetworkInfo().isConnected()){
            Toast.makeText(getApplicationContext(),"Connect to internet please",Toast.LENGTH_LONG).show();
            tv.setText("No location");
            tv2.setText("No location");
        }
        else {
            // Toast.makeText(getApplicationContext(),"Hello World!",Toast.LENGTH_LONG).show();


            LocationManager locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

            criteria=new Criteria();
            locationManager.requestLocationUpdates(locationManager.getBestProvider(criteria,false), 0, 0, this);
            Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria,false));


            if (loc == null) {
                locationText.setText("You are #@ to  ");
                tv.setText("No loc");
               tv2.setText("No loc");
            }
            else {
                tv.setText(Double.toString(loc.getLatitude()));
                tv2.setText(Double.toString(loc.getLongitude()));
                String api="http://api.geonames.org/findNearbyJSON?formatted=true&lat=" + tv.getText() + "&lng=" + tv2.getText() + "&fclass=P&fcode=PPLA&fcode=PPL&fcode=PPLC&username=madhusudan&style=full";
                locationText.append(api);
                onLocationChanged(loc);

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
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
    public void onLocationChanged(Location location) {
        Toast.makeText(getApplicationContext(),"Loc:"+location.getLatitude(),Toast.LENGTH_LONG).show();

        tv.setText(Double.toString(location.getLatitude()));
        tv2.setText(Double.toString(location.getLongitude()));
        //locationText.setText("You are near to  ");
        locationText.setText("");;
        final String uri="http://api.geonames.org/findNearbyJSON?";
        final String formatted="formatted";
        final String lat="lat";
        final String lng="lng";
        final String fclass="fclass";
        //fclass=P&fcode=PPLA&fcode=PPL&fcode=PPLC&username=madhusudan&style=full
        final  String fcode="fcode";
        final  String fcodec="fcode";
        final  String fcodea="fcode";
        final String username="username";
        final String style="style";

        String formateedval="true";
        String fclassval="P";
        String fcodeval="PPL";
        String fcodecval="PPLC";
        String fcodeaval="PPLA";
        String usernameVal="madhusudan";
        String styleval="full";
        String latText=tv.getText().toString();
        String lngTExt=tv2.getText().toString();

        Uri uri1=Uri.parse(uri).buildUpon().appendQueryParameter(formatted,formateedval).
                appendQueryParameter(lat,latText).appendQueryParameter(lng,lngTExt).
                appendQueryParameter(fclass,fclassval).appendQueryParameter(fcodea,fcodeaval)
                .appendQueryParameter(fcode,fcodeval).appendQueryParameter(fcodec,fcodecval)
                .appendQueryParameter(username,usernameVal).appendQueryParameter(style,styleval).build();

        locationText.setText(uri1.toString());

        //locationText.append("http://api.geonames.org/findNearbyJSON?formatted=true&lat=" + tv.getText().toString() + "&lng=" + tv2.getText().toString() + "&fclass=P&fcode=PPLA&fcode=PPL&fcode=PPLC&username=madhusudan&style=full");
        RestURLJSONCreator creator=new RestURLJSONCreator();
        creator.execute(uri1.toString());


       // / onLocationChanged(location);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(),"Enabled",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        locationManager=(LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 6000, 1, this);


    }



    private class RestURLJSONCreator extends AsyncTask<String ,Void,Void>{

        String resp="";
        ProgressDialog dialog=new ProgressDialog(HomeScreen.this);



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
                errorToast=e.toString();
                //Toast.makeText(getApplicationContext(),"Exception:"+e,Toast.LENGTH_LONG).show();
                bigLocation.setText(errorToast);
                Log.d("Loc",e.toString());

            }






            return null;
        }

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Loading..",Toast.LENGTH_LONG).show();
            dialog.setMessage("Loading location");
            dialog.show();


        }

        @Override
        protected void onPostExecute(Void aVoid) {

          //  Toast.makeText(getApplicationContext(),"Error:"+errorToast,Toast.LENGTH_LONG).show();
            bigLocation=(TextView)findViewById(R.id.textView3);
            //bigLocation.setText(errorToast);
            JSONObject jsonObject;
            try {
                jsonObject= new JSONObject(resp);
                String name = (String)jsonObject.getJSONArray("geonames").getJSONObject(0).getString("asciiName");
               // bigLocation.setText(resp);
                bigLocation.setText(name);
                dialog.dismiss();


            }
            catch (Exception e){
                bigLocation.setText(e.toString());
                dialog.dismiss();
            }


        }
    }


}
