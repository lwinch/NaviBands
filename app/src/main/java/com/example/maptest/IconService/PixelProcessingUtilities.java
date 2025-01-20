package com.example.maptest.IconService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Environment;
import android.util.Log;

import androidx.appcompat.content.res.AppCompatResources;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PixelProcessingUtilities {
    private static final String TAG = "PixelProcessingService";

    //get alpha pixels of Bitmap as a list
    public static ArrayList<Integer> getAlphaPixels(Bitmap a) {
        ArrayList<Integer> pixels = new ArrayList<>();
        for (int i = 0; i < a.getWidth(); i++) {
            for (int j = 0; j < a.getHeight(); j++) {
                pixels.add(Color.alpha(a.getPixel(i, j)));
            }
        }
        return pixels;
    }

    //get an alpha bitmap from a resource of size 100x100
    public static Bitmap getComparableBitmap(Context appContext, int resId) {
        //get drawable from resource id
        Drawable drawable = AppCompatResources.getDrawable(appContext, resId);
        assert drawable != null;
        drawable = drawable.mutate();
        return getComparableBitmap(drawable);
    }

    //get an alpha bitmap from a resource of size 100x100
    public static Bitmap getComparableBitmap(Drawable drawable) {
        //for storing result
        Bitmap bitmap;

        try {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        } catch (ClassCastException e) {
            VectorDrawable vd = (VectorDrawable) drawable;
            bitmap = Bitmap.createBitmap(vd.getIntrinsicWidth(),
                    vd.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        //return alpha bitmap of size 100x100
        return scaleBitmap(bitmap.extractAlpha(), bitmap.getWidth(), bitmap.getHeight(), 100f, 100f);
    }

    //resize bitmap
    public static Bitmap scaleBitmap(Bitmap cbOriginal, int width, int height, float newWidth, float newHeight) {
        //find scaling factors
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        //prepare transformation matrix
        Matrix matrix = new Matrix();
        matrix.setScale(scaleWidth, scaleHeight, width / 2f, height / 2f);
        //generate scaled bitmap
        return Bitmap.createBitmap(cbOriginal, 0, 0, width, height, matrix, true);
    }

    public static double cosineSimilarity(ArrayList<Integer> a, ArrayList<Integer> b) {
        int size = a.size();
        double similarity;
        double sumAB = 0, Asq = 0, Bsq = 0;

        for (int i = 0; i < size; i++) {
            //extract alpha value from pixel value
            int alphaA = a.get(i);
            int alphaB = b.get(i);
            //calculate and update cosine similarity factors
            sumAB += (alphaA * alphaB);
            Asq += (alphaA * alphaA);
            Bsq += (alphaB * alphaB);

        }
        //square root for denominator
        Asq = Math.sqrt(Asq);
        Bsq = Math.sqrt(Bsq);
        //calculate the similarity value
        similarity = (sumAB / (Asq * Bsq));

        return similarity;
    }

    //Create a File for saving an image or video
    private static File getOutputMediaFile(Context context) {
        //adding random suffix to filename to avoid collision
        long random = (int) (Math.random() % 1000);
        random += System.nanoTime();
        random %= 1000;
        if (random < 0) random = Math.abs(random);

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        //String directoryPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator;
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyy_HHmmssss", Locale.US).format(new Date());
        File mediaFile;
        String mImageName = timeStamp + "_" + random + ".PNG";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
//        mediaFile = new File(directoryPath+ mImageName);
        return mediaFile;
    }

    //Write the bitmap to filesystem as a image
    public static String storeImage(Bitmap image, Context context) {
        File pictureFile = getOutputMediaFile(context);
        if (pictureFile == null) {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return "File not created";
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 50, fos);
            fos.close();
            Log.i(TAG, "Image saved successfully");
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return pictureFile.getPath();
    }
}
