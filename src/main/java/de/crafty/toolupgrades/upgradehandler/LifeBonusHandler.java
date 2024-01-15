package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.api.MaxHealthChangeEvent;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.BoundingBox;

public class LifeBonusHandler implements Listener {


    @EventHandler
    public void onLifeBonusUpdate$0(InventoryClickEvent event) {

        this.checkArmor((Player) event.getWhoClicked());

    }

    @EventHandler
    public void onLifeBonusUpdate$1(PlayerInteractEvent event) {

        this.checkArmor(event.getPlayer());

    }

    @EventHandler
    public void onLifeBonusUpdate$2(BlockDispenseArmorEvent event) {

        if (event.getTargetEntity() instanceof Player player)
            this.checkArmor(player);

    }

    @EventHandler
    public void onLifeBonusUpdate$3(EntityPickupItemEvent event) {

        if (event.getEntity() instanceof Player player)
            this.checkArmor(player);
    }

    @EventHandler
    public void onLifeBonusUpdate$4(PlayerRespawnEvent event) {
        this.checkArmor(event.getPlayer());
    }


    @EventHandler
    public void onLifeBonusUpdate$5(PlayerItemBreakEvent event) {

        if (ToolManager.hasUpgrade(event.getBrokenItem(), ToolUpgrade.LIFE_BONUS))
            this.checkArmor(event.getPlayer());

    }

    private void checkArmor(Player player) {

        PlayerInventory playerInv = player.getInventory();

        ItemStack oldHelmet = playerInv.getHelmet();
        ItemStack oldChestplate = playerInv.getChestplate();
        ItemStack oldLeggings = playerInv.getLeggings();
        ItemStack oldBoots = playerInv.getBoots();


        ToolUpgrades.runPostEventTask(() -> {

            ItemStack helmet = playerInv.getHelmet();
            ItemStack chestplate = playerInv.getChestplate();
            ItemStack leggings = playerInv.getLeggings();
            ItemStack boots = playerInv.getBoots();

            if ((helmet != null && helmet.equals(oldHelmet)) && (chestplate != null && chestplate.equals(oldChestplate)) && (leggings != null && leggings.equals(oldLeggings)) && (boots != null && boots.equals(oldBoots)))
                return;

            double maxHealth = 20.0F;

            if (ToolManager.hasUpgrade(helmet, ToolUpgrade.LIFE_BONUS))
                maxHealth += 5.0F;

            if (ToolManager.hasUpgrade(chestplate, ToolUpgrade.LIFE_BONUS))
                maxHealth += 5.0F;

            if (ToolManager.hasUpgrade(leggings, ToolUpgrade.LIFE_BONUS))
                maxHealth += 5.0F;

            if (ToolManager.hasUpgrade(boots, ToolUpgrade.LIFE_BONUS))
                maxHealth += 5.0F;

            double prevMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
            if (prevMaxHealth == maxHealth)
                return;

            MaxHealthChangeEvent e = new MaxHealthChangeEvent(player, prevMaxHealth, maxHealth);
            Bukkit.getPluginManager().callEvent(e);

            maxHealth = e.getNewHealth();

            if (player.getHealth() > maxHealth)
                player.setHealth(maxHealth);

            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(maxHealth);

            World world = player.getWorld();
            BoundingBox box = player.getBoundingBox();

            int xPoints = 3;
            int zPoints = 3;
            int yPoints = 5;

            if (maxHealth > prevMaxHealth) {

                for (int x = 0; x < xPoints; x++) {
                    for (int y = 0; y < yPoints; y++) {
                        for (int z = 0; z < zPoints; z++) {

                            world.spawnParticle(Particle.REDSTONE, new Location(world, box.getMinX() + (x * (box.getWidthX() / (xPoints - 1.0F))), box.getMinY() + (y * (box.getHeight() / (yPoints - 1.0F))), box.getMinZ() + (z * (box.getWidthZ() / (zPoints - 1.0F)))), 100, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(255, 0, 0), 0.5F));


                        }
                    }
                }

            }
        });
    }

}
