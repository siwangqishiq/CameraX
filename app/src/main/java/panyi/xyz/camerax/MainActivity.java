package panyi.xyz.camerax;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
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

    private MyLocationListener mLocation;

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

        mLocation = new MyLocationListener();
        getLifecycle().addObserver(mLocation);
        getLifecycle().addObserver(new OwnWidget());
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
        if(imageCapture == null)
            return;

        final File photoFile = new File(outputDirectory ,
                new SimpleDateFormat(FILENAME_FORMAT).format(System.currentTimeMillis()) +".jpeg");

        final ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(photoFile).build();
        imageCapture.takePicture(options, ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        String msg = "Photo capture succeeded " + photoFile.getAbsolutePath();
                        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, msg);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "拍摄图片失败: " + exception.getMessage());
                    }
                });
    }

    //start preview
    public void startCamera(){
        final ListenableFuture<ProcessCameraProvider> cameraProviderFuture
                = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(()->{
            ProcessCameraProvider cameraProvider = null;
            try {
                cameraProvider = cameraProviderFuture.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if(cameraProvider == null)
                return;

            imageCapture = new ImageCapture.Builder().build();

            final Preview preview = new Preview.Builder().build();

            PreviewView previewView = findViewById(R.id.viewFinder);
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            try{
//                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;
                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this , cameraSelector , preview , imageCapture);
            }catch (Exception e){
                e.printStackTrace();
            }
        } , ContextCompat.getMainExecutor(this));
    }

    @Override
    protected void onDestroy() {
        cameraExecutor.shutdown();
        super.onDestroy();
    }
}