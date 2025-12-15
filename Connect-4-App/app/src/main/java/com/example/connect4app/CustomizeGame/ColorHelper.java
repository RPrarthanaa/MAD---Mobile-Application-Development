package com.example.connect4app.CustomizeGame;

import com.example.connect4app.R;
import java.util.HashMap;
import java.util.Map;

public class ColorHelper{
    private static final Map<String, Integer> colorMap = new HashMap<>();

    static  {
        colorMap.put("Red", R.color.red);
        colorMap.put("Blue", R.color.blue);
        colorMap.put("Green", R.color.green);
        colorMap.put("Orange", R.color.orange);
        colorMap.put("Pink", R.color.pink);
        colorMap.put("Pale Pink", R.color.pale_pink);
        colorMap.put("Yellow", R.color.yellow);
        colorMap.put("Purple", R.color.purple);
        colorMap.put("Brown", R.color.brown);
    }

    public static int getColorResource(String color, int num) {
        Integer colorRes = colorMap.get(color);
        return (colorRes != null ? colorRes :  (num == 1 ? R.color.red: R.color.blue));
    }
}
