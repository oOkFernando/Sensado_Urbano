package mx.itchetumal.sensado_urbano;

import java.util.concurrent.CopyOnWriteArrayList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

 public class MainMapas extends Activity implements OnMapClickListener, OnMapLongClickListener, OnMarkerClickListener, LocationListener {
	final int RQS_GooglePlayServices = 1;
	private GoogleMap map;
	
	Location myLoc;
	LocationManager lo;
	
	TextView tvLocinf;
	boolean marketClicked;
	PolygonOptions polygonops;
	Polygon polygon;
	Marker ap;
	MarkerOptions drop;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_two);
		setUpMap();
		tvLocinf = (TextView)findViewById(R.id.tex);
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.m, menu);
		return true;
	}
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) public void setUpMap() {
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
		if(status!=ConnectionResult.SUCCESS){
			int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
		}else{
		
			if(map == null){
				map  = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				if(map != null){
					map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
					map.setMyLocationEnabled(true);
				}
			}
			lo = (LocationManager) getSystemService(LOCATION_SERVICE);
			Criteria criteria = new Criteria();
			String provider = lo.getBestProvider(criteria, true);
			myLoc = lo.getLastKnownLocation(provider);
			if(myLoc!=null){
                onLocationChanged(myLoc);
            }
            lo.requestLocationUpdates(provider, 20000, 0, this);
      
			FragmentManager myfrag = getFragmentManager();
			MapFragment myFragment = (MapFragment)myfrag.findFragmentById(R.id.map);
			map = myFragment.getMap();
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			map.setMyLocationEnabled(true);
		//	map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myLoc.getLatitude(), myLoc.getLongitude())));
			map.setOnMapClickListener(this);
			map.setOnMapLongClickListener(this);
			map.setOnMarkerClickListener(this);
			marketClicked = false;	
		}
	}
	public void onLocationChanged(Location loc) {
		double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.animateCamera(CameraUpdateFactory.zoomTo(15));
 
	}
	@Override
	public boolean onMarkerClick(Marker mark) {
		// TODO Auto-generated method stub
		if(marketClicked){
			if(polygon != null){
				polygon.remove();
				polygon = null;
			}
			polygonops.add(mark.getPosition());
			polygonops.strokeColor(Color.RED);
			polygonops.fillColor(Color.BLACK);
			polygon = map.addPolygon(polygonops);
		}else{
			if(polygon != null){
				polygon.remove();
				polygon=null;
			}
			polygonops = new PolygonOptions().add(mark.getPosition());
			marketClicked = true;
		}
		return true;
	}

	@Override
	public void onMapLongClick(LatLng point) {
		// TODO Auto-generated method stub
	
		tvLocinf.setText("Nuevo Markador agregado @" + point.toString());
		map.addMarker(new MarkerOptions()
			.position(point)
			.draggable(true)
			.title(point.toString()));
		marketClicked = false;
		
		
	}
	public void onMarkerDrag(Marker marker){
		tvLocinf.setText("Markador"+marker.getId()+ "Drageado @"+marker.getPosition());
	}
	public void onMarkerDragEnd(Marker marker){
		tvLocinf.setText("Marker " + marker.getId() + " DragEnd");
	}
	public void onMarkerDragStart(Marker marker){
		tvLocinf.setText("Marker " + marker.getId() + " DragStart");
	}
	@Override
	public void onMapClick(LatLng point) {
		// TODO Auto-generated method stub
		tvLocinf.setText(point.toString());
		map.animateCamera(CameraUpdateFactory.newLatLng(point));
		marketClicked = false;
	}

	@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	  switch (item.getItemId()) {
	     case R.id.action_settings:
	      String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(
	        getApplicationContext());
	      AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MainMapas.this);
	      LicenseDialog.setTitle("Legal Notices");
	      LicenseDialog.setMessage(LicenseInfo);
	      LicenseDialog.show();
	         return true;
	     }
	  return super.onOptionsItemSelected(item);
	 }

	 @Override
	 protected void onResume() {

	  super.onResume();

	  int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
	  
	  if (resultCode == ConnectionResult.SUCCESS){
	   Toast.makeText(getApplicationContext(), 
	     "isGooglePlayServicesAvailable SUCCESS", 
	     Toast.LENGTH_LONG).show();
	  }else{
	   GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
	  }
	  
	 }

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
