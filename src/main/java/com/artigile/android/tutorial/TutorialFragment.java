package com.artigile.android.tutorial;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.artigile.android.R;
import com.artigile.android.game.GameActivity;

/**
 * @author ivanbahdanau
 */
public class TutorialFragment extends Fragment {

    private ViewGroup rootView;
    private ImageView ivImage;
    private LinearLayout nextPageImg;
    private TextView textView;
    private Integer imageId = R.drawable.game_preview;
    private Integer textId = R.string.tutorial_page_1;
    private Bitmap myBitmap;
    private Button goToGameButton;
    private boolean displayNextPageImg = true;

    public static TutorialFragment newInstance() {
        return new TutorialFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tutorial_slides, container, false);
        ivImage = (ImageView) rootView.findViewById(R.id.imageView);
        nextPageImg = (LinearLayout) rootView.findViewById(R.id.nextPage);
        textView = (TextView) rootView.findViewById(R.id.tutorialDescriptionText);
        goToGameButton = (Button) rootView.findViewById(R.id.takeToTheGameButton);
        goToGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                getActivity().startActivity(intent);
            }
        });
        setDataInViewPager();
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (myBitmap != null) {
            myBitmap.recycle();
            myBitmap = null;
        }
    }


    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }


    public void setTextId(Integer textId) {
        this.textId = textId;
    }

    public void setDisplayNextPageImg(boolean displayNextPageImg) {
        this.displayNextPageImg = displayNextPageImg;
    }

    public void setDataInViewPager() {
        try {
            //if image size is too large. Need to scale as below code.
            displayImage();
            textView.setText(textId);
            setNextButton();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            System.gc();
        }
    }

    private void setNextButton() {
        if (displayNextPageImg) {
            nextPageImg.setVisibility(View.VISIBLE);
            goToGameButton.setVisibility(View.GONE);
        } else {
            nextPageImg.setVisibility(View.GONE);
            goToGameButton.setVisibility(View.VISIBLE);
        }
    }

    private void displayImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        if (imageId == null) {
            imageId = R.drawable.game_preview;
        }
        myBitmap = BitmapFactory.decodeResource(getResources(), imageId,
                options);
        if (options.outWidth > 3000 || options.outHeight > 2000) {
            options.inSampleSize = 4;
        } else if (options.outWidth > 2000 || options.outHeight > 1500) {
            options.inSampleSize = 3;
        } else if (options.outWidth > 1000 || options.outHeight > 1000) {
            options.inSampleSize = 2;
        }
        options.inJustDecodeBounds = false;
        myBitmap = BitmapFactory.decodeResource(getResources(), imageId,
                options);
        if (myBitmap != null) {
            try {
                if (ivImage != null) {
                    ivImage.setImageBitmap(myBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
