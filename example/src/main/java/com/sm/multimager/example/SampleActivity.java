package com.sm.multimager.example;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.sm.multimager.activities.BaseActivity;
import com.sm.multimager.activities.GalleryActivity;
import com.sm.multimager.activities.MultiCameraActivity;
import com.sm.multimager.adapters.GalleryImagesAdapter;
import com.sm.multimager.utils.Constants;
import com.sm.multimager.utils.Image;
import com.sm.multimager.utils.Params;
import com.sm.multimager.utils.Utils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by vansikrishna on 08/06/2016.
 */
public class SampleActivity extends BaseActivity {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.parentLayout)
    LinearLayout parentLayout;
    int selectedColor;
    int darkenedColor;
    @Bind(R.id.multiCaptureButton)
    Button multiCaptureButton;
    @Bind(R.id.multiPickerButton)
    Button multiPickerButton;
    @Bind(R.id.customThemeButton)
    Button customThemeButton;
    @Bind(R.id.call)
    ImageView callImageView;
    @Bind(R.id.message)
    ImageView messageImageView;
    @Bind(R.id.contact_us)
    TextView contactUsTextView;
    @Bind(R.id.app_name)
    TextView appNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample);
        ButterKnife.bind(this);
        selectedColor = fetchAccentColor();
        darkenedColor = Utils.getDarkColor(selectedColor);
        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                customThemeButton,
                multiCaptureButton,
                multiPickerButton,
                callImageView,
                messageImageView,
                contactUsTextView,
                appNameTextView);
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
                handleResponseIntent(intent);
                break;
            case Constants.TYPE_MULTI_PICKER:
                handleResponseIntent(intent);
                break;
        }
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailDpWidth = getResources().getDimension(R.dimen.thumbnail_width) / displayMetrics.density;
        return (int) (dpWidth / thumbnailDpWidth);
    }

    private void handleResponseIntent(Intent intent) {
        ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(getColumnCount(), GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(mLayoutManager);
        GalleryImagesAdapter imageAdapter = new GalleryImagesAdapter(this, imagesList, getColumnCount(), new Params());
        recyclerView.setAdapter(imageAdapter);
    }

    @OnClick({R.id.multiCaptureButton, R.id.multiPickerButton, R.id.customThemeButton, R.id.github})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.multiCaptureButton:
                if (Utils.hasCameraHardware(this))
                    initiateMultiCapture();
                else
                    Utils.showLongSnack(parentLayout, "Sorry. Your device does not have a camera.");
                break;
            case R.id.multiPickerButton:
                initiateMultiPicker();
                break;
            case R.id.customThemeButton:
                setCustomTheme();
                break;
            case R.id.github:
                navigateToUrl();
                break;
        }
    }

    private void navigateToUrl() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/vansikrishna/Multimager.git")));
    }

    private void setCustomTheme() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeColor(selectedColor);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeColor(int selectedColor) {
        this.selectedColor = selectedColor;
        this.darkenedColor = Utils.getDarkColor(selectedColor);
        Utils.showShortSnack(parentLayout, "New color selected" + Integer.toHexString(selectedColor));
//        Utils.setViewsColorStateList(customThemeButton, selectedColor, darkenedColor);
//        Utils.setViewsColorStateList(multiCaptureButton, selectedColor, darkenedColor);
//        Utils.setViewsColorStateList(multiPickerButton, selectedColor, darkenedColor);

        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                customThemeButton,
                multiCaptureButton,
                multiPickerButton,
                callImageView,
                messageImageView,
                contactUsTextView,
                appNameTextView);
    }

    private void initiateMultiCapture() {
        Intent intent = new Intent(this, MultiCameraActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
    }

    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setPickerLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }


}
