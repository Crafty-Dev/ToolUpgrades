package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;

public class EnchantmentRelocationHandler implements Listener {

    @EventHandler
    public void onEnchantmentRelocation$0(InventoryOpenEvent event) {


        if (!(event.getInventory() instanceof GrindstoneInventory inv))
            return;

        String title = event.getView().getTitle().replace(" & ", ", ") + " & Relocate";

        ToolUpgrades.runPostEventTask(() -> event.getView().setTitle(title));

    }

    @EventHandler
    public void onEnchantmentRelocation$1(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();


        if (!(event.getInventory() instanceof GrindstoneInventory inv))
            return;


        if (event.getSlotType() == InventoryType.SlotType.RESULT)
            return;


        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && event.getClickedInventory().getType() == InventoryType.PLAYER && ToolManager.hasUpgrade(clicked, ToolUpgrade.ENCHANTMENT_RELOCATION)) {
            event.setCancelled(true);
            HashMap<Integer, ItemStack> reminder = inv.addItem(clicked);
            event.getClickedInventory().setItem(event.getSlot(), reminder.size() == 0 ? null : reminder.values().stream().toList().get(0));
            reminder.values().forEach(player.getInventory()::addItem);
            return;
        }

        if (clicked == null)
            return;

        if (ToolManager.hasUpgrade(cursor, ToolUpgrade.ENCHANTMENT_RELOCATION)) {

            if (event.isLeftClick() && event.getClickedInventory().getType() != InventoryType.PLAYER) {
                event.setCancelled(true);


                if (clicked.getType() == Material.AIR) {
                    inv.setItem(event.getSlot(), cursor);
                    player.setItemOnCursor(null);
                    return;
                }

                if (!clicked.isSimilar(cursor)) {
                    inv.setItem(event.getSlot(), cursor);
                    player.setItemOnCursor(clicked);
                    return;
                }

                int space = clicked.getMaxStackSize() - clicked.getAmount();
                if (cursor.getAmount() <= space) {
                    clicked.setAmount(clicked.getAmount() + cursor.getAmount());
                    player.setItemOnCursor(null);
                    return;
                }

                clicked.setAmount(clicked.getMaxStackSize());
                inv.setItem(event.getSlot(), clicked);

                cursor.setAmount(cursor.getAmount() - space);
                player.setItemOnCursor(cursor);
                return;
            }

            if (event.isRightClick() && event.getClickedInventory().getType() != InventoryType.PLAYER) {

                event.setCancelled(true);

                if (clicked.getType() == Material.AIR) {
                    clicked = cursor.clone();
                    clicked.setAmount(1);
                } else
                    clicked.setAmount(clicked.getAmount() + 1);

                inv.setItem(event.getSlot(), clicked);
                cursor.setAmount(cursor.getAmount() - 1);

            }
        }


    }

    @EventHandler
    public void onEnchantmentRelocation$2(PrepareGrindstoneEvent event) {

        GrindstoneInventory inv = event.getInventory();

        ItemStack input_0 = inv.getItem(0);
        ItemStack input_1 = inv.getItem(1);

        if (input_0 == null || input_1 == null)
            return;

        boolean input_0_upgrade = ToolManager.hasUpgrade(input_0, ToolUpgrade.ENCHANTMENT_RELOCATION);
        boolean input_1_upgrade = ToolManager.hasUpgrade(input_1, ToolUpgrade.ENCHANTMENT_RELOCATION);

        if (!(input_0_upgrade || input_1_upgrade))
            return;

        if (!((input_0.getEnchantments().size() > 0 && input_1_upgrade) || (input_1.getEnchantments().size() > 0 && input_0_upgrade)))
            return;
        
        ItemStack relocationStack;
        ItemStack removeStack;

        if (input_0_upgrade && input_1_upgrade) {
            relocationStack = input_0.getEnchantments().size() == 0 ? input_0.clone() : input_1.clone();
            removeStack = input_0.getEnchantments().size() == 0 ? input_1 : input_0;
        } else if (input_0_upgrade) {
            relocationStack = input_0.clone();
            removeStack = input_1;
        } else {
            relocationStack = input_1.clone();
            removeStack = input_0;
        }

        ItemMeta relocationMeta = relocationStack.getItemMeta();

        removeStack.getEnchantments().forEach((enchantment, level) -> {

            if (relocationStack.getEnchantmentLevel(enchantment) >= level)
                return;

            relocationMeta.addEnchant(enchantment, level, true);
        });

        relocationStack.setItemMeta(relocationMeta);

        if (!(input_0_upgrade && input_1_upgrade))
            ToolManager.removeUpgrade(relocationStack, ToolUpgrade.ENCHANTMENT_RELOCATION);
        relocationStack.setType(Material.ENCHANTED_BOOK);
        relocationStack.setAmount(1);

        event.setResult(relocationStack);
    }


    @EventHandler
    public void onEnchantmentRelocation$3(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        ItemStack cursor = event.getCursor();

        if (!(event.getInventory() instanceof GrindstoneInventory inv))
            return;

        ItemStack input_0 = inv.getItem(0);
        ItemStack input_1 = inv.getItem(1);
        ItemStack result = inv.getItem(2);

        boolean input_0_upgrade = ToolManager.hasUpgrade(input_0, ToolUpgrade.ENCHANTMENT_RELOCATION);
        boolean input_1_upgrade = ToolManager.hasUpgrade(input_1, ToolUpgrade.ENCHANTMENT_RELOCATION);

        if (event.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        if (input_0 == null || input_1 == null || (result == null || result.getType() != Material.ENCHANTED_BOOK))
            return;

        if (!(input_0_upgrade || input_1_upgrade))
            return;

        if (cursor.getType() != Material.AIR && (!cursor.isSimilar(clicked) || cursor.getAmount() >= cursor.getMaxStackSize())) {
            ToolUpgrades.runPostEventTask(() -> inv.setItem(2, result));
            return;
        }


        if (input_0_upgrade && input_1_upgrade) {
            if (input_0.getEnchantments().size() == 0)
                this.onPostRelocation(input_1, input_0, 1, 0, inv);
            else
                this.onPostRelocation(input_0, input_1, 0, 1, inv);

            return;
        }


        if (input_0_upgrade) {
            this.onPostRelocation(input_1, input_0, 1, 0, inv);
            return;
        }

        this.onPostRelocation(input_0, input_1, 0, 1, inv);

    }

    private void onPostRelocation(ItemStack removeStack, ItemStack relocationStack, int removeSlot, int relocationSlot, GrindstoneInventory inv) {
        removeStack.getEnchantments().forEach((enchantment, integer) -> removeStack.removeEnchantment(enchantment));
        relocationStack.setAmount(relocationStack.getAmount() - 1);

        ToolUpgrades.runPostEventTask(() -> {
            if (removeStack.getType() == Material.ENCHANTED_BOOK)
                removeStack.setType(Material.BOOK);

            inv.setItem(removeSlot, removeStack);
            inv.setItem(relocationSlot, relocationStack);
        });
    }

    @EventHandler
    public void preventBookBug(PrepareGrindstoneEvent event) {

        GrindstoneInventory inv = event.getInventory();

        ItemStack input_0 = inv.getItem(0);
        ItemStack input_1 = inv.getItem(1);
        ItemStack result = event.getResult();

        if (result == null || result.getType() != Material.BOOK)
            return;

        boolean input_0_upgrades = ToolManager.getUpgrades(input_0).size() > 0;
        boolean input_1_upgrades = ToolManager.getUpgrades(input_1).size() > 0;

        if ((input_0_upgrades && input_1_upgrades) || !(input_0_upgrades || input_1_upgrades))
            return;

        if (input_0 != null && input_1 != null)
            return;

        ItemStack newResult = input_0 == null ? input_1.clone() : input_0.clone();
        newResult.setType(Material.BOOK);
        newResult.getEnchantments().forEach((enchantment, integer) -> newResult.removeEnchantment(enchantment));
        event.setResult(newResult);

    }
}
