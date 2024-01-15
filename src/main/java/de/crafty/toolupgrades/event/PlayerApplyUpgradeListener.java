package de.crafty.toolupgrades.event;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.UpgradeItem;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class PlayerApplyUpgradeListener implements Listener {


    @EventHandler
    public void onApplyUpgrade$0(PrepareAnvilEvent event) {

        AnvilInventory inv = event.getInventory();

        ItemStack input_0 = inv.getItem(0);
        ItemStack input_1 = inv.getItem(1);

        UpgradeItem upgradeItem = UpgradeItem.getByStack(input_1);
        if (upgradeItem == null || !ToolManager.canApplyTo(input_0, upgradeItem.getUpgrade()))
            return;

        ItemStack result = input_0.clone();
        result.setAmount(1);

        event.setResult(ToolManager.applyUpgrade(result, upgradeItem.getUpgrade()));

    }


    @EventHandler
    public void onApplyUpgrade$1(InventoryClickEvent event) {

        if (!(event.getInventory() instanceof AnvilInventory inv))
            return;

        Player player = (Player) event.getWhoClicked();

        ItemStack input_0 = inv.getItem(0);
        ItemStack input_1 = inv.getItem(1);
        ItemStack result = inv.getItem(2);

        if (event.getSlotType() != InventoryType.SlotType.RESULT)
            return;

        UpgradeItem upgradeItem = UpgradeItem.getByStack(input_1);
        if (input_0 == null || upgradeItem == null || result == null)
            return;

        if (event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) {

            if (player.getInventory().addItem(result).size() == 0) {
                this.onSuccessfullUpgrade(input_0, input_1, player, inv);
            }

            return;
        }

        if (event.getClick() == ClickType.LEFT || event.getClick() == ClickType.RIGHT) {

            ItemStack cursor = player.getItemOnCursor();

            if (cursor.getType() == Material.AIR)
                player.setItemOnCursor(result);
            else if (!cursor.isSimilar(result))
                return;
            else if (cursor.getAmount() >= cursor.getMaxStackSize())
                return;

            cursor.setAmount(cursor.getAmount() + 1);
            this.onSuccessfullUpgrade(input_0, input_1, player, inv);

        }

    }

    private void onSuccessfullUpgrade(ItemStack input_0, ItemStack input_1, Player player, Inventory inv){
        inv.setItem(2, null);
        input_0.setAmount(input_0.getAmount() - 1);
        input_1.setAmount(input_1.getAmount() - 1);


        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, 1.0F, 1.0F);

        //Update Inventory for PrepareAnvilEvent
        Bukkit.getScheduler().scheduleSyncDelayedTask(ToolUpgrades.getInstance(), () -> inv.setItem(1, input_1));
    }

}
