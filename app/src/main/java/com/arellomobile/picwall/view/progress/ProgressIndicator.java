package com.arellomobile.picwall.view.progress;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ProgressIndicator {
    protected final ProgressBar progressBar;
    private final TextView progressText;

    private String prefixText = "Loading ";
    private String postfixText = "%";

    public ProgressIndicator(ProgressBar progressBar, TextView progressText){
        this.progressBar = progressBar;
        this.progressText = progressText;
        setMax(100);
    }

    public void setMax(int max){
        progressBar.setMax(max);
    }

    public void show(){
        progressBar.setVisibility(View.VISIBLE);
        progressText.setVisibility(View.VISIBLE);
    }

    public void hide(){
        progressBar.setVisibility(View.INVISIBLE);
        progressText.setVisibility(View.INVISIBLE);
    }
    public void setPrefixPostixTexts(String prefix, String postfix){
        prefixText = prefix;
        postfixText = postfix;
    }

    public void setProgress(int value){
        progressBar.setProgress(value);
        if(value<=0){
            progressText.setText(prefixText);
        }
        else{
            progressText.setText(prefixText+value+postfixText);
        }
    }
}
