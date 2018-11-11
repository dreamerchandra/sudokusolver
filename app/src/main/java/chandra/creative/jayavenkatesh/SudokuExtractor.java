package chandra.creative.jayavenkatesh;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

class SudokuExtractor {
    private Mat puzzle;
    private Bitmap puzzle_bm;
    private Context context;
    SudokuExtractor(Bitmap bm, Context context){
        puzzle=new Mat();
        puzzle_bm=bm;
        Bitmap bmp32 = bm.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bmp32, puzzle);
        this.context = context;
    }
    private String processTextRecognitionResult(FirebaseVisionText text){
        List<FirebaseVisionText.Block> blocks = text.getBlocks();
        if(blocks.size()!=0)
        Log.d("Block size",blocks.size()+"");
        if (blocks.size() == 0) {
            return ".";
        }
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    if(blocks.size()!=0) {
                        Log.d("Rec text", elements.get(k).getBoundingBox().flattenToString());
                        Log.d("Detec text", elements.get(k).getText());
                    }
                    return elements.get(k).getText();
                }
            }
        }
        return ".";
    }
    private String no;
    private void getNum(Mat image){
        Point[] h_p=new Point[10];
        Point[] v_p=new Point[10];
        h_p[0]= new Point(0,0);
        v_p[0]=new Point(0,0);

        Bitmap masked = null;
        for (int i=0;i<9;i++) {
            h_p[i+1] = new Point(0, (i + 1) * image.cols() / 9);
            v_p[i+1]=new Point((i+1)*image.width()/9,0);
        }
        Mat canvas=null;
        for (int i=0;i<9;i++){
            for (int j=0;j<9;j++){
                canvas = Mat.zeros(image.rows(),image.cols(),image.type());
                Imgproc.rectangle(canvas,
                        new org.opencv.core.Point(v_p[j].x-20,h_p[i].y-20),
                        new org.opencv.core.Point(v_p[j+1].x+20,h_p[i+1].y+20),
                        new Scalar(255,255,255),
                        -1);
                Core.bitwise_and(image,canvas,canvas);
                masked=Bitmap.createBitmap(image.cols(),image.rows(),Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(canvas, masked);
                FirebaseVisionImage vimage = FirebaseVisionImage.fromBitmap(masked);
                FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();
                detector.detectInImage(vimage)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText text) {
                                String temp = processTextRecognitionResult(text);
                                if(!(temp.toLowerCase().toCharArray()[0]>'a'&& temp.toLowerCase().toCharArray()[0]<'z'))
                                no+=temp;
                                else
                                    no+='.';
                                if(no.length()==81){
                                    Intent intent = new Intent(context,PuzzleActivity.class);
                                    intent.putExtra("no",no);
                                    context.startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Error in detection   ",e.getMessage());
                            }
                        });
            }
        }
    }
    public void getNum(){
        if(!puzzle.empty()){
            no="";
            getNum(puzzle);
        }
    }
}
