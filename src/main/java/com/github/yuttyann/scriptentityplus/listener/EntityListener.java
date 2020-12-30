package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.json.BlockScriptJson;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.item.ToolMode;
import com.github.yuttyann.scriptentityplus.json.EntityScript;
import com.github.yuttyann.scriptentityplus.json.EntityScriptJson;
import com.github.yuttyann.scriptentityplus.script.EntityScriptRead;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class EntityListener implements Listener {

    public static final Set<Entity> TEMP_ENTITIES = new HashSet<>();

    public static final String KEY_OFF = Utils.randomUUID();
    public static final String KEY_ENTITY = Utils.randomUUID();
    public static final String KEY_CLICK_ENTITY = Utils.randomUUID();

    @EventHandler(priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (event.getRemover() != null) {
            damageEvent(event, event.getRemover(), event.getEntity(), 0.0D);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            return;
        }
        damageEvent(event, event.getDamager(), event.getEntity(), event.getDamage());
    }

    private void damageEvent(@NotNull Cancellable event, @NotNull Entity damager, @NotNull Entity entity, final double damage) {
        EntityScriptJson entityScriptJson = new EntityScriptJson(entity.getUniqueId());
        if (!entityScriptJson.exists()) {
            return;
        }
        EntityScript info = entityScriptJson.load();
        if (info.isInvincible()) {
            event.setCancelled(true);
        }
        if (info.isProjectile()) {
            if (!(damager instanceof Projectile)) {
                return;
            }
            damager = (Entity) ((Projectile) damager).getShooter();
        }
        if (damager instanceof Player) {
            ToolMode toolMode = ToolMode.NORMAL_SCRIPT;
            if (!event.isCancelled()) {
                if (entity instanceof LivingEntity) {
                    if (entity instanceof ArmorStand) {
                        entity.remove();
                    }
                    if (entity.isDead() || ((LivingEntity) entity).getHealth() - damage < 1.0D) {
                        toolMode = ToolMode.DEATH_SCRIPT;
                    }
                } else {
                    toolMode = ToolMode.DEATH_SCRIPT;
                }
            }
            try {
                if (toolMode == ToolMode.DEATH_SCRIPT) {
                    TEMP_ENTITIES.add(entity = ScriptEntity.getInstance().createArmorStand(entity.getLocation()));
                }
                if (info.getScripts(toolMode).size() > 0) {
                    for (String script : info.getScripts(toolMode)) {
                        read((Player) damager, entity, StringUtils.split(script, "|"), Action.LEFT_CLICK_AIR);
                    }
                }
            } finally {
                TEMP_ENTITIES.forEach(Entity::remove);
                TEMP_ENTITIES.clear();
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) {
            new EntityScriptJson(event.getEntity().getUniqueId()).deleteFile();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getType() == EntityType.ARMOR_STAND) {
            onPlayerInteractEntity(event);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            event.setCancelled(true);
            return;
        }
        Player player = event.getPlayer();
        Entity entity = event.getRightClicked();
        ObjectMap objectMap = SBPlayer.fromPlayer(player).getObjectMap();
        try {
            objectMap.put(KEY_CLICK_ENTITY, entity);
            ItemStack main = player.getInventory().getItemInMainHand();
            ItemStack off = player.getInventory().getItemInOffHand();
            if (ToolMode.isItem(main) && ItemAction.has(player, main, true)) {
                ItemAction.callRun(player, main, entity.getLocation(), Action.RIGHT_CLICK_AIR);
                event.setCancelled(true);
            } else if (ToolMode.isItem(off) && ItemAction.has(player, off, true)) {
                try {
                    objectMap.put(KEY_OFF, true);
                    ItemAction.callRun(player, off, entity.getLocation(), Action.RIGHT_CLICK_AIR);
                } finally {
                    objectMap.put(KEY_OFF, false);
                }
                event.setCancelled(true);
            } else {
                EntityScript entityScript = new EntityScriptJson(entity.getUniqueId()).load();
                if (entityScript.getScripts(ToolMode.NORMAL_SCRIPT).size() > 0) {
                    if (!entityScript.isProjectile()) {
                        for (String script : entityScript.getScripts(ToolMode.NORMAL_SCRIPT)) {
                            read(player, entity, StringUtils.split(script, "|"), Action.RIGHT_CLICK_AIR);
                        }
                    }
                    event.setCancelled(true);
                }
            }
        } finally {
            objectMap.remove(KEY_CLICK_ENTITY);
        }
    }

    private void read(@NotNull Player player, @NotNull Entity entity, @NotNull String[] array, @NotNull Action action) {
        Location location = BlockCoords.fromString(array[1]);
        if (!BlockScriptJson.has(location, ScriptType.valueOf(array[0]))) {
            return;
        }
        EntityScriptRead entityScriptRead = new EntityScriptRead(player, entity, location, ScriptType.valueOf(array[0]));
        entityScriptRead.put(ScriptAction.KEY, action);
        entityScriptRead.put(KEY_ENTITY, entity);
        entityScriptRead.read(0);
    }
}