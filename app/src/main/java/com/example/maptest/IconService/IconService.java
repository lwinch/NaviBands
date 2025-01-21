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
    private HashMap<Integer, String> iconData;
    private HashMap<String, Direction> directionData;
    private List<IconData> bitmapData = null;

    public IconService(Context context) {
        mapDirectionsWithResources();
        mapDirectionData();
        loadBitmapData(context);
    }

    private void loadBitmapData(Context context) {
        bitmapData = new ArrayList<>();
        for (int res : iconData.keySet()) {
//            Log.d(TAG, "res: " + res);
            Bitmap bitmap = PixelProcessingUtilities.getComparableBitmap(context, res);
            ArrayList<Integer> alphaPixels = PixelProcessingUtilities.getAlphaPixels(bitmap);
            bitmapData.add(new IconData(res, alphaPixels));
        }
    }

    private void mapDirectionsWithResources() {
        iconData = new HashMap<>();
        iconData.put(R.drawable.da_turn_arrive_right_svg, Directions.ARRIVED);
        iconData.put(R.drawable.da_turn_arrive_svg, Directions.ARRIVED);
        iconData.put(R.drawable.da_turn_depart_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.da_turn_fork_left_svg, Directions.LEFT);
        iconData.put(R.drawable.da_turn_fork_right_svg, Directions.RIGHT);
        iconData.put(R.drawable.da_turn_generic_roundabout_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.da_turn_ramp_right_svg, Directions.RIGHT);
        iconData.put(R.drawable.da_turn_left_svg, Directions.LEFT);
        iconData.put(R.drawable.da_turn_right_svg, Directions.RIGHT);
        iconData.put(R.drawable.da_turn_roundabout_1_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.da_turn_roundabout_2_svg, Directions.RIGHT);
        iconData.put(R.drawable.da_turn_roundabout_3_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.da_turn_roundabout_4_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.da_turn_roundabout_5_svg, Directions.SLIGHT_LEFT);
        iconData.put(R.drawable.da_turn_roundabout_6_svg, Directions.LEFT);
        iconData.put(R.drawable.da_turn_roundabout_7_svg, Directions.SHARP_LEFT);
        iconData.put(R.drawable.da_turn_roundabout_8_svg, Directions.U_LEFT);
        iconData.put(R.drawable.da_turn_roundabout_exit_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.da_turn_sharp_left_svg, Directions.SHARP_LEFT);
        iconData.put(R.drawable.da_turn_sharp_right_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.da_turn_slight_left_svg, Directions.SLIGHT_LEFT);
        iconData.put(R.drawable.da_turn_slight_right_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.da_turn_straight_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.da_turn_uturn_svg, Directions.U_LEFT);

        iconData.put(R.drawable.ic_alternate_route_svg, Directions.ALTERNATE);
        iconData.put(R.drawable.ic_arrive_right_svg, Directions.ARRIVED);
        iconData.put(R.drawable.ic_roundabout_exit_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.ic_roundabout_left_svg, Directions.LEFT);
        iconData.put(R.drawable.ic_roundabout_right_svg, Directions.RIGHT);
        iconData.put(R.drawable.ic_roundabout_sharp_left_svg, Directions.SHARP_LEFT);
        iconData.put(R.drawable.ic_roundabout_sharp_right_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.ic_roundabout_slight_left_svg, Directions.SLIGHT_LEFT);
        iconData.put(R.drawable.ic_roundabout_slight_right_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.ic_roundabout_straight_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.ic_roundabout_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.ic_roundabout_u_turn_svg, Directions.U_LEFT);
        iconData.put(R.drawable.ic_straight_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.ic_turn_left_svg, Directions.LEFT);
        iconData.put(R.drawable.ic_turn_right_svg, Directions.RIGHT);
        iconData.put(R.drawable.ic_turn_sharp_left_svg, Directions.SHARP_LEFT);
        iconData.put(R.drawable.ic_turn_sharp_right_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.ic_turn_slight_left_svg, Directions.SLIGHT_LEFT);
        iconData.put(R.drawable.ic_turn_slight_right_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.ic_u_turn_svg, Directions.U_LEFT);

        iconData.put(R.drawable.lane_normal_short_svg, Directions.RIGHT);
        iconData.put(R.drawable.lane_normal_svg, Directions.RIGHT);
        iconData.put(R.drawable.lane_sharp_short_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.lane_sharp_svg, Directions.SHARP_RIGHT);
        iconData.put(R.drawable.lane_slight_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.lane_slight_tall_svg, Directions.SLIGHT_RIGHT);
        iconData.put(R.drawable.lane_straight_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.lane_straight_tall_svg, Directions.STRAIGHT);
        iconData.put(R.drawable.lane_uturn_short_svg, Directions.U_RIGHT);
        iconData.put(R.drawable.lane_uturn_svg, Directions.U_RIGHT);
        iconData.put(R.drawable.notification_icon, Directions.UNKNOWN);
        //iconData.put(0, Directions.UNKNOWN);

        //sort by value so that same directions remain together
        iconData = sortDirectionNamesByValue(iconData);
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

    private void mapDirectionData() {
        directionData = new HashMap<>();
        directionData.put(Directions.ARRIVED, new Direction(Directions.ARRIVED, "Arrived", "\uD83D\uDCCD"));
        directionData.put(Directions.STRAIGHT, new Direction(Directions.STRAIGHT, "Straight", "️⬆️"));

        directionData.put(Directions.SHARP_RIGHT, new Direction(Directions.SHARP_RIGHT, "Sharp right", "↘️"));
        directionData.put(Directions.RIGHT, new Direction(Directions.RIGHT, "Right", "➡️"));
        directionData.put(Directions.SLIGHT_RIGHT, new Direction(Directions.SLIGHT_RIGHT, "Slight right", "↗️"));
        directionData.put(Directions.U_RIGHT, new Direction(Directions.U_RIGHT, "U-turn right", "↩️"));

        directionData.put(Directions.SLIGHT_LEFT,  new Direction(Directions.SLIGHT_LEFT, "Slight Left", "↖️"));
        directionData.put(Directions.LEFT, new Direction(Directions.LEFT, "Left", "⬅️"));
        directionData.put(Directions.SHARP_LEFT, new Direction(Directions.SHARP_LEFT, "Sharp left", "↙️"));
        directionData.put(Directions.U_LEFT, new Direction(Directions.U_LEFT, "U-turn left", "↪️"));

        directionData.put(Directions.ALTERNATE, new Direction(Directions.ALTERNATE, "Alternate route", "❗"));
        directionData.put(Directions.UNKNOWN, new Direction(Directions.UNKNOWN, "Unknown", "❗"));
    }

    //get Direction from icons
    private IconData getMatchingIcon(Bitmap a) {
        double maxSimilarity = 0;//, prevSimilarity = 0;
        IconData bestMatch = null;
        //String prevDirection = "";
        ArrayList<Integer> targetPixels = PixelProcessingUtilities.getAlphaPixels(a);

        //traverse array and compare
        for (IconData iconData : bitmapData) {
            //get current direction name from current resId
            String currDirection = this.iconData.getOrDefault(iconData.getResId(), Directions.UNKNOWN);

            //calc similarity
            double val = PixelProcessingUtilities.cosineSimilarity(iconData.getBitmapData(), targetPixels);

            Log.d(TAG, "comparing with: " + currDirection + " | " + val);

            if (val > maxSimilarity) {
                maxSimilarity = val;
                bestMatch = iconData; //for debugging
                bestMatch.setDirection(this.directionData.get(currDirection));

//                double diff = Math.abs(val - prevSimilarity);
//                if (diff >= 0 && diff <= 0.15d && currDirection != null && currDirection.equals(prevDirection) && maxSimilarity > 0.6) {
//                    //if the previous similarity is almost same and the direction is also same
//                    //no redundant comparison needed
//                    Log.d(TAG, "contains: DirectionDetected >> " + currDirection);
//                    return bestMatch;
//                }
            }
            //store prev direction name and prev
            //prevSimilarity = val;
            //prevDirection = this.iconData.get(iconData.getResId());
        }
        if (bestMatch != null) {
            Log.d(TAG, "contains: DirectionDetected >> " + bestMatch.getDirection().getLongName());
        }
        return bestMatch;
    }

    public IconData getDirection(Drawable drawable) {
        Log.d(TAG, "getDirection: Inside IconService");
        Log.d(TAG, "getDirection: Starting processing of BITMAP");
        //Perform operations and calculate processing time
        long start_time = System.nanoTime();

        //convert icon to bitmap
        Bitmap currentBitmap = PixelProcessingUtilities.getComparableBitmap(drawable);
        IconData matchingRes = this.getMatchingIcon(currentBitmap);
        //String filename = storeImage(currentBitmap,context);
        //Log.d(TAG, "Stored >> "+title+" | "+text+" | "+direction + " | "+filename);

        long end_time = System.nanoTime();
        double difference = (end_time - start_time) / 1e6;
        Log.d(TAG, "getDirection: Processing time: " + difference + " seconds");

        return matchingRes;
    }
}


