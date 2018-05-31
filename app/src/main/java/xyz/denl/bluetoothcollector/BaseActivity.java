package xyz.denl.bluetoothcollector;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * Created by tw on 2017-09-28.
 */

public class BaseActivity extends AppCompatActivity {

    public void println(Object o){
        System.out.println(o);
    }

    public void setFadeInAnimation(View view){
        Animation animation = new AlphaAnimation(0, 1);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void setFadeOutAnimation(View view){
        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);
        view.setAnimation(animation);
    }

    public void redirectStartPage(){
        Intent intent = new Intent(getApplicationContext(), StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void showSnackbar(String msg){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), msg, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public void showSnackbar(int id){
        Snackbar snackbar = Snackbar.make(getWindow().getDecorView().getRootView(), getResources().getString(id), Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.snackbar_color));
        snackbar.show();
    }

    public int getColorId(int id){
        return ContextCompat.getColor(getApplicationContext(), id);
    }



    public void setStatusColor(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        }
    }

    public void setStatusColorDark(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        }
    }

}
