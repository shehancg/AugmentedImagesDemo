package com.example.travelBuddy1;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements Scene.OnUpdateListener {

    private CustomArFragment arFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //HOLD A REFERENCE TO AN AR FRAGMENT THAT IS THEIR IN OUR LAYOUT FILE
        arFragment = (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
    }

    //SETTING UP THE AR IMAGE DATABASE
    public void setupDatabase(Config config, Session session){
        Bitmap dragonBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.dragon);
        AugmentedImageDatabase aid = new AugmentedImageDatabase(session);
        aid.addImage("dragon",dragonBitmap);
        config.setAugmentedImageDatabase(aid);
    }
    //SCANNING EVERY FRAME PER INTERVAL TO SEE IF OUR DESIRED IMAGE IS THEIR
    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        Collection<AugmentedImage> images = frame.getUpdatedTrackables(AugmentedImage.class);

        //WE WILL GO THROUGH A FOREACH LOOP TO SEE IF OUR DRAGON IMAGE IS BEING TRACKED
        // IF IT IS AVAILABLE THEN WE WILL CREATE A ANCHOR AN PLACE THE 3D MODEL
        for(AugmentedImage image:images){
            if (image.getTrackingState()== TrackingState.TRACKING){
                if (image.getName().equals("dragon")){
                    Anchor anchor = image.createAnchor(image.getCenterPose());
                    createModel(anchor);
                }
            }
        }
    }

    private void createModel(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("cloud.sfb"))
                .build()
                .thenAccept(modelRenderable -> placeModel(modelRenderable,anchor));
    }

    private void placeModel(ModelRenderable modelRenderable, Anchor anchor) {
        AnchorNode anchorNode=new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
    }
}