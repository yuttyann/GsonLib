package com.github.yuttyann.scriptentityplus.script;

import com.github.yuttyann.scriptblockplus.BlockCoords;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCount;
import com.github.yuttyann.scriptblockplus.listener.ScriptListener;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.player.SBPlayer;
import com.github.yuttyann.scriptblockplus.script.ScriptData;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.Main;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ScriptRead extends com.github.yuttyann.scriptblockplus.script.ScriptRead {

	private final BlockCoords location;

	public ScriptRead(Player player, Entity entity, Location script, ScriptListener listener) {
		super(listener);
		this.sbPlayer = SBPlayer.fromPlayer(player);
		this.scriptData = new ScriptData(script, scriptType, true);
		this.location = new BlockCoords(script).unmodifiable();
		this.blockCoords = new BlockCoords(entity.getLocation()).unmodifiable();
	}

	@NotNull
	public final Entity getEntity() {
		return (Entity) Objects.requireNonNull(get(EntityListener.KEY_ENTITY));
	}

	@NotNull
	public final BlockCoords getScriptLocation() {
		return location;
	}

	@Override
	public boolean read(int index) {
		Validate.notNull(sbPlayer.getPlayer(), "Player cannot be null");
		if (!scriptData.hasPath()) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return false;
		}
		if (!sort(scriptData.getScripts())) {
			SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType).send(sbPlayer);
			SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, blockCoords).console();
			return false;
		}
		for (scriptIndex = index; scriptIndex < scripts.size(); scriptIndex++) {
			if (!sbPlayer.isOnline() || getEntity().isDead()) {
				executeEndProcess(e -> e.failed(this));
				return false;
			}
			String script = replace(scripts.get(scriptIndex));
			Option option = OptionManager.get(script).newInstance();
			optionValue = setPlaceholders(getSBPlayer(), option.getValue(script));
			if (!hasPermission(option) || !option.callOption(this)) {
				executeEndProcess(e -> { if (!option.isFailedIgnore()) e.failed(this); });
				return false;
			}
		}
		executeEndProcess(e -> e.success(this));
		new PlayerCount(sbPlayer.getUniqueId()).add(getScriptLocation(), scriptType);
		SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptType, getScriptLocation()).console();
		return true;
	}

	@NotNull
	private String replace(String script) {
		List<String> list = Main.getInstance().getConfig().getStringList("Replaces");
		String result = list.stream()
				.map(s -> StringUtils.split(s, ">>>"))
				.filter(a -> a.length > 1 && script.contains(a[0]))
				.map(a -> StringUtils.replace(script, a[0], a[1]))
				.collect(Collectors.joining());
		return StringUtils.isEmpty(result) ? script : result;
	}
}