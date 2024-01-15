package de.crafty.toolupgrades.util;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.*;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ToolManager {


    public static ItemStack applyUpgrade(ItemStack stack, ToolUpgrade upgrade) {
        if (ToolManager.hasUpgrade(stack, upgrade) || stack.getItemMeta() == null)
            return stack;


        List<String> additionalLore = new ArrayList<>();

        if (ToolManager.getUpgrades(stack).size() == 0)
            additionalLore.addAll(Arrays.asList("   ", "\u00a76\u00a7lUpgrades: "));


        additionalLore.add("\u00a77- " + upgrade.getDisplayName());

        String upgradeData = stack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(ToolUpgrades.getInstance(), "upgrades"), PersistentDataType.STRING, "");
        upgradeData += upgrade.name() + ";";

        ItemMeta meta = stack.getItemMeta();

        meta.getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "upgrades"), PersistentDataType.STRING, upgradeData);

        List<String> currentLore = new ArrayList<>();

        if (meta.hasLore())
            currentLore = meta.getLore();

        currentLore.addAll(additionalLore);
        meta.setLore(currentLore);

        stack.setItemMeta(meta);
        return stack;
    }

    public static ItemStack removeUpgrade(ItemStack stack, ToolUpgrade upgrade) {

        if (!ToolManager.hasUpgrade(stack, upgrade))
            return stack;

        List<ToolUpgrade> upgrades = ToolManager.getUpgrades(stack);
        upgrades.remove(upgrade);

        ItemMeta meta = stack.getItemMeta();
        meta.setLore(new ArrayList<>());
        meta.getPersistentDataContainer().remove(new NamespacedKey(ToolUpgrades.getInstance(), "upgrades"));
        stack.setItemMeta(meta);


        upgrades.forEach(u -> ToolManager.applyUpgrade(stack, u));

        return stack;
    }

    public static boolean canApplyTo(ItemStack stack, ToolUpgrade upgrade) {
        return !ToolManager.hasUpgrade(stack, upgrade) && ToolManager.canApplyTo(stack, upgrade.getType());
    }

    private static boolean canApplyTo(ItemStack stack, ToolUpgrade.Type type) {

        if (type == ToolUpgrade.Type.ALL_GEAR && (CraftItemStack.asNMSCopy(stack).d() instanceof ItemToolMaterial || CraftItemStack.asNMSCopy(stack).d() instanceof ItemArmor || CraftItemStack.asNMSCopy(stack).d() instanceof ItemProjectileWeapon))
            return true;

        if (type == ToolUpgrade.Type.BOOK && (CraftItemStack.asNMSCopy(stack).d() instanceof ItemBook || CraftItemStack.asNMSCopy(stack).d() instanceof ItemEnchantedBook))
            return true;

        if (CraftItemStack.asNMSCopy(stack).d() instanceof ItemArmor armor) {

            if (armor.b().a() == EnumItemSlot.c && type == ToolUpgrade.Type.BOOTS)
                return true;
            if (armor.b().a() == EnumItemSlot.d && type == ToolUpgrade.Type.LEGGINGS)
                return true;
            if (armor.b().a() == EnumItemSlot.e && type == ToolUpgrade.Type.CHESTPLATE)
                return true;
            if (armor.b().a() == EnumItemSlot.f && type == ToolUpgrade.Type.HELMET)
                return true;

        }

        if (CraftItemStack.asNMSCopy(stack).d() instanceof ItemToolMaterial && type == ToolUpgrade.Type.TOOL_AND_WEAPON)
            return true;

        if (CraftItemStack.asNMSCopy(stack).d() instanceof ItemTool && type == ToolUpgrade.Type.TOOL)
            return true;

        if ((CraftItemStack.asNMSCopy(stack).d() instanceof ItemSword || CraftItemStack.asNMSCopy(stack).d() instanceof ItemProjectileWeapon) && (type == ToolUpgrade.Type.WEAPON || type == ToolUpgrade.Type.TOOL_AND_WEAPON))
            return true;

        return CraftItemStack.asNMSCopy(stack).d() instanceof Equipable && type == ToolUpgrade.Type.ARMOR;
    }

    public static List<ToolUpgrade> getUpgrades(ItemStack stack) {
        List<ToolUpgrade> list = new ArrayList<>();

        if (stack == null || stack.getItemMeta() == null)
            return list;

        String upgradeData = stack.getItemMeta().getPersistentDataContainer().getOrDefault(new NamespacedKey(ToolUpgrades.getInstance(), "upgrades"), PersistentDataType.STRING, "");

        if ("".equals(upgradeData))
            return list;

        String[] upgrades = upgradeData.split(";");

        for (String upgrade : upgrades) {
            list.add(ToolUpgrade.valueOf(upgrade));
        }

        return list;
    }

    public static boolean hasUpgrade(ItemStack stack, ToolUpgrade upgrade) {
        return ToolManager.getUpgrades(stack).contains(upgrade);
    }


    //---------------------Mob Capture---------------------

    public static void addCapturedMobData(ItemStack stack, EntityType entityType, NBTTagCompound tag) {

        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();

        meta.getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING, entityType + "_:_" + tag.toString());

        int i = lore.indexOf("\u00a77- " + ToolUpgrade.MOB_CAPTURE.getDisplayName());

        lore.set(i, lore.get(i) + " \u00a77(\u00a7b" + entityType.name().toLowerCase() + "\u00a77)");
        meta.setLore(lore);

        stack.setItemMeta(meta);

    }

    public static void removeCapturedMobData(ItemStack stack) {

        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();

        int i;
        for (i = 0; i < lore.size(); i++) {

            if (!lore.get(i).startsWith("\u00a77- " + ToolUpgrade.MOB_CAPTURE.getDisplayName()))
                continue;
            break;
        }

        meta.getPersistentDataContainer().remove(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"));


        lore.set(i, "\u00a77- " + ToolUpgrade.MOB_CAPTURE.getDisplayName());
        meta.setLore(lore);

        stack.setItemMeta(meta);

    }

    public static EntityType getCapturedMob(ItemStack stack) {

        if (!ToolManager.hasUpgrade(stack, ToolUpgrade.MOB_CAPTURE) || !stack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING))
            return null;

        return EntityType.valueOf(stack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING).split("_:_")[0]);
    }

    //---------------------Celestial---------------------
    public static void addCelestialData(ItemStack stack, int pieces) {

        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();


        String color = pieces == 4 ? "\u00a7a" : "\u00a77";

        for (int i = 0; i < lore.size(); i++) {
            if (!lore.get(i).startsWith("\u00a77- " + ToolUpgrade.CELESTIAL.getDisplayName()))
                continue;

            lore.set(i, ("\u00a77- " + ToolUpgrade.CELESTIAL.getDisplayName()) + " \u00a77(" + color + pieces + "\u00a77/\u00a764" + "\u00a77)");
            meta.setLore(lore);
            stack.setItemMeta(meta);
            break;
        }
    }

    public static void removeCelestialData(ItemStack stack) {

        if (stack == null)
            return;

        ItemMeta meta = stack.getItemMeta();
        if (meta == null || meta.getLore() == null)
            return;

        List<String> lore = meta.getLore();

        int i;
        for (i = 0; i < lore.size(); i++) {

            if (!lore.get(i).startsWith("\u00a77- " + ToolUpgrade.CELESTIAL.getDisplayName()))
                continue;

            lore.set(i, "\u00a77- " + ToolUpgrade.CELESTIAL.getDisplayName());
            break;
        }


        meta.setLore(lore);

        stack.setItemMeta(meta);

    }

    public static void setCelestialData(Player player, int pieces) {
        player.getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "celestialPieces"), PersistentDataType.INTEGER, pieces);
    }

    public static void removeCelestialData(Player player) {
        if (!player.getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "celestialPieces"), PersistentDataType.INTEGER))
            return;

        player.getPersistentDataContainer().remove(new NamespacedKey(ToolUpgrades.getInstance(), "celestialPieces"));
    }

    public static int getCelestialPieces(Player player) {
        NamespacedKey key = new NamespacedKey(ToolUpgrades.getInstance(), "celestialPieces");
        return player.getPersistentDataContainer().has(key, PersistentDataType.INTEGER) ? player.getPersistentDataContainer().get(key, PersistentDataType.INTEGER) : 0;
    }
}
