package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import net.minecraft.world.item.ItemSword;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class TeleportationHandler implements Listener {


    @EventHandler
    public void onSwordTeleport(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK || !player.isSneaking())
            return;

        if (!ToolManager.hasUpgrade(usedStack, ToolUpgrade.TELEPORTATION) || !(CraftItemStack.asNMSCopy(usedStack).d() instanceof ItemSword))
            return;

        if ((((Damageable) usedStack.getItemMeta()).getDamage() >= usedStack.getType().getMaxDurability()))
            return;

        int teleportRange = ToolUpgrades.getInstance().teleportationRange();
        Location eyeLoc = player.getEyeLocation();

        Vector lookVec = eyeLoc.getDirection();
        Location destination = player.getEyeLocation().add(new Vector(teleportRange, teleportRange, teleportRange).multiply(lookVec));

        if (!player.getWorld().getBlockAt(destination).isEmpty()) {

            destination = eyeLoc.clone();
            Location prevDest;

            for (int i = 0; i < teleportRange; i++) {

                prevDest = destination.clone();
                destination.add(new Vector(1, 1, 1).multiply(lookVec));

                if (!player.getWorld().getBlockAt(destination).isEmpty() && !player.getWorld().getBlockAt(destination).isLiquid()) {
                    destination = prevDest;
                    break;
                }
            }
        }


        if (player.getWorld().getBlockAt(destination.clone().subtract(0, 2, 0)).isEmpty())
            destination.subtract(0, 2, 0);

        player.teleport(destination);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

        if (player.getGameMode() == GameMode.CREATIVE)
            return;

        Damageable meta = (Damageable) usedStack.getItemMeta();
        meta.setDamage(meta.getDamage() + 1);
        usedStack.setItemMeta(meta);

    }


    @EventHandler
    public void onArrowShoot(EntityShootBowEvent event) {

        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack bowStack = event.getBow();
        if (!ToolManager.hasUpgrade(bowStack, ToolUpgrade.TELEPORTATION))
            return;

        event.getProjectile().getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "teleportationPlayer"), PersistentDataType.STRING, player.getUniqueId().toString());
    }

    @EventHandler
    public void onArrowTeleport(ProjectileHitEvent event) {


        if (event.getHitBlock() == null)
            return;

        if (!event.getEntity().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "teleportationPlayer"), PersistentDataType.STRING))
            return;

        UUID uuid = UUID.fromString(event.getEntity().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "teleportationPlayer"), PersistentDataType.STRING));
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.getUniqueId().equals(uuid))
                return;

            player.teleport(event.getHitBlock().getLocation().add(event.getHitBlockFace().getDirection()));
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        });

    }
}
