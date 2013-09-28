package org.brenckmann.java.android.app.msurllinkproxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

/**
 *	Activity to handle Microsoft URL Links
 *  ... you know - that kind of links being created by 
 *  dragging urls from a browser to the windows desktop.
 *   
 * 	@author Dirk Brenckmann
 */
public class MSURLLinkProxy extends Activity  {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

        Toast.makeText( this, "Proxy for Microsoft URL Link called.", Toast.LENGTH_SHORT ).show();
        
        Intent		intent	= this.getIntent();
		Uri			uri		= intent.getData();
		InputStream is		= null;
		try {
			  is = this.getContentResolver().openInputStream( uri );
			  InputStreamReader ir		 = new InputStreamReader( is );
			  BufferedReader    br		 = new BufferedReader( ir );
			  String 			strLine  = null;
			  String			url 	 = null;
			  boolean 			checkurl = false;
			  while (( strLine = br.readLine()) != null )   {
				   	   if ( strLine.toLowerCase( Locale.US ).equals( "[internetshortcut]" )) {
				   		   	// Next line should contain "URL=...."
					        checkurl = true;
				       }
					   else if ((checkurl) && (strLine.startsWith("URL="))) {
						    url = strLine.substring( 4 );
						   	break;
					   }
					   else checkurl = false;
			  }
			  
			  if ( url != null ) {
				   // Convert incoming intent to new browsing intent
				   Intent browseIntent = new Intent( Intent.ACTION_VIEW );
				   browseIntent.setData( Uri.parse( url ));
				   startActivity( browseIntent );
			  }
			  else Toast.makeText( this, "Not a Microsoft URL Link", Toast.LENGTH_SHORT ).show();
			  // We do not try to resend the intent to avoid circular calls...
		} 
		catch ( Exception e ) {
				// Error 0001: Probably failed to open stream from ContentProvider
			    this.error( "Error 0001" , e );
		}
		finally {
			  if ( is != null ) {
				   try { is.close(); }
				   catch ( Exception e ) { this.error( "Error 0002" , e ); }
				   // Error 0002: Failed closing InputStream 'is'
			  }
		}
	}
    private void error( String msg,  Exception e ) {
    	Log.e( this.getClass().getName(), msg + " - " + e.getMessage(), e );
    }

}
