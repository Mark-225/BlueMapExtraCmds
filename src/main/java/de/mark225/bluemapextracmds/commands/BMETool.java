package de.mark225.bluemapextracmds.commands;

import com.flowpowered.math.vector.Vector2d;
import com.flowpowered.math.vector.Vector3d;
import de.mark225.bluemapextracmds.BlueMapExtraCmds;
import de.mark225.bluemapextracmds.data.ShapeSelection;
import de.mark225.bluemapextracmds.util.BMEKeys;
import de.mark225.bluemapextracmds.util.SimpleTabCompleter;
import de.mark225.bluemapextracmds.util.persistence.BooleanDataType;
import de.mark225.bluemapextracmds.util.persistence.EnumDataType;
import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatMessageType;
import org.bukkit.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.util.RayTraceResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

public class BMETool implements CommandExecutor, SimpleTabCompleter, Listener {

    public static final EnumDataType<Mode> modeDataType = new EnumDataType<>(Mode.class, Mode.CENTER);

    private ItemStack toolPreset;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length != 0) return false;
        if(toolPreset == null) generateTool();
        if(!(sender instanceof Player)){
            sender.spigot().sendMessage(new MineDown(Lang.WRONG_EXECUTOR_PLAYER).toComponent());
            return true;
        }
        Player p = (Player) sender;
        ItemStack tool = toolPreset.clone();
        if(p.getInventory().getItemInMainHand() == null ||p.getInventory().getItemInMainHand().getType() == Material.AIR){
            p.getInventory().setItemInMainHand(tool);
            p.spigot().sendMessage(new MineDown(Lang.TOOL_ADDED).toComponent());
            return true;
        }
        HashMap<Integer, ItemStack> overflow = p.getInventory().addItem(tool);
        if(!overflow.isEmpty()){
            p.spigot().sendMessage(new MineDown(Lang.NO_INV_SPACE).toComponent());
            return true;
        }
        p.spigot().sendMessage(new MineDown(Lang.TOOL_ADDED).toComponent());
        return true;
    }

    private void generateTool(){
        ItemStack tool = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta im = tool.getItemMeta();
        PersistentDataContainer pdc = im.getPersistentDataContainer();
        pdc.set(BMEKeys.getInstance().isBMETool, BooleanDataType.instance, true);
        pdc.set(BMEKeys.getInstance().toolMode, modeDataType, Mode.CENTER);
        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.setUnbreakable(true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
        im.setDisplayName("Region creation tool");
        updateLore(im, Mode.CENTER);
        tool.setItemMeta(im);
        toolPreset = tool;
    }

    private void updateLore(ItemMeta im, Mode currentMode){
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.WHITE + "Right click" + ChatColor.GRAY + " to add a location");
        lore.add(ChatColor.WHITE + "Left click" + ChatColor.GRAY + " to remove the last location");
        lore.add(ChatColor.WHITE + "Sneak + Scroll" + ChatColor.GRAY + " to cycle through modes");
        lore.add(ChatColor.WHITE + "Sneak + Left/Right click" + ChatColor.GRAY + " to cycle through shape edges");
        lore.add("");
        lore.add(ChatColor.YELLOW + "Current Mode: " + ChatColor.WHITE + currentMode.getDisplayName());
        lore.add(ChatColor.GRAY + currentMode.getDescription());
        im.setLore(lore);
    }

    @Override
    public void onTabComplete(CommandSender sender, Command command, String alias, String[] args, List<String> completions) {

    }

    @EventHandler
    public void onScroll(PlayerItemHeldEvent evt){
        Player p = evt.getPlayer();
        if(!p.isSneaking()) return;
        boolean isOneRight = evt.getNewSlot() == Math.floorMod(evt.getPreviousSlot() + 1, 9);
        boolean isOneLeft = evt.getNewSlot() == Math.floorMod(evt.getPreviousSlot() - 1, 9);
        if(!isOneLeft && !isOneRight) return;

        ItemStack heldItem = p.getInventory().getItem(evt.getPreviousSlot());
        if(heldItem == null) return;
        ItemMeta im = heldItem.getItemMeta();
        if(im == null) return;
        PersistentDataContainer pdc = im.getPersistentDataContainer();
        if(!isBMETool(im)) return;

        evt.setCancelled(true);

        Mode m = getMode(im);
        if(m == null) m = Mode.CENTER;

        Mode nextMode = Mode.values()[Math.floorMod(m.ordinal() + (isOneRight ? 1 : -1), Mode.values().length)];
        pdc.set(BMEKeys.getInstance().toolMode, modeDataType, nextMode);
        updateLore(im, nextMode);
        heldItem.setItemMeta(im);
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new MineDown(Lang.SWITCHED_MODE).replace("mode", nextMode.getDisplayName()).toComponent());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent evt){
        Player p  =evt.getPlayer();
        ItemStack is = evt.getItem();
        if(is == null) return;
        ItemMeta im = is.getItemMeta();
        if(!isBMETool(im)) return;
        evt.setCancelled(true);
        Action action = evt.getAction();
        ShapeSelection shape = BlueMapExtraCmds.getInstance().getOrCreateSelection(p.getUniqueId());
        switch(action){
            case LEFT_CLICK_AIR:
            case LEFT_CLICK_BLOCK:
                if(p.isSneaking()){
                    cycleShape(shape, false);
                    break;
                }
                if(shape.getPoints().size() <= 0){
                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new MineDown(Lang.NO_POINTS_AVAILABLE).toComponent());
                    break;
                }
                shape.getPoints().remove(shape.getPoints().size()-1);
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new MineDown(Lang.POINT_REMOVED).toComponent());
                break;
            case RIGHT_CLICK_BLOCK:
                if(p.isSneaking()){
                    cycleShape(shape, true);
                    break;
                }
                Location clickedLocation = evt.getClickedBlock().getLocation();
                Mode mode = getMode(im);
                Vector3d toAdd = null;
                switch (mode){
                    case BLOCK:
                        toAdd = new Vector3d(clickedLocation.getX(), clickedLocation.getY() + 1, clickedLocation.getZ());
                        break;
                    case CENTER:
                        toAdd = new Vector3d(clickedLocation.getX() + 0.5, clickedLocation.getY() + 1, clickedLocation.getZ() + 0.5);
                        break;
                    case EXACT:
                        RayTraceResult result = p.rayTraceBlocks(5, FluidCollisionMode.NEVER);
                        if(result == null) break;
                        org.bukkit.util.Vector bukkitVector = p.rayTraceBlocks(5, FluidCollisionMode.NEVER).getHitPosition();
                        toAdd = new Vector3d(bukkitVector.getX(),bukkitVector.getY(), bukkitVector.getZ());
                        break;
                }
                if(toAdd == null){
                    p.spigot().sendMessage(new MineDown(Lang.ERROR_RETRY).toComponent());
                    break;
                }
                shape.getPoints().add(new Vector2d(toAdd.getX(), toAdd.getZ()));
                if(shape.getMinY() > toAdd.getY() || shape.getPoints().size() == 1) shape.setMinY((float) toAdd.getY());
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new MineDown(Lang.POINT_ADDED).replace("point", toAdd.toString()).toComponent());
                break;
            case RIGHT_CLICK_AIR:
                if(p.isSneaking()) cycleShape(shape, true);
                break;
        }

    }

    private void cycleShape(ShapeSelection selection, boolean backwards){
        List<Vector2d> points = selection.getPoints();
        if(points.size() <= 0) return;
        if(!backwards){
            Vector2d point = points.remove(0);
            points.add(point);
        }else{
            Vector2d point = points.remove(points.size() -1);
            points.add(0, point);
        }
    }

    public static boolean isBMETool(ItemMeta im){
        if(im == null) return false;
        Boolean val = im.getPersistentDataContainer().get(BMEKeys.getInstance().isBMETool, BooleanDataType.instance);
        return val != null ? val : false;
    }

    public static Mode getMode(ItemMeta im){
        try {
            return im.getPersistentDataContainer().get(BMEKeys.getInstance().toolMode, modeDataType);
        }catch (IllegalArgumentException e){
            return null;
        }
    }

    public enum Mode{
        BLOCK("Block", "Selects the north-west corner of the clicked block"),
        CENTER("Center", "Selects the center of the clicked block"),
        EXACT("Exact", "Selects exactly where you clicked (not 100% accurate)");

        String displayName;
        String description;

        Mode(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getDescription() {
            return description;
        }
    }

}
