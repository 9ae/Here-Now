package me.valour.hereandnow.objects;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;

import me.valour.hereandnow.constants.Himitsu;

/**
 * Created by alice on 12/7/14.
 */
public class AstraUploader {

    private final String filename;
    private final String venueID;
    private final String checkinID;
    private final Context context;
    private FutureCallback<JsonObject> finalCallback;

    public boolean taskComplete;

    public AstraUploader(String filename, String venueID, String checkinID, Context context){
        this.filename = filename;
        this.venueID = venueID;
        this.checkinID = checkinID;
        this.context = context;
        taskComplete = false;
    }

    public void startUpload(FutureCallback<JsonObject> finalCallback){
        this.finalCallback = finalCallback;
        Ion.with(context)
                .load("https://api.astra.io/v0/bucket/"+venueID)
                .setHeader(Himitsu.Astra.authName, Himitsu.Astra.secret)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                Log.d("test", new Gson().toJson(result));
                if(result.get("ok").getAsBoolean()){
                    uploadCheckinImage();
                } else {
                    createVenueBucket();
                }
            }
        });;

    }

    private void createVenueBucket(){
        Ion.with(context).load("https://api.astra.io/v0/bucket")
                .setHeader(Himitsu.Astra.authName,Himitsu.Astra.secret)
                .setBodyParameter("name",venueID)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject jsonObject) {
                Log.d("test", new Gson().toJson(jsonObject));
                if(jsonObject.get("ok").getAsBoolean()){
                    if(jsonObject.get("data").getAsJsonObject().get("status").getAsString().equals("ready")){
                        uploadCheckinImage();
                    }
                }
            }
        });
    }

    private void uploadCheckinImage(){
        Log.d("test", "prepare to upload image");
        Ion.with(context, "https://api.astra.io/v0/bucket/"+venueID+"/object")
                .setLogging("request", Log.DEBUG)
                .setHeader(Himitsu.Astra.authName,Himitsu.Astra.secret)
                .setMultipartParameter("type", "image")
                .setMultipartParameter("name",checkinID)
                .setMultipartFile("file", new File(filename))
                .asJsonObject()
                .setCallback(finalCallback);
    }

}
