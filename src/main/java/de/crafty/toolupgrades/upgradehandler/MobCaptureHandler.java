package de.crafty.toolupgrades.upgradehandler;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.crafty.toolupgrades.ToolUpgrades;
import de.crafty.toolupgrades.api.MobCaptureEvent;
import de.crafty.toolupgrades.api.MobReleaseEvent;
import de.crafty.toolupgrades.upgrade.ToolUpgrade;
import de.crafty.toolupgrades.util.ToolManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.nbt.MojangsonParser;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.item.ItemProjectileWeapon;
import net.minecraft.world.item.ItemToolMaterial;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_19_R1.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MobCaptureHandler implements Listener {


    private final List<ItemStack> lockedItems = new ArrayList<>();

    @EventHandler
    public void onCapture(PlayerInteractAtEntityEvent event) {

        if (event.getRightClicked().getType() == EntityType.UNKNOWN)
            return;

        Player player = event.getPlayer();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (!player.isSneaking() || !ToolManager.hasUpgrade(usedStack, ToolUpgrade.MOB_CAPTURE))
            return;

        if (CraftItemStack.asNMSCopy(usedStack).c() instanceof ItemProjectileWeapon)
            return;

        if (usedStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING))
            return;

        Entity entity = event.getRightClicked();
        SpawnCategory spawnCategory = entity.getSpawnCategory();

        if (!(entity instanceof LivingEntity) || entity instanceof Player)
            return;

        int toolLevel = this.getToolLevel(usedStack);

        if (toolLevel < 2 && (spawnCategory == SpawnCategory.MONSTER || spawnCategory == SpawnCategory.WATER_UNDERGROUND_CREATURE)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a7cThis creature seems too mighty to be captured with that tool"));
            return;
        }

        if (toolLevel < 4 && (entity.getType() == EntityType.WITHER || entity.getType() == EntityType.GUARDIAN || entity.getType() == EntityType.ENDER_DRAGON)) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a7cThis creature seems too mighty to be captured with that tool"));
            return;
        }


        int requiredDurability = this.getRequiredDurability(entity);

        if (requiredDurability < 0) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a75This creature seems way to mighty"));
            return;
        }

        MobCaptureEvent e = new MobCaptureEvent(player, entity);
        Bukkit.getPluginManager().callEvent(e);

        if (e.isCancelled())
            return;

        entity = e.getCapturedEntity();

        if (usedStack.getItemMeta() instanceof Damageable meta) {

            if (meta.getDamage() > usedStack.getType().getMaxDurability() - requiredDurability)
                return;

            meta.setDamage(meta.getDamage() + requiredDurability);
            usedStack.setItemMeta(meta);
        }


        entity.remove();
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0F, 0.5F);
        ToolManager.addCapturedMobData(usedStack, entity.getType(), ((CraftEntity) entity).getHandle().f(new NBTTagCompound()));

        this.spawnParticles(entity.getLocation(), entity, Color.fromRGB(76, 76, 76));
        this.lockedItems.add(usedStack);
    }


    @EventHandler
    public void onRelease(PlayerInteractEvent event) {

        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SPAWNER)
            return;

        Player player = event.getPlayer();
        World world = player.getWorld();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (this.lockedItems.contains(usedStack)) {
            this.lockedItems.remove(usedStack);
            return;
        }

        if (CraftItemStack.asNMSCopy(usedStack).c() instanceof ItemProjectileWeapon)
            return;

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        if (!player.isSneaking() || !ToolManager.hasUpgrade(usedStack, ToolUpgrade.MOB_CAPTURE))
            return;

        if (!usedStack.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING))
            return;

        String[] data = usedStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING).split("_:_");


        EntityType type = EntityType.valueOf(data[0]);
        NBTTagCompound tag = null;
        try {
            tag = MojangsonParser.a(data[1]);
        } catch (CommandSyntaxException e) {
            Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Failed to read NBT Data");
        }

        if (tag == null)
            return;

        Location spawnLoc = player.getLocation();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {

            Block block = event.getClickedBlock();
            Block possibleSpawnBlock = world.getBlockAt(block.getLocation().clone().add(event.getBlockFace().getDirection()));
            if (possibleSpawnBlock.isEmpty() || possibleSpawnBlock.isLiquid())
                spawnLoc = possibleSpawnBlock.getLocation().add(0.5D, 0.0D, 0.5D);

        }

        Entity entity = world.spawnEntity(spawnLoc, type);

        NBTTagList listTag = new NBTTagList();
        listTag.add(NBTTagDouble.a(spawnLoc.getX()));
        listTag.add(NBTTagDouble.a(spawnLoc.getY()));
        listTag.add(NBTTagDouble.a(spawnLoc.getZ()));
        tag.a("Pos", listTag);

        ((CraftEntity) entity).getHandle().g(tag);

        Entity originalEntity = entity;
        MobReleaseEvent e = new MobReleaseEvent(player, entity);
        Bukkit.getPluginManager().callEvent(e);

        if (e.isCancelled()) {
            entity.remove();
            return;
        }

        entity = e.getReleasedEntity();

        if (!entity.equals(originalEntity))
            world.spawnEntity(spawnLoc, type);


        this.spawnParticles(spawnLoc, entity, Color.fromRGB(200, 200, 200));
        player.playSound(player.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0F, 0.5F);
        ToolManager.removeCapturedMobData(usedStack);

        event.setCancelled(true);
    }


    @EventHandler
    public void onBowCapture$0(EntityShootBowEvent event) {


        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack usedStack = event.getBow();

        if (!ToolManager.hasUpgrade(usedStack, ToolUpgrade.MOB_CAPTURE))
            return;


        Bukkit.getScheduler().scheduleSyncDelayedTask(ToolUpgrades.getInstance(), () -> {
            event.getProjectile().getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "capturer"), PersistentDataType.STRING, player.getUniqueId().toString());
            event.getProjectile().getPersistentDataContainer().set(new NamespacedKey(ToolUpgrades.getInstance(), "capturerHashCode"), PersistentDataType.INTEGER, usedStack.hashCode());
        });

    }

    @EventHandler
    public void onBowCapture$1(ProjectileHitEvent event) {


        if (event.getHitEntity() == null || !(event.getHitEntity() instanceof LivingEntity) || event.getHitEntity() instanceof Player)
            return;

        if (!event.getEntity().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "capturer"), PersistentDataType.STRING))
            return;

        Entity entity = event.getHitEntity();

        UUID uuid = UUID.fromString(event.getEntity().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturer"), PersistentDataType.STRING));

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (!player.getUniqueId().equals(uuid))
                return;


            ItemStack holdStack = player.getInventory().getItemInMainHand();
            if (holdStack.hashCode() != event.getEntity().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturerHashCode"), PersistentDataType.INTEGER))
                return;

            if (ToolManager.getCapturedMob(holdStack) != null)
                return;

            MobCaptureEvent e = new MobCaptureEvent(player, entity);
            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled())
                return;

            event.getEntity().remove();

            int requiredDurability = this.getRequiredDurability(entity);
            if(requiredDurability < 0){
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a75This creature seems way to mighty"));
                return;
            }

            Damageable meta = (Damageable) holdStack.getItemMeta();
            if (meta.getDamage() > holdStack.getType().getMaxDurability() - requiredDurability) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("\u00a7cThis Weapon is not tough enough"));
                return;
            }

            meta.setDamage(meta.getDamage() + requiredDurability);
            holdStack.setItemMeta(meta);

            Entity actualEntity = e.getCapturedEntity();
            actualEntity.remove();

            player.playSound(actualEntity.getLocation(), Sound.ENTITY_ENDER_EYE_DEATH, 1.0F, 0.5F);
            ToolManager.addCapturedMobData(holdStack, actualEntity.getType(), ((CraftEntity) actualEntity).getHandle().f(new NBTTagCompound()));

            this.spawnParticles(actualEntity.getLocation(), actualEntity, Color.fromRGB(76, 76, 76));

        });

    }


    @EventHandler
    public void onBowRelease(ProjectileHitEvent event) {

        if (!event.getEntity().getPersistentDataContainer().has(new NamespacedKey(ToolUpgrades.getInstance(), "capturer"), PersistentDataType.STRING))
            return;

        UUID uuid = UUID.fromString(event.getEntity().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturer"), PersistentDataType.STRING));

        Bukkit.getOnlinePlayers().forEach(player -> {

            if (!player.getUniqueId().equals(uuid))
                return;

            ItemStack holdStack = player.getInventory().getItemInMainHand();

            if (holdStack.hashCode() != event.getEntity().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturerHashCode"), PersistentDataType.INTEGER))
                return;

            if (ToolManager.getCapturedMob(holdStack) == null)
                return;

            String[] data = holdStack.getItemMeta().getPersistentDataContainer().get(new NamespacedKey(ToolUpgrades.getInstance(), "capturedMob"), PersistentDataType.STRING).split("_:_");


            EntityType type = EntityType.valueOf(data[0]);
            NBTTagCompound tag = null;
            try {
                tag = MojangsonParser.a(data[1]);
            } catch (CommandSyntaxException e) {
                Bukkit.getConsoleSender().sendMessage(ToolUpgrades.PREFIX + "Failed to read NBT Data");
            }

            if (tag == null)
                return;

            Location spawnLoc = event.getEntity().getLocation();

            if (event.getHitBlock() != null)
                spawnLoc = event.getHitBlock().getLocation().add(event.getHitBlockFace().getDirection()).add(0.5D, 0.0D, 0.5D);


            NBTTagList listTag = new NBTTagList();
            listTag.add(NBTTagDouble.a(spawnLoc.getX()));
            listTag.add(NBTTagDouble.a(spawnLoc.getY()));
            listTag.add(NBTTagDouble.a(spawnLoc.getZ()));
            tag.a("Pos", listTag);

            Entity entity = event.getEntity().getWorld().spawnEntity(spawnLoc, type);
            ((CraftEntity) entity).getHandle().g(tag);


            Entity originalEntity = entity;
            MobReleaseEvent e = new MobReleaseEvent(player, entity);
            Bukkit.getPluginManager().callEvent(e);

            if (e.isCancelled()) {
                entity.remove();
                return;
            }

            event.getEntity().remove();
            entity = e.getReleasedEntity();

            if (!entity.equals(originalEntity))
                event.getEntity().getWorld().spawnEntity(spawnLoc, type);

            this.spawnParticles(spawnLoc, entity, Color.fromRGB(200, 200, 200));
            player.playSound(spawnLoc, Sound.ENTITY_ENDER_EYE_DEATH, 1.0F, 0.5F);
            ToolManager.removeCapturedMobData(holdStack);

        });
    }

    @EventHandler
    public void onSpawnerTypeChange(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || event.getClickedBlock().getType() != Material.SPAWNER)
            return;


        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        World world = player.getWorld();
        ItemStack usedStack = player.getInventory().getItemInMainHand();

        if (!player.isSneaking())
            return;

        EntityType captured = ToolManager.getCapturedMob(usedStack);

        if (captured == null)
            return;

        CreatureSpawner spawner = (CreatureSpawner) block.getState();
        spawner.setSpawnedType(captured);
        spawner.update();

        for (int x = 0; x <= 2; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = 0; z <= 2; z++) {

                    world.spawnParticle(Particle.REDSTONE, new Location(world, block.getX() + (x * 0.5D), block.getY() + (y * 0.5D), block.getZ() + (z * 0.5D)), 5, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(50, 180, 255), 0.75F));

                }
            }
        }

        ToolManager.removeCapturedMobData(usedStack);
    }


    private int getToolLevel(ItemStack stack) {

        if (!(CraftItemStack.asNMSCopy(stack).c() instanceof ItemToolMaterial toolMaterial))
            return -1;

        return toolMaterial.j().d();

    }

    private void spawnParticles(Location src, Entity entity, Color color) {

        World world = src.getWorld();

        //Effect Rendering
        int particleCount = 15;
        double range = entity.getWidth();

        double angle = Math.toRadians(360.0D / particleCount);

        for (int i = 0; i < particleCount; i++) {

            Vector vec = new Vector(1.0D, 0.0D, 1.0D).rotateAroundY(angle * i).normalize();
            Vector particleVec = src.toVector().add(new Vector(range, 0, range).multiply(vec));

            world.spawnParticle(Particle.REDSTONE, new Location(world, particleVec.getX(), particleVec.getY(), particleVec.getZ()), 100, 0, 0, 0, new Particle.DustOptions(color, 0.5F));
        }

    }

    private int getRequiredDurability(Entity entity) {

        SpawnCategory category = entity.getSpawnCategory();

        if (entity.getType() == EntityType.ENDER_DRAGON || entity.getType() == EntityType.WARDEN)
            return -1;
        else if (entity.getType() == EntityType.WITHER)
            return 540;
        else if (entity.getType() == EntityType.GUARDIAN)
            return 135;
        else if (category == SpawnCategory.MONSTER || category == SpawnCategory.WATER_UNDERGROUND_CREATURE)
            return 2;

        return 1;
    }

}
