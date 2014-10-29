package mx.itchetumal.sensado_urbano;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;




public class MainActivity extends Activity
{
	Dialog customDialog = null;
	Httppostaux post;
	String IP_Server="192.168.10.161";//IP DE NUESTRO PC
	String URL_connect="http://"+IP_Server+"/~gregorio/Lotes/acceso.php";//ruta en donde estan nuestros archivos
	boolean result_back;
	private ProgressDialog pDialog;
    String usuario;
	String contraseña;
	EditText us;
	EditText pas;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);	
		// con este tema personalizado evitamos los bordes por defecto
				customDialog = new Dialog(this,R.style.Theme_Dialog_Translucent);
				//deshabilitamos el tÃ­tulo por defecto
				customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				//obligamos al usuario a pulsar los botones para cerrarlo
				customDialog.setCancelable(false);
				//establecemos el contenido de nuestro dialog
				customDialog.setContentView(R.layout.dialog_login);
				us = (EditText) customDialog.findViewById(R.id.user);
				pas= (EditText)customDialog.findViewById(R.id.pass);
				
				TextView titulo = (TextView) customDialog.findViewById(R.id.titulo);
				titulo.setText("Login");
				post=new Httppostaux();
			    ((Button) customDialog.findViewById(R.id.aceptar)).setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View view)
					{
						usuario = us.getText().toString();
						contraseña = pas.getText().toString();
						if((checklogindataUser(usuario)==true)&&(checklogindataNip(contraseña)==true)){

		        			//si pasamos esa validacion ejecutamos el asynctask pasando el usuario y clave como parametros
						    new asynclogin().execute(usuario,contraseña);        		               
		        			      		
		        		
		        		}else if( (checklogindataUser(usuario)==true)&&(checklogindataNip(contraseña)==false)){
		        			//si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
		        			Toast toast1 = Toast.makeText(getApplicationContext(),"Campo Usuario Vacio", Toast.LENGTH_SHORT);
		             	    toast1.show();
		        		}else if( (checklogindataUser(usuario)==false)&&(checklogindataNip(contraseña)==true)){
		        			//si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
		        			Toast toast1 = Toast.makeText(getApplicationContext(),"Campo Contase–a Vacio", Toast.LENGTH_SHORT);
		             	    toast1.show();
		        		}else {
		        			//si detecto un error en la primera validacion vibrar y mostrar un Toast con un mensaje de error.
		        			Toast toast1 = Toast.makeText(getApplicationContext(),"Campos Vacios", Toast.LENGTH_SHORT);
		             	    toast1.show();
		             	   Log.e("Error", "Campos Vacios");
		        		}
		        		
		        		/*if(!usuario.equals("")&&!passw.equals("")){
						customDialog.dismiss();
					    finish();
					    Intent asd = new Intent(getApplicationContext(),MainForm.class);
					    startActivity(asd);
					    overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
						Toast.makeText(MainActivity.this, R.string.aceptar, Toast.LENGTH_SHORT).show();
		        		}*/
					}
				});
				customDialog.show();
	}
	//Validar numControl y Nip
	public boolean checklogindataUser(String username){	  	
	  if(username.equals("")){
	   	 Log.e("Error", "Campo NumControl Vacio");
	     return false;
	   }else{
		return true;
	   }
	  } 
	//validamos si no hay ningun campo en blanco
	public boolean checklogindataNip(String password ){
		if(password.equals("")){
	  	  Log.e("Error", "Campo NIP vacio");
	     return false;
	     }else{
	  	  return true;
	    }    
	   }
	  /*Valida el estado del logueo solamente necesita como parametros el usuario y passw*/
    public boolean loginstatus(String username ,String password ) {
    	int logstatus=-1;
    	
    	/*Creamos un ArrayList del tipo nombre valor para agregar los datos recibidos por los parametros anteriores
    	 * y enviarlo mediante POST a nuestro sistema para relizar la validacion*/ 
    	ArrayList<NameValuePair> postparameters2send= new ArrayList<NameValuePair>();
     		
		    		postparameters2send.add(new BasicNameValuePair("usuario",username));
		    		postparameters2send.add(new BasicNameValuePair("password",password));

		   //realizamos una peticion y como respuesta obtenes un array JSON
      		JSONArray jdata=post.getserverdata(postparameters2send, URL_connect);

      		/*como estamos trabajando de manera local el ida y vuelta sera casi inmediato
      		 * para darle un poco realismo decimos que el proceso se pare por unos segundos para poder
      		 * observar el progressdialog
      		 * la podemos eliminar si queremos
      		 */
		    SystemClock.sleep(950);
		    		
		    //si lo que obtuvimos no es null
		    	if (jdata!=null && jdata.length() > 0){

		    		JSONObject json_data; //creamos un objeto JSON
					try {
						json_data = jdata.getJSONObject(0); //leemos el primer segmento
						 logstatus=json_data.getInt("logstatus");//accedemos al valor 
						 Log.e("loginstatus","logstatus= "+logstatus);//muestro por log que obtuvimos
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		            
		             
					//validamos el valor obtenido
		    		 if (logstatus==0){// [{"logstatus":"0"}] 
		    			 Log.e("loginstatus ", "invalido");
		    			 return false;
		    		 }
		    		 else{// [{"logstatus":"1"}]
		    			 Log.e("loginstatus ", "valido");
		    			 return true;
		    		 }
		    		 
			  }else{	//json obtenido invalido verificar parte WEB.
		    			 Log.e("JSON  ", "ERROR");
			    		return false;
			  }
    	
    }
    class asynclogin extends AsyncTask< String, String, String > {
   	 
    	String user,pass;
        protected void onPreExecute() {
			//customDialog.dismiss();
        	//para el progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Autenticando....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
		protected String doInBackground(String... params) {
			//obtnemos usr y pass
			user=params[0];
			pass=params[1];
            
			//enviamos y recibimos y analizamos los datos en segundo plano.
    		if (loginstatus(user,pass)==true){    		    		
    			return "ok"; //login valido
    		}else{    		
    			return "err"; //login invalido     	          	  
    		}
        	
		}
       
		/*Una vez terminado doInBackground segun lo que halla ocurrido 
		pasamos a la sig. activity
		o mostramos error*/
        protected void onPostExecute(String result) {

           pDialog.dismiss();//ocultamos progess dialog.
           Log.e("onPostExecute=",""+result);
           
           if (result.equals("ok")){
				Intent i=new Intent(getApplicationContext(), MainForm.class);
				startActivity(i); 
				
            }else{
                Toast toast1 = Toast.makeText(getApplicationContext(),"Usuario no dado de alta", Toast.LENGTH_SHORT);
         	    toast1.show(); 
            }
       	}
		
    }		
}
