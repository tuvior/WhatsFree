package ch.epfl.sweng.freeapp;

import android.graphics.Bitmap;

/**
 * Class which lets us provide an image. This greatly simplifies testing. Due to the
 * the ability to inject an image.
 */
public class ProvideImage {


    private static   Bitmap bitmap;
    private static ImageType TYPE_OF_IMAGE = ImageType.FROM_PHONE;

    public static void setImage(Bitmap that){
        bitmap = that;

    }
    public static Bitmap getImage(){


        return bitmap;
    }



    public enum ImageType{

        FROM_PHONE,FROM_TEST

    }

    public void setTypeOfImage(ImageType imageType){
        TYPE_OF_IMAGE = imageType;
    }

    public ImageType getTypeOfImage(){
        return TYPE_OF_IMAGE;
    }




}
