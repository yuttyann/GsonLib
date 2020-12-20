package com.github.yuttyann.scriptentityplus.script;

import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.file.json.PlayerCountJson;
import com.github.yuttyann.scriptblockplus.file.json.element.PlayerCount;
import com.github.yuttyann.scriptblockplus.manager.OptionManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.ScriptType;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptblockplus.utils.unmodifiable.UnmodifiableLocation;
import com.github.yuttyann.scriptentityplus.ScriptEntity;
import com.github.yuttyann.scriptentityplus.listener.EntityListener;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class EntityScriptRead extends ScriptRead {

	private final Location scriptLocation;

	public EntityScriptRead(@NotNull Player player, @NotNull Entity entity, @NotNull Location location, @NotNull ScriptType scriptType) {
		super(player, entity.getLocation(), scriptType);
		this.scriptLocation = new UnmodifiableLocation(location);
	}

	@NotNull
	public final Entity getEntity() {
		return (Entity) Objects.requireNonNull(get(EntityListener.KEY_ENTITY));
	}

	@NotNull
	public final Location getScriptLocation() {
		return scriptLocation;
	}

	@Override
	public boolean read(int index) {
		if (!blockScript.has(scriptLocation)) {
			SBConfig.ERROR_SCRIPT_FILE_CHECK.send(sbPlayer);
			return false;
		}
		if (!sort(blockScript.get(scriptLocation).getScript())) {
			SBConfig.ERROR_SCRIPT_EXECUTE.replace(scriptType).send(sbPlayer);
			SBConfig.CONSOLE_ERROR_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptLocation, scriptType).console();
			return false;
		}
		for (scriptIndex = index; scriptIndex < script.size(); scriptIndex++) {
			if (!sbPlayer.isOnline()) {
				executeEndProcess(e -> e.failed(this));
				return false;
			}
			String script = replace(this.script.get(scriptIndex));
			Option option = OptionManager.newInstance(script);
			optionValue = setPlaceholders(getSBPlayer(), option.getValue(script));
			if (!hasPermission(option) || !option.callOption(this)) {
				executeEndProcess(e -> { if (!option.isFailedIgnore()) e.failed(this); });
				return false;
			}
		}
		executeEndProcess(e -> e.success(this));
		new PlayerCountJson(sbPlayer.getUniqueId()).action(PlayerCount::add, scriptLocation, scriptType);
		SBConfig.CONSOLE_SUCCESS_SCRIPT_EXECUTE.replace(sbPlayer.getName(), scriptLocation, scriptType).console();
		return true;
	}

	@NotNull
	private String replace(String script) {
		List<String> list = ScriptEntity.getInstance().getConfig().getStringList("Replaces");
		String result = list.stream()
				.map(s -> StringUtils.split(s, ">>>"))
				.filter(a -> a.length > 1 && script.contains(a[0]))
				.map(a -> StringUtils.replace(script, a[0], a[1]))
				.collect(Collectors.joining());
		return StringUtils.isEmpty(result) ? script : result;
	}
}