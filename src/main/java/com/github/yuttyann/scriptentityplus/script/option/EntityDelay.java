package com.github.yuttyann.scriptentityplus.script.option;

import com.github.yuttyann.scriptblockplus.ScriptBlock;
import com.github.yuttyann.scriptblockplus.file.config.SBConfig;
import com.github.yuttyann.scriptblockplus.manager.EndProcessManager;
import com.github.yuttyann.scriptblockplus.script.ScriptRead;
import com.github.yuttyann.scriptblockplus.script.option.Option;
import com.github.yuttyann.scriptblockplus.script.option.OptionTag;
import com.github.yuttyann.scriptblockplus.script.option.time.Delay;
import com.github.yuttyann.scriptblockplus.script.option.time.TimerTemp;
import com.github.yuttyann.scriptentityplus.script.EntityOption;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

@OptionTag(name = "entity_delay", syntax = "@e_delay:")
public class EntityDelay extends EntityOption implements Runnable {

    private boolean saveDelay;

    @Override
    @NotNull
    public Option newInstance() {
        return new EntityDelay();
    }

    @Override
    public boolean isFailedIgnore() {
        return true;
    }

    @Override
    protected boolean isValid() throws Exception {
        String[] array = getOptionValue().split("/");
        saveDelay = array.length <= 1 || Boolean.parseBoolean(array[1]);
        if (saveDelay && Delay.DELAY_SET.contains(newTimerTemp())) {
            SBConfig.ACTIVE_DELAY.send(getSBPlayer());
        } else {
            if (saveDelay) {
                Delay.DELAY_SET.add(newTimerTemp());
            }
            ((ScriptRead) getTempMap()).setInitialize(false);
            Bukkit.getScheduler().runTaskLater(ScriptBlock.getInstance(), this, Long.parseLong(array[0]));
        }
        return false;
    }

    @Override
    public void run() {
        if (saveDelay) {
            Delay.DELAY_SET.remove(newTimerTemp());
        }
        ScriptRead scriptRead = (ScriptRead) getTempMap();
        if (getSBPlayer().isOnline()) {
            scriptRead.setInitialize(true);
            scriptRead.read(getScriptIndex() + 1);
        } else {
            EndProcessManager.forEachFinally(e -> e.failed(scriptRead), () -> scriptRead.clear());
        }
    }

    private TimerTemp newTimerTemp() {
        return new TimerTemp(getUniqueId(), getScriptLocation(), getScriptKey());
    }
}