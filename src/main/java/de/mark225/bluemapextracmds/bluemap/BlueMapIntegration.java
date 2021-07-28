package de.mark225.bluemapextracmds.bluemap;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.marker.MarkerAPI;

import java.io.IOException;

public class BlueMapIntegration {

    BlueMapAPI currentAPI = null;

    public void onEnable(BlueMapAPI api){
        currentAPI = api;
    }

    public void onDisable(BlueMapAPI api){
        currentAPI = null;
    }

    public MarkerAPI loadMarkerAPI(){
        if(currentAPI != null) {
            try {
                return currentAPI.getMarkerAPI();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public BlueMapAPI getApi(){
        return currentAPI;
    }

}
