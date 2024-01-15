package de.crafty.toolupgrades.event;

import de.crafty.toolupgrades.upgrade.UpgradeItem;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class PlayerApplyUpgradeListener implements Listener {


    @EventHandler
    public void onApply$0(PrepareSmithingEvent event) {

        SmithingInventory inv = event.getInventory();

        ItemStack stack = inv.getItem(0);
        ItemStack upgradeStack = inv.getItem(1);

        if (stack == null || upgradeStack == null)
            return;


        UpgradeItem upgradeItem = UpgradeItem.getByStack(upgradeStack);
        if (upgradeItem == null)
            return;


        if (!ToolManager.canApplyTo(stack, upgradeItem.getUpgrade()))
            return;

        event.setResult(ToolManager.applyUpgrade(stack.clone(), upgradeItem.getUpgrade()));

    }


    @EventHandler
    public void onApply$1(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        if (!(event.getSlotType() == InventoryType.SlotType.RESULT && event.getView().getTopInventory() instanceof SmithingInventory inv))
            return;

        if (UpgradeItem.getByStack(inv.getItem(1)) == null || inv.getResult() == null || inv.getResult().getType() == Material.AIR)
            return;


        if (event.isShiftClick()) {
            if(player.getInventory().addItem(inv.getResult()).size() != 0)
                return;
        }

        if(!event.isShiftClick() && player.getItemOnCursor().getType() != Material.AIR)
            return;


        if(!event.isShiftClick())
            player.setItemOnCursor(inv.getResult());

        if (inv.getItem(0).getAmount() > 1)
            inv.getItem(0).setAmount(inv.getItem(0).getAmount() - 1);
        else
            inv.setItem(0, null);


        if (inv.getItem(1).getAmount() > 1)
            inv.getItem(1).setAmount(inv.getItem(1).getAmount() - 1);
        else
            inv.setItem(1, null);

        inv.setResult(null);
        player.playSound(player.getLocation(), Sound.BLOCK_SMITHING_TABLE_USE, 1.0F, 1.0F);
    }
}
