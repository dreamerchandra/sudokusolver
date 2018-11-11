package chandra.creative.jayavenkatesh;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;

public class PuzzleAdaper extends BaseAdapter {
    private char num[];
    private boolean filled;
    private Context mContext;
    private int temp_ui;
    @Override
    public int getCount() {
        return num.length;
    }

    @Override
    public Object getItem(int i) {
        return num[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        EditText text;
        if(filled) {
            if (view == null) {
                text = new EditText(mContext);
                text.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
                text.setPadding(8, 8, 8, 8);
                if((temp_ui%9==3 || temp_ui%9==4 || temp_ui%9==5) &&(temp_ui<26 || temp_ui>54)) {
                    text.setBackgroundResource(R.drawable.rectange);
                }
                else {
                    text.setBackgroundResource(R.drawable.rectange_red);
                }
                temp_ui++;
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            } else
                text = (EditText) view;
            Log.d("PuzzleAdapter",num[i]+" for "+i);
            if(num[i]!='.')
                text.setText(num[i]+"");
            else
                text.setText("");
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    changeText(editable,i);
                }
            });
            return text;
        }
        else
            return null;
    }
    PuzzleAdaper(Context context){
        num=new char[81];
        mContext=context;
        filled=false;
        temp_ui=0;
    }
    public void initPuzzle(char[] num){
        this.num=num;
        filled=true;
    }
    private void changeText(Editable editable, int i){
        Log.d("seting ",i+"");
        if(editable.toString().toCharArray().length!=0)
        num[i]=editable.toString().toCharArray()[0];
    }
}
