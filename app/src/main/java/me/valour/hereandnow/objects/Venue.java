package me.valour.hereandnow.objects;

import com.google.gson.JsonObject;

/**
 * Created by alice on 12/6/14.
 */
public class Venue {

    public String name;
    public String address;
    public String fourSquareId;


    public Venue(JsonObject jsonObject){
        fourSquareId = jsonObject.get("id").getAsString();
        name = jsonObject.get("name").getAsString();
        if(!jsonObject.get("location").isJsonNull()) {
            JsonObject locationObj = jsonObject.get("location").getAsJsonObject();
            if (locationObj.has("address")) {
                address = locationObj.get("address").getAsString();
            }
        }
    }

}
