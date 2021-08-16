package de.mark225.bluemapextracmds.util;

import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import org.bukkit.NamespacedKey;

public class BMEKeys {
    private static BMEKeys instance;
    public static BMEKeys getInstance(){
        return  instance;
    }
    public static void initialize(){
        instance = new BMEKeys();
    }
    public final NamespacedKey isBMETool;
    public final NamespacedKey toolMode;

    private BMEKeys(){
        isBMETool = new NamespacedKey(BlueMapExtraCmds.getInstance(), "isTool");
        toolMode = new NamespacedKey(BlueMapExtraCmds.getInstance(), "toolMode");
    }

}
