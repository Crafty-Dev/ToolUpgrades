package de.crafty.toolupgrades.upgradehandler;

import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import org.bukkit.Material;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EnderMaskHandler implements Listener {

    @EventHandler
    public void onEnderMask(EntityTargetLivingEntityEvent event) {

        if (!(event.getEntity() instanceof Enderman man) || !(event.getTarget() instanceof Player player))
            return;

        if ((man.getLastDamageCause() != null && man.getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) || event.getReason() != EntityTargetEvent.TargetReason.CLOSEST_PLAYER || man.getTarget() != null)
            return;

        if (ToolManager.hasUpgrade(player.getInventory().getArmorContents()[3], ToolUpgrade.ENDER_MASK))
            event.setCancelled(true);

    }

}
