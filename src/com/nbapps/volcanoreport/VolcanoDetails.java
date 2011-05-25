package com.nbapps.volcanoreport;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class VolcanoDetails extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        
        Intent intent = getIntent();

		String title = intent.getStringExtra("TITLE");
		String reportDate = intent.getStringExtra("REPORTDATE");
		@SuppressWarnings("unused")
		String newUnrest = new Boolean(intent.getBooleanExtra("NEWUNREST",false)).toString();
		Float latitudeDegrees = new Float((float)intent.getIntExtra("LATITUDE",0)/1000000);
		String latitude = latitudeDegrees.toString();
		Float longitudeDegrees = new Float((float)intent.getIntExtra("LONGITUDE",0)/1000000);
		String longitude = longitudeDegrees.toString();
		String description = intent.getStringExtra("DESCRIPTION");
		@SuppressWarnings("unused")
		String link = intent.getStringExtra("LINK");

        TextView titleView = (TextView)findViewById(R.id.title);
        titleView.setText(Html.fromHtml(title));
        
        TextView reportDateView = (TextView)findViewById(R.id.reportdate);
        reportDateView.setText(Html.fromHtml(reportDate));
        
        TextView locationView = (TextView)findViewById(R.id.location);
        locationView.setText(Html.fromHtml("Latitude: " + latitude + ", Longitude: " + longitude));
        
        TextView descriptionView = (TextView)findViewById(R.id.description);
        descriptionView.setText(Html.fromHtml(description));
        
    } 

}
