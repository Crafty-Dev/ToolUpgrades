package de.crafty.toolupgrades.event;

import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.SmithingInventory;

public class UpgradeAbusePreventionListener implements Listener {


    @EventHandler
    public void onItemClick$0(InventoryClickEvent event) {

        if (!(event.getInventory() instanceof CraftingInventory) || event.getWhoClicked().getGameMode() == GameMode.CREATIVE)
            return;

        if (UpgradeItem.getByStack(event.getCurrentItem()) != null)
            event.setCancelled(true);


        if (event.getHotbarButton() >= 0 && UpgradeItem.getByStack(event.getView().getBottomInventory().getItem(event.getHotbarButton())) != null)
            event.setCancelled(true);
    }

    @EventHandler
    public void onInteract$0(PlayerInteractEvent event) {

        if(event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if(UpgradeItem.getByStack(event.getItem()) == null)
            return;

        if(event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.CRAFTING_TABLE || event.getClickedBlock().getType() == Material.ANVIL))
            return;

        event.setCancelled(true);

    }

}
