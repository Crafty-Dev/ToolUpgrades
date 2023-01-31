package de.crafty.toolupgrades.event;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.recipe.RecipeManager;
import de.crafty.toolupgrades.recipe.UpgradeRecipe;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeRecipeViewListener implements Listener {


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (UpgradeItem.list().stream().filter(upgradeItem -> event.getView().getTitle().startsWith(upgradeItem.getUpgrade().getDisplayName())).toList().size() > 0)
            event.setCancelled(true);

        if (event.getView().getTitle().startsWith("\u00a77Creator:")) {

            if (event.getSlotType() == InventoryType.SlotType.RESULT || event.getSlotType() == InventoryType.SlotType.FUEL || event.isShiftClick()) {
                event.setCancelled(true);
                return;
            }

            Inventory inv = event.getView().getTopInventory();

            if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
                event.setCancelled(true);
                inv.setItem(event.getSlot(), new ItemStack(event.getCursor().getType()));
            }
        }

    }


    @EventHandler
    public void onDrag(InventoryDragEvent event) {
        if (!event.getView().getTitle().startsWith("\u00a77Creator:"))
            return;

        if (!this.isOtherInvAffected(event.getView(), event.getNewItems())) {
            event.getRawSlots().forEach(slot -> {
                event.getInventory().setItem(slot, new ItemStack(event.getOldCursor().getType()));
            });
        }
        event.setCancelled(true);

    }

    private boolean isOtherInvAffected(InventoryView view, Map<Integer, ItemStack> items) {
        for (int i : items.keySet()) {
            if (i >= view.getTopInventory().getSize())
                return true;
        }

        return false;
    }


    @EventHandler
    public void onRecipeCreation(InventoryCloseEvent event) {

        if (!event.getView().getTitle().startsWith("\u00a77Creator:"))
            return;

        Inventory inv = event.getInventory();
        UpgradeRecipe.CreationData data = RecipeManager.CREATORS.get(inv);

        if (data == null)
            return;

        RecipeManager.CREATORS.remove(inv);

        UpgradeRecipe recipe = null;

        if (data.recipeType() == UpgradeRecipe.Type.SHAPED) {
            Material[] ingredients = new Material[9];
            for (int i = 0; i < 9; i++) {
                if (inv.getItem(i + 1) == null)
                    ingredients[i] = null;
                else
                    ingredients[i] = inv.getItem(i + 1).getType();
            }

            if (this.isEmpty(ingredients))
                return;

            recipe = new UpgradeRecipe(data.recipeId(), data.upgradeItem(), data.recipeType(), ingredients);
        }

        if (data.recipeType() == UpgradeRecipe.Type.SHAPELESS) {
            List<Material> ingredients = new ArrayList<>();

            for (int i = 0; i < 9; i++) {
                if (inv.getItem(i + 1) != null)
                    ingredients.add(inv.getItem(i + 1).getType());
            }

            if (this.isEmpty(ingredients.toArray(new Material[0])))
                return;

            recipe = new UpgradeRecipe(data.recipeId(), data.upgradeItem(), data.recipeType(), ingredients.toArray(new Material[0]));
        }

        if (inv instanceof FurnaceInventory furnaceInventory) {
            if (furnaceInventory.getSmelting() != null)
                recipe = new UpgradeRecipe(data.recipeId(), data.upgradeItem(), data.recipeType(), furnaceInventory.getSmelting().getType());
        }


        if (recipe != null) {
            RecipeManager.addRecipe(recipe);
            event.getPlayer().sendMessage(ToolUpgrades.PREFIX + "\u00a7aSuccessfully \u00a77added Recipe \u00a7b" + recipe.getId() + " \u00a77for Upgrade Item: \u00a7b" + recipe.getUpgrade().getUpgrade().getDisplayName() + " \u00a77(" + data.recipeType().name() + ")");
            return;
        }

        event.getPlayer().sendMessage(ToolUpgrades.PREFIX + "Failed to save Recipe: \u00a7c" + data.recipeId());
    }


    private boolean isEmpty(Material[] ingredients) {
        for (Material mat : ingredients) {
            if (mat != null)
                return false;
        }

        return true;
    }
}
