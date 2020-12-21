package com.github.yuttyann.scriptentityplus.item;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public enum ToolMode {
    NORMAL_SCRIPT("NORMAL MODE"),
    DEATH_SCRIPT("DEATH MODE");

    private final String mode;

    ToolMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public static boolean isItem(@NotNull ItemStack item) {
        return ItemUtils.isItem(item, Material.BONE, s -> s.equals("§dScript Connection"));
    }

    @NotNull
    public static ItemStack getItem() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dScript Connection");
        meta.setLore(StringUtils.setListColor(SEConfig.SCRIPT_CONNECTION.getValue()));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    public static ToolMode getNextMode(@NotNull ToolMode toolMode) {
        return toolMode == NORMAL_SCRIPT ? DEATH_SCRIPT : NORMAL_SCRIPT;
    }
}