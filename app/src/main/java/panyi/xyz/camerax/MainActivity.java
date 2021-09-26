package panyi.xyz.camerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "CameraXBasic";
    public static final String FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS";
    public static final int REQUEST_CODE_PERMISSIONS = 10;
    private static String[] REQUIRED_PERMISSIONS = {Manifest.permission.CAMERA};

    private ImageCapture imageCapture;
    private File outputDirectory;
    private ExecutorService cameraExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(allPermissionsGranted()){
            startCamera();
        }else{
            ActivityCompat.requestPermissions(
                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        outputDirectory = getOutputDirectory();
        findViewById(R.id.camera_capture_button).setOnClickListener((v)->{
            takePhoto();
        });
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    private boolean allPermissionsGranted(){
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getBaseContext() , permission)
                    != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_PERMISSIONS ){
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "请给我拍摄的权限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private File getOutputDirectory(){
        File[] mediaDirs = getExternalMediaDirs();
        if(mediaDirs != null && mediaDirs.length > 0){
            File mediaFile = mediaDirs[0];
            return mediaFile;
        }
        return getFilesDir();
    }

    public void takePhoto(){

    }

    public void startCamera(){

    }

    @Override
    protected void onDestroy() {
        cameraExecutor.shutdown();
        super.onDestroy();
    }
}