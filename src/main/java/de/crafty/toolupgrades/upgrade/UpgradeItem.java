package de.crafty.toolupgrades.upgrade;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpgradeItem {


    private static final List<UpgradeItem> ALL = new ArrayList<>();

    public static final UpgradeItem MAGNETISM = register("magnetism", ToolUpgrade.MAGNETISM, Material.DIAMOND, true, "\u00a77Get Mob and Block Drops directly in your Inventory");
    public static final UpgradeItem AUTO_SMELTING = register("auto_smelting", ToolUpgrade.AUTO_SMELTING, Material.LAVA_BUCKET, true, "\u00a77Automatically smelts destroyed blocks");

    public static final UpgradeItem MULTI_MINER = register("multi_miner", ToolUpgrade.MULTI_MINER, Material.COBBLESTONE, true, "\u00a77Mine multiple Blocks at once");

    public static final UpgradeItem TELEPORTATION = register("teleportation", ToolUpgrade.TELEPORTATION, Material.ENDER_PEARL, true, "\u00a77Sword: Allows you to use a short range teleport", "\u00a77Bow: Teleport to arrow destination");

    public static final UpgradeItem FADELESS = register("fadeless", ToolUpgrade.FADELESS, Material.NETHER_STAR, true, "\u00a77Prevents Items from being lost on death");
    public static final UpgradeItem SOFT_FALL = register("soft_fall", ToolUpgrade.SOFT_FALL, Material.FEATHER, true, "\u00a77Prevents you from taking Fall damage");
    public static final UpgradeItem ENDER_MASK = register("ender_mask", ToolUpgrade.ENDER_MASK, Material.CARVED_PUMPKIN, true, "\u00a77Prevents Enderman from attacking you when you look at them");

    public static final UpgradeItem MOB_CAPTURE = register("mob_capture", ToolUpgrade.MOB_CAPTURE, Material.ZOMBIE_HEAD, true, "\u00a77Allows you to capture a mob in your item");

    public static final UpgradeItem SILKY = register("silky", ToolUpgrade.SILKY, Material.WHITE_WOOL, true, "\u00a77Mine blocks that you normally can't mine");

    public static final UpgradeItem LIFE_BONUS = register("life_bonus", ToolUpgrade.LIFE_BONUS, Material.GLISTERING_MELON_SLICE, true, "\u00a77Gain extra hearts");

    private static UpgradeItem register(String id, ToolUpgrade upgrade, Material material, boolean enchantmentGlint, String... lore) {

        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();

        meta.setDisplayName(upgrade.getDisplayName() + " Upgrade");
        meta.setLore(Arrays.asList(lore));

        if (enchantmentGlint) {
            meta.addEnchant(Enchantment.DURABILITY, 0, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }

        stack.setItemMeta(meta);

        UpgradeItem item = new UpgradeItem(id, upgrade, stack);
        ALL.add(item);
        return item;
    }

    public static UpgradeItem getByStack(ItemStack stack) {

        if (stack == null)
            return null;

        for (UpgradeItem upgradeItem : ALL) {
            if (stack.isSimilar(upgradeItem.getStack()))
                return upgradeItem;
        }

        return null;
    }


    private final String id;
    private final ToolUpgrade upgrade;
    private final ItemStack stack;

    private UpgradeItem(String id, ToolUpgrade upgrade, ItemStack stack) {
        this.id = id;
        this.upgrade = upgrade;
        this.stack = stack;
    }


    public ItemStack getStack() {
        return this.stack;
    }

    public ToolUpgrade getUpgrade() {
        return this.upgrade;
    }

    public String getId() {
        return this.id;
    }

    public static List<UpgradeItem> list() {
        return ALL;
    }

    public static UpgradeItem byName(String name) {

        for(UpgradeItem item : UpgradeItem.list()){
            if(item.getId().equals(name))
                return item;
        }

        return null;
    }
}
