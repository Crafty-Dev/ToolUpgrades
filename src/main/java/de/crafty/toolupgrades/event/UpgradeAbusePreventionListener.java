package de.crafty.toolupgrades.event;

import de.crafty.toolupgrades.upgrade.UpgradeItem;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftInventoryCrafting;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

public class UpgradeAbusePreventionListener implements Listener {


    @EventHandler
    public void onItemDrag$0(InventoryDragEvent event) {

        if (event.getInventory().getType() == InventoryType.CHEST || event.getInventory().getType() == InventoryType.ENDER_CHEST || event.getInventory().getType() == InventoryType.ANVIL)
            return;
        if (UpgradeItem.getByStack(event.getNewItems().values().stream().findFirst().orElse(null)) == null)
            return;

        System.out.println("Moin");
        for (int i : event.getRawSlots()) {

            if (i < event.getView().getTopInventory().getSize()) {
                event.setCancelled(true);
                break;
            }

        }

    }


    @EventHandler
    public void onItemClick$0(InventoryClickEvent event) {

        Inventory clicked = event.getClickedInventory();

        if (event.getInventory().getType() == InventoryType.CHEST || event.getInventory().getType() == InventoryType.ENDER_CHEST || event.getInventory().getType() == InventoryType.ANVIL)
            return;

        if (event.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY && !event.getAction().toString().startsWith("PLACE"))
            return;

        if (event.getAction().toString().startsWith("PLACE") && event.getClickedInventory() == event.getView().getBottomInventory())
            return;


        if (event.getAction().toString().startsWith("PLACE") && UpgradeItem.getByStack(event.getCursor()) != null)
            event.setCancelled(true);

        if(event.getInventory() instanceof CraftInventoryCrafting craftingInventory && craftingInventory.getSize() == 5)
            return;

        if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && UpgradeItem.getByStack(event.getCurrentItem()) != null)
            event.setCancelled(true);


    }

    @EventHandler
    public void onItemMove$0(InventoryMoveItemEvent event) {

        if (event.getDestination() == event.getSource() || event.getDestination().getType() == InventoryType.PLAYER)
            return;

        if (UpgradeItem.getByStack(event.getItem()) != null && event.getDestination().getType() != InventoryType.CHEST)
            event.setCancelled(true);

    }

    @EventHandler
    public void onInteract$0(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (UpgradeItem.getByStack(event.getItem()) == null)
            return;

        if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.CRAFTING_TABLE || event.getClickedBlock().getType() == Material.ANVIL))
            return;

        event.setCancelled(true);

    }

}
