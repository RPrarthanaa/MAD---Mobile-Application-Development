package com.example.connect4app.CustomizeUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.example.connect4app.R;
import java.util.Objects;

public class ViewPagerAdapterUser extends PagerAdapter {

    int imagePosition = 0;

    // Context object
    Context context;

    // Array of images
    int[] images;

    // Layout inflater
    LayoutInflater layoutInflater;

    // Added to check
    ImageView imageView;

    // Viewpager constructor
    public ViewPagerAdapterUser(Context context, int[] images) {
        this.context = context;
        this.images = images;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // Return the number of images
    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        // Inflating the item.xml
        View itemView = layoutInflater.inflate(R.layout.activity_avatar_slideshow, container, false);

        // Referencing the image view from the item.xml file
        imageView = (ImageView) itemView.findViewById(R.id.imageViewMain);

        // Setting the image in the imageView
        imageView.setImageResource(images[position]);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);
        imagePosition = position;
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }

    public int getImage()
    {
        return images[imagePosition];
    }
}