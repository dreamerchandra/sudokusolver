package chandra.creative.jayavenkatesh;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "OCVSample::Activity";
    private CameraBridgeViewBase mOpenCvCameraView;
    private boolean sudokuReg;
    private Bitmap puzzle;
    Button RL,RR,scan,recapture;
    ProgressBar progressBar;

    // These variables are used (at the moment) to fix camera orientation from 270degree to 0degree
    Mat mRgba;
    Mat mRgbaF;
    Mat mRgbaT;
    Mat mIntermediateMat;
    ImageView imageView;
    SudokuExtractor extractor;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    imageView.setVisibility(View.INVISIBLE);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = (JavaCameraView) findViewById(R.id.show_camera_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        FirebaseOptions options = new FirebaseOptions.Builder().setApplicationId("chandra.creative.sudokusolver").build();
        FirebaseApp.initializeApp(this, options);


        mOpenCvCameraView.setCvCameraViewListener(this);
        imageView=findViewById(R.id.image);
        RR = findViewById(R.id.rotate_right);
        RL = findViewById(R.id.rotate_left);
        sudokuReg=false;
        RR.setVisibility(View.INVISIBLE);
        RL.setVisibility(View.INVISIBLE);
        progressBar= findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        scan=findViewById(R.id.button);
        recapture=findViewById(R.id.button2);
        recapture.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
        changeUI();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        mRgbaF = new Mat(height, width, CvType.CV_8UC4);
        mRgbaT = new Mat(width, width, CvType.CV_8UC4);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
        mIntermediateMat.release();
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // TODO Auto-generated method stub
        mRgba = inputFrame.rgba();
        // Rotate mRgba 90 degrees
        Core.transpose(mRgba, mRgbaT);
        Imgproc.resize(mRgbaT, mRgbaF, mRgbaF.size(), 0,0, 0);
        Core.flip(mRgbaF, mRgba, 1 );
        PuzzleFinder finder = new PuzzleFinder(mRgba);
        return finder.getPuzzle(false);
        // This function must return
    }


    public void scan(View view) {
        RR.setVisibility(View.VISIBLE);
        RL.setVisibility(View.VISIBLE);
        recapture.setVisibility(View.VISIBLE);
        if(!sudokuReg) {
            Log.d("Scanning", "In scanner");
            Log.d("Channel",mRgba.channels()+"");
            Mat source = mRgba;
            PuzzleFinder finder = new PuzzleFinder(source);
            Mat m = finder.getPuzzle(true);
            Mat temp = m.clone();
            Bitmap bm = Bitmap.createBitmap(temp.cols(), temp.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(temp, bm);
            sudokuReg=true;
            puzzle=bm;
            mOpenCvCameraView.disableView();
            mOpenCvCameraView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bm);
            scan.setText(R.string.Extract);
        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            extractor = new SudokuExtractor(puzzle,this);
            extractor.getNum();
        }
    }
    private void changeUI(){
        mOpenCvCameraView.setVisibility(View.VISIBLE);
        mOpenCvCameraView.enableView();
        imageView.setVisibility(View.INVISIBLE);
        sudokuReg=false;
        RR.setVisibility(View.INVISIBLE);
        RL.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        recapture.setVisibility(View.INVISIBLE);
        scan.setText(R.string.Scan);
    }
    public void recapture(View view) {
        changeUI();
    }

    public void rotateLeft(View view) {
        Matrix mat = new Matrix();
        mat.postRotate(90);
        puzzle=Bitmap.createBitmap(puzzle,0,0,puzzle.getWidth(),puzzle.getHeight(),mat,true);
        imageView.setImageBitmap(puzzle);
    }

    public void rotateRight(View view) {
        Matrix mat = new Matrix();
        mat.postRotate(-90);
        puzzle=Bitmap.createBitmap(puzzle,0,0,puzzle.getWidth(),puzzle.getHeight(),mat,true);
        imageView.setImageBitmap(puzzle);
    }
}
