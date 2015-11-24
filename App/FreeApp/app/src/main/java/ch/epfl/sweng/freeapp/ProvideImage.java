package ch.epfl.sweng.freeapp;

import android.graphics.Bitmap;

/**
 * Created by francisdamachi on 18/11/15.
 */
public class ProvideImage {

    private static   Bitmap bitmap;

    public static void setImage(Bitmap that){
        bitmap = that;

    }
    public static Bitmap getImage(){
        if(bitmap == null ){
            throw new NullPointerException("empty image ");
        }

        return bitmap;
    }

}
