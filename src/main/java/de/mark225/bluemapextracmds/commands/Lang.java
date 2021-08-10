package de.mark225.bluemapextracmds.commands;

public interface Lang {
    String WRONG_EXECUTOR_PLAYER = "&red&You can only execute this command as a Player.";
    String WE_UNAVAILABLE = "&red&To import a region selection, you need to have WorldEdit installed.";
    String WE_NO_SELECTION = "&red&Can't import from WorldEdit. Make a selection in your current world first and try again.";
    String SEL_IMPORTED = "&green&WorldEdit selection imported successfully.";
    String SEL_RESET = "&green&Your selection has been reset.";
    String BLOCKIFY_INVALID_SEL = "&red&You must first select a valid polygon (>=3 points) to do this.";
    String UNEXPECTED_ERROR = "&red&An unexpected error occurred. Please report this to the plugin author.";
    String BLOCKIFY_STARTED = "&green&Blockify in progress. This may take a while for large polygons...\n&red&Do not use or edit your region selection while this is running.";
    String BLOCKIFY_SUCCESS = "&green&Blockify completed. You can now use your selection to create markers.";
    String NUMBER_FORMAT = "&red&Wrong number format!\n" +
            "Make sure you are either entering a number or a '~' symbol to use your current x/z coordinates (optionally followed by decimal places). Examples:\n" +
            "2.5, -1, ~, ~.5, ~.0";
    String POINT_ADDED = "&green&Point %point% added to your current selection";
}
