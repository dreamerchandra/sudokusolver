package chandra.creative.jayavenkatesh;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.Toast;

public class PuzzleActivity extends AppCompatActivity {
    private char[] sampleGrid;
    private PuzzleAdaper adaper;
    GridView gridview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);
        gridview = (GridView) findViewById(R.id.gridview);
        Bundle b=getIntent().getExtras();
        sampleGrid=new char[81];
//        sampleGrid="..3.2.6..9..3.5..1..18.64....81.29..7.......8..67.82....26.95..8..2.3..9..5.1.3..".toCharArray();
        sampleGrid = b.getString("no").toCharArray();
        adaper = new PuzzleAdaper(this);
        adaper.initPuzzle(sampleGrid.clone());
        gridview.setAdapter(adaper);
    }
    private boolean isFull(char[] chars){
        for (char aChar : chars) {
            if (aChar == '.')
                return false;
        }
        return true;
    }
    private int getTrialCelli(char[] chars){
        for (int i=0;i<chars.length;i++){
            if(chars[i]=='.'){
                return i;
            }
        }
        return -1;
    }
    private boolean isLegal(int trialVal,int trialCelli,char[] grid){
        int col=0;
        for (int eachSq=0;eachSq<9;eachSq++){
            int[] trialSq=new int[9];
            for (int i=0;i<3;i++){
                trialSq[i]=i+col;
            }
            for (int i=0;i<3;i++){
                trialSq[i+3]=i+col+9;
            }for (int i=0;i<3;i++){
                trialSq[i+6]=i+col+18;
            }
            col+=3;
            if (col==9 || col==36) {
                col += 18;
            }
            for (int i=0;i<trialSq.length;i++){
                if(trialCelli==trialSq[i]){
                    for (int j=0;j<trialSq.length;j++){
                        if(grid[trialSq[j]]!='.'){
                            if(trialVal==Integer.parseInt(String.valueOf(grid[trialSq[j]]))){
                                Log.e("In solvig",Integer.parseInt(String.valueOf(grid[trialSq[j]]))+" "+j);
                                return false;
                            }
                        }
                    }
                    i=trialSq.length+10;
                }
            }
        }
        for (int eachRow=0;eachRow<9;eachRow++){
            int[] trialRow = new int[9];
            for (int i=0;i<9;i++){
                trialRow[i]=i+(9*eachRow);
            }
            boolean flag=false;
            for (int i=0;i<trialRow.length;i++){
                if(trialCelli==trialRow[i]) {
                    flag = true;
                    i=trialRow.length+10;
                }
            }
            if (flag){
                for (int i=0;i<trialRow.length;i++){
                    if(grid[trialRow[i]]!='.'){
                        Log.e("In solvig",Integer.parseInt(String.valueOf(grid[trialRow[i]]))+""+i);
                        if(trialVal==Integer.parseInt(String.valueOf(grid[trialRow[i]]))){
                            return false;
                        }
                    }
                }
            }
        }
        for (int eachCol=0;eachCol<9;eachCol++){
            int[] trialCol=new int[9];
            for (int i=0;i<9;i++){
                trialCol[i]=(9*i)+eachCol;
            }
            boolean flag=false;
            for (int i=0;i<trialCol.length;i++){
                if(trialCelli==trialCol[i]){
                    flag=true;
                    i=trialCol.length+10;
                }
            }
            if(flag){
                for (int i:trialCol){
                    if(grid[i]!='.'){
                        if(trialVal==Integer.parseInt(grid[i]+"")){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    private char[] setCell(int val,int cell,char[] grid){
        grid[cell]=(char)(val+'0');
        return grid;
    }
    private char[] clearCell(int cell,char[] grid){
        grid[cell]='.';
        return grid;
    }
    private boolean hasSolution(char[] grid){
        if (isFull(grid)){
            Log.e("In solving","Solved");
            sampleGrid=grid;
            for (char aGrid : grid) Log.d("In solving", aGrid + "");
            return true;
        }
        else {
            int trialCelli = getTrialCelli(grid);
            int trialVal = 1;
            boolean solution_found = false;
            while (!solution_found && trialVal < 10){
                if (isLegal(trialVal,trialCelli,grid)){
                    grid=setCell(trialVal,trialCelli,grid);
                    if(hasSolution(grid)) {
                        solution_found = true;
                        return true;
                    }
                    else {
                        grid=clearCell(trialCelli,grid);
                    }
                }
                trialVal++;
            }
            return solution_found;
        }
    }
    public void solve(View view){
        if (hasSolution (sampleGrid)){
            adaper.initPuzzle(sampleGrid);
            adaper.notifyDataSetChanged();
        }
        else
            Toast.makeText(this,"Can't find solution",Toast.LENGTH_SHORT).show();
    }

    public void update(View view) {
        StringBuilder no= new StringBuilder();
        for (int i=0;i<adaper.getCount();i++)
            no.append(adaper.getItem(i));
        sampleGrid=no.toString().toCharArray();
        adaper.initPuzzle(sampleGrid.clone());
        adaper.notifyDataSetChanged();
    }
}
