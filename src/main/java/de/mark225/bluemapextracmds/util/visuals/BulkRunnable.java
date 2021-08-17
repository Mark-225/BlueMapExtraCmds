package de.mark225.bluemapextracmds.util.visuals;

import java.util.ArrayList;
import java.util.List;

public class BulkRunnable implements Runnable{

    private List<Runnable> runnables = new ArrayList<>();


    @Override
    public void run() {
        for(Runnable r : runnables){
            r.run();
        }
    }

    public void addRunnable(Runnable r){
        runnables.add(r);
    }
}
