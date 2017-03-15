package com.softwaremobility.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.isseiaoki.simplecropview.CropImageView;
import com.softwaremobility.contactsphone.R;
import com.softwaremobility.interfaces.CroppingListener;
import com.softwaremobility.utilities.MediaUtilities;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.softwaremobility.utilities.MediaUtilities.dispatchTakePictureIntent;
import static com.softwaremobility.utilities.MediaUtilities.mCurrentPhotoPath;

/**
 * Created by darkgeat on 3/15/17.
 */
public class PreviewCropImage extends Fragment implements CroppingListener {

    private static final String TAG = PreviewCropImage.class.getSimpleName();
    private boolean isFromGallery = false;
    private boolean isFromCamera = false;
    private boolean isCroppingAgain = false;
    private boolean isPortrait = false;
    private static final int IMAGE_PICKER = 2345;
    private CropImageView imageHolder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromGallery = getActivity().getIntent().getBooleanExtra(getString(R.string.tag_gallery),false);
        isFromCamera = getActivity().getIntent().getBooleanExtra(getString(R.string.tag_photo),false);
        isCroppingAgain = getActivity().getIntent().getBooleanExtra(getString(R.string.cropping_tag),false);
        isPortrait = getActivity().getIntent().getBooleanExtra("portrait",true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crop_image,container,false);

        if (!isPortrait){
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        imageHolder = (CropImageView) view.findViewById(R.id.cropImageView);
        imageHolder.setGuideShowMode(CropImageView.ShowMode.SHOW_ON_TOUCH);
        if (savedInstanceState != null){

        }else {
            if (isFromGallery) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMAGE_PICKER);
            }
            if (isFromCamera) {
                dispatchTakePictureIntent(PreviewCropImage.this);
            }
        }

        TextView ratio169 = (TextView) view.findViewById(R.id.aspectRatio169);
        TextView ratio43 = (TextView) view.findViewById(R.id.aspectRatio43);

        ratio169.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHolder.setCropMode(CropImageView.CropMode.RATIO_16_9);
                imageHolder.setInitialFrameScale(0.5f);
            }
        });

        ratio43.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHolder.setCropMode(CropImageView.CropMode.RATIO_4_3);
                imageHolder.setInitialFrameScale(0.5f);
            }
        });

        ImageView rotateLeft = (ImageView)view.findViewById(R.id.rotateLeftCropImage);
        ImageView rotateRight = (ImageView)view.findViewById(R.id.rotateRightCropImage);

        rotateLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHolder.rotateImage(CropImageView.RotateDegrees.ROTATE_M90D);
            }
        });

        rotateRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageHolder.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });

        if (isCroppingAgain){
            String path = getActivity().getIntent().getStringExtra(getString(R.string.crop_path));
            Glide.with(this).load(path).asBitmap().fitCenter()
                    .into(new SimpleTarget<Bitmap>(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            Log.d(TAG,"Image width: " + resource.getWidth());
                            Log.d(TAG,"Image height: " + resource.getHeight());
                            Log.d(TAG,"Image size: " + (resource.getHeight() * resource.getRowBytes()));
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG,100,stream);
                            final Bitmap bitmap2 = Bitmap.createScaledBitmap(resource, resource.getWidth(), resource.getHeight(), false);
                            imageHolder.setImageBitmap(bitmap2);
                        }
                    });
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_PICKER){
            if (data != null){
                Uri uri = data.getData();
                try {
                    final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    Bitmap less = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
                    imageHolder.setImageBitmap(less);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                        Bitmap less = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
                        imageHolder.setImageBitmap(less);
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        if (requestCode == MediaUtilities.MEDIA_TYPE_IMAGE){
            if (resultCode == Activity.RESULT_OK){
                Glide.with(this).load(mCurrentPhotoPath)
                        .asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new SimpleTarget<Bitmap>(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL, com.bumptech.glide.request.target.Target.SIZE_ORIGINAL) {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                Bitmap less = Bitmap.createScaledBitmap(resource,resource.getWidth()/2,resource.getHeight()/2,false);
                                imageHolder.setImageBitmap(less);
                            }
                        });
            }
        }
        if (resultCode == 0){
            getActivity().setResult(Activity.RESULT_CANCELED,null);
            getActivity().finish();
        }
    }

    @Override
    public void OnCropImage() {
        Bitmap bitmap = imageHolder.getCroppedBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
        byte[] array = stream.toByteArray();
        while ((bitmap.getHeight() * bitmap.getRowBytes())> (2 * 1048576)){ // Bigger than 2Mb
            stream = new ByteArrayOutputStream();
            bitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth() * 0.5),(int)(bitmap.getHeight() * 0.5),false);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
            array = stream.toByteArray();
        }
        Log.d(TAG,"Image width: " + bitmap.getWidth());
        Log.d(TAG,"Image height: " + bitmap.getHeight());
        Log.d(TAG,"Image size: " + (bitmap.getHeight() * bitmap.getRowBytes()));
        String path = "";
        try {
            File imageFile = MediaUtilities.createImageFile(getActivity());
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(imageFile));
            bos.write(array);
            bos.flush();
            bos.close();
            path = imageFile.getPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent data = new Intent();
        data.putExtra(getString(R.string.tag_picture),path);
        getActivity().setResult(Activity.RESULT_OK,data);
        getActivity().finish();
    }
}
