# Information
If you can read this message, the plugin is not yet ready. Until the first version is released, this repository is solely for people who might be interested in the development of this plugin.

## Currently (partly) working features:
 - Importing region selections from WorldEdit
 - Command to "blockify" the selected polygon
 - Debug command to create a simple shape marker from the current selection (will be replaced by a customizable region creation command)
 - Creating a region directly by adding points using /bmeregion addPoint
 - Region creation tool to define a region by right clicking points in the world
   - /bmetool adds the tool to your inventory
   - Sneak + Scroll cycles through the (currently) three modes: Block, Center and Exact
   - Right clicking adds the clicked location (depending on the mode) to the current region selection as a point
   - Left clicking removes the last added point
   - While holding the tool, your current region is shown as particles (Red: first corner, Green: corners, Yellow: lines between corners, Orange: last line)
