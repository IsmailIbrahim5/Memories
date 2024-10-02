package com.idea.memories.Classes;

import android.content.Context;
import android.util.Log;import com.idea.memories.R;


public class ColorsGenerator {
    public final String red;
    public final String lightRed;
    public final String yellow;
    public final String lightYellow;
    public final String green;
    public final String lightGreen;
    public final String blue;
    public final String lightBlue;
    public final String deleted;

    public ColorsGenerator(Context context){
        red = String.format("#%06X" , 0xFFFFFF & context.getResources().getColor(R.color.red));
        lightRed = String.format("#%06X" , context.getResources().getColor(R.color.light_red));
        yellow = String.format("#%06X" , 0xFFFFFF & context.getResources().getColor(R.color.yellow));
        lightYellow = String.format("#%06X" , context.getResources().getColor(R.color.light_yellow));
        green = String.format("#%06X" , 0xFFFFFF & context.getResources().getColor(R.color.green));
        lightGreen = String.format("#%06X" ,context.getResources().getColor(R.color.light_green));
        blue = String.format("#%06X" , 0xFFFFFF & context.getResources().getColor(R.color.blue));
        lightBlue = String.format("#%06X" ,context.getResources().getColor(R.color.light_blue));
        deleted = String.format("#%06X" , 0xFFFFFF & context.getResources().getColor(R.color.deleted));
    }
    /*
        public static final String blue = "#4486f3";
    public static final String green = "#119f5b";
    public static final String yellow = "#ffce41";
    public static final String red ="#dd4d3e";

     */

    public String getHex (String color){
        String hexs = "";
        Log.e(getClass().getName() , color);
        switch (color){
            case "blue" : hexs =  blue; break;
            case "red" : hexs = red; break;
            case "yellow" : hexs =  yellow; break;
            case "green" : hexs = green ; break;
        }
        return hexs;
    }

    public String getColor (String hex){
        String color = null;
        if(hex.equalsIgnoreCase(blue))
            color = "blue";
        else if(hex.equalsIgnoreCase(red))
            color = "red";
        else if(hex.equalsIgnoreCase(yellow))
            color = "yellow";
        else if(hex.equalsIgnoreCase(green))
            color = "green";
        Log.e(getClass().getName() , color);
        return color;
    }

    public String getLightColor (String hex){
        String color = null;
        if(hex.equalsIgnoreCase(blue))
            color = lightBlue;
        else if(hex.equalsIgnoreCase(red))
            color = lightRed;
        else if(hex.equalsIgnoreCase(yellow))
            color = lightYellow;
        else if(hex.equalsIgnoreCase(green))
            color = lightGreen;
        Log.e(getClass().getName() , color);
        return color;
    }

    public String next(String color){
        if (color.equalsIgnoreCase(red))
            return yellow;
        else if (color.equalsIgnoreCase(yellow))
            return green ;
        else if (color.equalsIgnoreCase(green))
            return blue;
        else if (color.equalsIgnoreCase(blue))
            return red;
        else
            return null;
    }

    public String previous(String color){
        if (color.equalsIgnoreCase(red))
            return blue;
        else if (color.equalsIgnoreCase(yellow))
            return red ;
        else if (color.equalsIgnoreCase(green))
            return yellow;
        else if (color.equalsIgnoreCase(blue))
            return green;
        else
            return null;
    }
}
