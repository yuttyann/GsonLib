package com.github.yuttyann.scriptentityplus.listener;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.listener.item.ItemAction;
import com.github.yuttyann.scriptblockplus.player.ObjectMap;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.other.ScriptAction;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import com.github.yuttyann.scriptentityplus.ArmType;
import com.github.yuttyann.scriptentityplus.Permission;
import com.github.yuttyann.scriptentityplus.item.ScriptConnection;
import com.github.yuttyann.scriptentityplus.json.ScriptEntity;
import com.github.yuttyann.scriptentityplus.json.ScriptEntityInfo;
import com.github.yuttyann.scriptentityplus.script.ScriptRead;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityListener implements Listener {

	public static final String KEY_OFF = Utils.randomUUID();
	public static final String KEY_ENTITY = Utils.randomUUID();
	public static final String KEY_CLICK_ENTITY = Utils.randomUUID();

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
			return;
		}
		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Entity entity = event.getEntity();
			ScriptEntityInfo info = new ScriptEntity(entity.getUniqueId()).getInfo();
			if (info.isInvincible()) {
				event.setCancelled(true);
			}
			if (info.getScripts().size() > 0) {
				for (String script : info.getScripts()) {
					String[] array = StringUtils.split(script, "|");
					read((Player) damager, entity, array[1], array[0], Action.LEFT_CLICK_AIR);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDeathEvent event) {
		if (event.getEntityType() != EntityType.PLAYER) {
			new ScriptEntity(event.getEntity().getUniqueId()).delete();
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
			if (ScriptConnection.is(main) && Permission.TOOL_SCRIPT_CONNECTION.has(player)) {
				ArmType.MAIN_HAND.swingAnimation(player);
				ItemAction.run(main, player, Action.RIGHT_CLICK_AIR, entity.getLocation(), true, player.isSneaking());
				event.setCancelled(true);
			} else if (ScriptConnection.is(off) && Permission.TOOL_SCRIPT_CONNECTION.has(player)) {
				try {
					objectMap.put(KEY_OFF, true);
					ArmType.OFF_HAND.swingAnimation(player);
					ItemAction.run(off, player, Action.RIGHT_CLICK_AIR, entity.getLocation(), true, player.isSneaking());
				} finally {
					objectMap.put(KEY_OFF, false);
				}
				event.setCancelled(true);
			} else {
				ScriptEntity scriptEntity = new ScriptEntity(entity.getUniqueId());
				ScriptEntityInfo info = scriptEntity.getInfo();
				if (info.getScripts().size() > 0) {
					for (String script : info.getScripts()) {
						String[] array = StringUtils.split(script, "|");
						read(player, entity, array[1], array[0], Action.RIGHT_CLICK_AIR);
					}
					event.setCancelled(true);
				}
			}
		} finally {
			objectMap.remove(KEY_CLICK_ENTITY);
		}
	}

	private void read(@NotNull Player player, @NotNull Entity entity, @NotNull String fullCoords, @NotNull String scriptType, @NotNull Action action) {
		BlockCoords blockCoords = BlockCoords.fromString(fullCoords);
		ScriptListener listener = new ScriptListener(ScriptBlock.getInstance(), ScriptType.valueOf(scriptType));
		if (!ScriptBlock.getInstance().getMapManager().containsCoords(blockCoords, listener.getScriptType())) {
			return;
		}
		ScriptRead scriptRead = new ScriptRead(player, entity, blockCoords, listener);
		scriptRead.put(ScriptAction.KEY, action);
		scriptRead.put(KEY_ENTITY, entity);
		scriptRead.read(0);
	}
}