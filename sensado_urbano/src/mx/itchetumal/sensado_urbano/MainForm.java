package mx.itchetumal.sensado_urbano;

import java.io.File;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class MainForm extends Activity implements OnClickListener{
	ImageButton guardar;
	ImageButton cancelar;
	ImageButton tomarFoto;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private storageAlbum mAlbumStorageDirFactory = null;
	int cons = 0;
	EditText nombreCarpeta;
	File cLote;
	String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath()+"/bd/";
	AlertDialog alertDialog;
    
	   
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	//	requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_datos);
		nombreCarpeta = new EditText(this);
		guardar = (ImageButton)findViewById(R.id.imgbtnGuardar);
		cancelar = (ImageButton)findViewById(R.id.imgbtnCancelar);
		tomarFoto = (ImageButton)findViewById(R.id.imgTakePhoto);
		
		guardar.setOnClickListener(this);
		cancelar.setOnClickListener(this);
		tomarFoto.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imgbtnGuardar:
			Intent a = new Intent(getApplicationContext(), MainMapas.class);
			startActivity(a);
			overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
			break;
		case R.id.imgbtnCancelar:
			/*Intent intent =  new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(intent,cons);	
			*/
			break;
		case R.id.imgTakePhoto:
			
			cLote = new File(baseDir);
			if(!cLote.isDirectory()){
			   directory();
			}
			else{
				Toast.makeText(getApplicationContext(),"SI existe",Toast.LENGTH_LONG).show();                        
			}
			 final AlertDialog.Builder alert = new AlertDialog.Builder(this);
			 
			 alert.setTitle("Crear Carpeta");
			 alert.setMessage("A continuacion agrege el nombre de la carpeta del local del cual le tomara fotos");
			 alert.setView(nombreCarpeta);
			 alert.setIcon(R.drawable.ic_launcher);
			 alert.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String loteCarpeta;
					String srt = nombreCarpeta.getEditableText().toString();
					getAlbumName(srt);
					loteCarpeta = baseDir+srt;
					cLote = new File(loteCarpeta);
					cLote.mkdirs();
					cam();
					
					alertDialog.cancel();
					finish();
					Toast.makeText(getApplicationContext(),"Se creo la Carpeta"+ ": "+srt,Toast.LENGTH_LONG).show();                        
				}
			});
			alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
	        	  public void onClick(DialogInterface dialog, int whichButton) {
	        	      nombreCarpeta.setText("");
					  alertDialog.cancel();
					  alertDialog.dismiss();
	        	  }
	        });
			 alertDialog = alert.create();
			    alertDialog.show();
			break;
		}
		
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
		//	alertDialog.cancel();
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	public void directory(){
		  File wallpaperDirectory = new File(baseDir);
		  // have the object build the directory structure, if needed.
		  wallpaperDirectory.mkdirs();
	}
	public void cam(){
		Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
	    this.startActivity(intent);
	}
	
	public String getAlbumName(String nombre) {
		return nombre;
	}
	private File getAlbumDir() {
		File storageDir = null;
		String na = null;
		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName(na));

		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
}