package com.example.maptest.IconService;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.maptest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IconService {
    private static final String TAG = "IconDataset";
    private  int[] datasetResources = null;
    private HashMap<Integer, ArrayList<Integer>> bitmapData = null;
    private HashMap<Integer, String> directionNames = null;

    public IconService(Context context) {
        loadDatasetResources();
        mapDirectionsWithResources();
        loadBitmapData(context);
    }


    private void loadBitmapData(Context context) {
        bitmapData = new HashMap<>();
        for (int res : datasetResources) {
            Bitmap bitmap = PixelProcessingUtilities.getComparableBitmap(context, res);
            ArrayList<Integer> alphaPixels = PixelProcessingUtilities.getAlphaPixels(bitmap);
            bitmapData.put(res, alphaPixels);
        }
    }

    private void loadDatasetResources() {
        if (datasetResources == null) {
            datasetResources = new int[]{
                    R.drawable.da_turn_arrive_right_svg,
                    R.drawable.da_turn_arrive_svg,
                    R.drawable.da_turn_depart_svg,
                    R.drawable.da_turn_ferry_svg,
                    R.drawable.da_turn_fork_right_svg,
                    R.drawable.da_turn_generic_merge_svg,
                    R.drawable.da_turn_generic_roundabout_svg,
                    R.drawable.da_turn_ramp_right_svg,
                    R.drawable.da_turn_right_svg,
                    R.drawable.da_turn_roundabout_1_svg,
                    R.drawable.da_turn_roundabout_2_svg,
                    R.drawable.da_turn_roundabout_3_svg,
                    R.drawable.da_turn_roundabout_4_svg,
                    R.drawable.da_turn_roundabout_5_svg,
                    R.drawable.da_turn_roundabout_6_svg,
                    R.drawable.da_turn_roundabout_7_svg,
                    R.drawable.da_turn_roundabout_8_svg,
                    R.drawable.da_turn_roundabout_exit_svg,
                    R.drawable.da_turn_sharp_right_svg,
                    R.drawable.da_turn_slight_right_svg,
                    R.drawable.da_turn_straight_svg,
                    R.drawable.da_turn_uturn_svg,

                    R.drawable.ic_alternate_route_svg,
                    R.drawable.ic_arrive_right_svg,
                    R.drawable.ic_roundabout_exit_svg,
                    R.drawable.ic_roundabout_left_svg,
                    R.drawable.ic_roundabout_right_svg,
                    R.drawable.ic_roundabout_sharp_left_svg,
                    R.drawable.ic_roundabout_sharp_right_svg,
                    R.drawable.ic_roundabout_slight_left_svg,
                    R.drawable.ic_roundabout_slight_right_svg,
                    R.drawable.ic_roundabout_straight_svg,
                    R.drawable.ic_roundabout_svg,
                    R.drawable.ic_roundabout_u_turn_svg,
                    R.drawable.ic_straight_svg,
                    R.drawable.ic_turn_right_svg,
                    R.drawable.ic_turn_sharp_right_svg,
                    R.drawable.ic_turn_slight_right_svg,
                    R.drawable.ic_u_turn_svg,

                    R.drawable.lane_normal_short_svg,
                    R.drawable.lane_normal_svg,
                    R.drawable.lane_sharp_short_svg,
                    R.drawable.lane_sharp_svg,
                    R.drawable.lane_slight_svg,
                    R.drawable.lane_slight_tall_svg,
                    R.drawable.lane_straight_svg,
                    R.drawable.lane_straight_tall_svg,
                    R.drawable.lane_stub_svg,
                    R.drawable.lane_uturn_short_svg,
                    R.drawable.lane_uturn_svg,
            };
        }

    }

    private void mapDirectionsWithResources() {
        directionNames = new HashMap<>();
        directionNames.put(R.drawable.da_turn_arrive_right_svg, Directions.ARRIVED);
        directionNames.put(R.drawable.da_turn_arrive_svg, Directions.ARRIVED);
        directionNames.put(R.drawable.da_turn_depart_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_fork_right_svg, Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_generic_roundabout_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_ramp_right_svg, Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_right_svg, Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_1_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_2_svg, Directions.RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_3_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_4_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_roundabout_5_svg, Directions.SLIGHT_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_6_svg, Directions.LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_7_svg, Directions.SHARP_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_8_svg, Directions.U_LEFT);
        directionNames.put(R.drawable.da_turn_roundabout_exit_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_sharp_right_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.da_turn_slight_right_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.da_turn_straight_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.da_turn_uturn_svg, Directions.U_LEFT);
        directionNames.put(R.drawable.ic_alternate_route_svg, Directions.ALTERNATE);
        directionNames.put(R.drawable.ic_arrive_right_svg, Directions.ARRIVED);
        directionNames.put(R.drawable.ic_roundabout_exit_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_left_svg, Directions.LEFT);
        directionNames.put(R.drawable.ic_roundabout_right_svg, Directions.RIGHT);
        directionNames.put(R.drawable.ic_roundabout_sharp_left_svg, Directions.SHARP_LEFT);
        directionNames.put(R.drawable.ic_roundabout_sharp_right_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.ic_roundabout_slight_left_svg, Directions.SLIGHT_LEFT);
        directionNames.put(R.drawable.ic_roundabout_slight_right_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.ic_roundabout_straight_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_roundabout_u_turn_svg, Directions.U_LEFT);
        directionNames.put(R.drawable.ic_straight_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.ic_turn_right_svg, Directions.RIGHT);
        directionNames.put(R.drawable.ic_turn_sharp_right_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.ic_turn_slight_right_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.ic_u_turn_svg, Directions.U_LEFT);
        directionNames.put(R.drawable.lane_normal_short_svg, Directions.RIGHT);
        directionNames.put(R.drawable.lane_normal_svg, Directions.RIGHT);
        directionNames.put(R.drawable.lane_sharp_short_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.lane_sharp_svg, Directions.SHARP_RIGHT);
        directionNames.put(R.drawable.lane_slight_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.lane_slight_tall_svg, Directions.SLIGHT_RIGHT);
        directionNames.put(R.drawable.lane_straight_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.lane_straight_tall_svg, Directions.STRAIGHT);
        directionNames.put(R.drawable.lane_uturn_short_svg, Directions.U_RIGHT);
        directionNames.put(R.drawable.lane_uturn_svg, Directions.U_RIGHT);
        directionNames.put(R.drawable.notification_icon, Directions.UNKNOWN);
        directionNames.put(0, Directions.UNKNOWN);

        //sort by value so that same directions remain together
        directionNames = sortDirectionNamesByValue(directionNames);
    }

    private HashMap<Integer, String> sortDirectionNamesByValue(HashMap<Integer, String> hm) {
        // Create a list from elements of HashMap
        List<Map.Entry<Integer, String>> list = new LinkedList<>(hm.entrySet());

        // Sort the list
        list.sort(Map.Entry.comparingByValue());

        // put data from sorted list to hashmap
        HashMap<Integer, String> temp = new LinkedHashMap<>();
        for (Map.Entry<Integer, String> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

    //get Direction from icons
    private int getMatchingIcon(Bitmap a) {

        //load data if not loaded
        //if (bitmapData==null)loadBitmapData();

        double maxSimilarity = 0, prevSimilarity = 0;
        int maxId = 0;
        String prevDirection = "";
        ArrayList<Integer> targetPixels = PixelProcessingUtilities.getAlphaPixels(a);

        //traverse array and compare
        for (Map.Entry<Integer, ArrayList<Integer>> res : bitmapData.entrySet()) {

            //get current direction name from current resId
            int currResId = res.getKey();
            String currDirection = directionNames.getOrDefault(currResId, Directions.UNKNOWN);
            ArrayList<Integer> currPixels = res.getValue();

            //calc similarity
            double val = PixelProcessingUtilities.cosineSimilarity(currPixels, targetPixels);

            Log.d(TAG, "comparing with: " + currDirection + " | " + val);

            if (val > maxSimilarity) {
                maxSimilarity = val;
                maxId = currResId;//for debugging

                double diff = Math.abs(val - prevSimilarity);
                if (diff >= 0 && diff <= 0.15d && currDirection != null && currDirection.equals(prevDirection) && maxSimilarity > 0.6) {
                    //if the previous similarity is almost same and the direction is also same
                    //no redundant comparison needed
                    Log.d(TAG, "contains: DirectionDetected >> " + currDirection);
                    return maxId;
                }

            }
            //store prev direction name and prev
            prevSimilarity = val;
            prevDirection = directionNames.get(currResId);
        }

        return maxId;
    }

    public int getDirection(Drawable drawable) {
        Log.d(TAG, "getDirection: Inside PixelProcessingService");
        Log.d(TAG, "getDirection: Starting processing of BITMAP");
        //Perform operations and calculate processing time
        long start_time = System.nanoTime();

        //convert icon to bitmap
        Bitmap currentBitmap = PixelProcessingUtilities.getComparableBitmap(drawable);
        int matchingRes = this.getMatchingIcon(currentBitmap);
//        String filename = storeImage(currentBitmap,context);
//        Log.d(TAG, "Stored >> "+title+" | "+text+" | "+direction + " | "+filename);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        Log.d(TAG, "getDirection: Processing time: " + difference + " ");

        return matchingRes;
    }

    public String getDirectionName(int iconRes) {
        return this.directionNames.get(iconRes);
    }
}


