package com.github.yuttyann.scriptentityplus;

import com.github.yuttyann.scriptblockplus.enums.reflection.PackageType;
import com.github.yuttyann.scriptblockplus.utils.Utils;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public enum ArmType {
    MAIN_HAND(0),
    OFF_HAND(3);

    private final int i;

    private ArmType(int i) {
        this.i = i;
    }

    public void swingAnimation(Player player) {
        if (!Utils.isPlatform()) {
            return;
        }
        try {
            Class<?>[] array = { PackageType.NMS.getClass("Entity"), int.class };
            Constructor<?> playOutAnimation = PackageType.NMS.getConstructor("PacketPlayOutAnimation", array);
            Object handle = PackageType.CB_ENTITY.invokeMethod(player, "CraftPlayer", "getHandle");
            PackageType.sendPacket(player, playOutAnimation.newInstance(handle, i));
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}