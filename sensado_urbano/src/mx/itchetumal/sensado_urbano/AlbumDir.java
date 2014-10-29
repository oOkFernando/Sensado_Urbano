package mx.itchetumal.sensado_urbano;

import java.io.File;

import android.os.Environment;

public final class AlbumDir extends storageAlbum {

	// Standard storage location for digital camera files
	private static final String CAMERA_DIR = "/bd/";

	public File getAlbumStorageDir(String albumName) {
		return new File (
				Environment.getExternalStorageDirectory()
				+ CAMERA_DIR
				+ albumName
		);
	}
}