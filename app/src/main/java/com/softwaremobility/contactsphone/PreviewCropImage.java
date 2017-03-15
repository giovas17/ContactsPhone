package com.softwaremobility.contactsphone;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.softwaremobility.interfaces.CroppingListener;

/**
 * Created by darkgeat on 3/15/17.
 */
public class PreviewCropImage extends AppCompatActivity {

    private CroppingListener listener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);

        listener = (CroppingListener) getSupportFragmentManager().findFragmentById(R.id.cropImageFragment);

        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.OnCropImage();
            }
        });
    }

}
