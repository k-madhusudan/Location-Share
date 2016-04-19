package com.example.madhusudan.locationshare;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class NameInit extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_init);
        Button button = (Button) findViewById(R.id.button);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_name_init, menu);
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

    public void gotoHome(View view){

        EditText editText = (EditText) findViewById(R.id.editText);

        if(!editText.getText().toString().equals("")) {
            SharedPreferences shared = getApplicationContext().getSharedPreferences("com.example.madhusudan.locationshare", getApplicationContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("Username", editText.getText().toString());
            editor.commit();

            Toast.makeText(getApplicationContext(),"Enter bfbdfb",Toast.LENGTH_LONG).show();

            startActivity(new Intent(this,MainActivityHome.class));
        }
        else{
            Toast.makeText(getApplicationContext(),"Enter name",Toast.LENGTH_LONG).show();
            //startActivity(new Intent(this, NameInit.class));
        }
    }
}
