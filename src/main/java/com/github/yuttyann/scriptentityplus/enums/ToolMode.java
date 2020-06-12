package com.github.yuttyann.scriptentityplus.enums;

import com.github.yuttyann.scriptblockplus.utils.ItemUtils;
import com.github.yuttyann.scriptblockplus.utils.StreamUtils;
import com.github.yuttyann.scriptblockplus.utils.StringUtils;
import com.github.yuttyann.scriptentityplus.file.SEConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum ToolMode {
    NORMAL_SCRIPT("NORMAL MODE"),
    DEATH_SCRIPT("DEATH MODE");

    private final String mode;

    private ToolMode(String mode) {
        this.mode = mode;
    }

    public String getMode() {
        return mode;
    }

    public boolean isItem(@NotNull ItemStack item) {
        return ItemUtils.isItem(item, Material.BONE, s -> s.equals("§dScript Connection§6[" + mode + "]"));
    }

    public static boolean has(@NotNull ItemStack item) {
        return StreamUtils.anyMatch(values(), t -> t.isItem(item));
    }

    @NotNull
    public ItemStack getItem() {
        ItemStack item = new ItemStack(Material.BONE);
        ItemMeta meta = Objects.requireNonNull(item.getItemMeta());
        meta.setDisplayName("§dScript Connection§6[" + mode + "]");
        List<String> lore = new ArrayList<>(SEConfig.SCRIPT_CONNECTION.getValue());
        for (int i = 0; i < lore.size(); i++) {
            lore.set(i, StringUtils.setColor(lore.get(i), true));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getNextItem(@NotNull ItemStack item) {
        ToolMode toolMode = getType(item);
        int nextOridinal = toolMode.ordinal() + 1;
        toolMode = Arrays.stream(values())
                .filter(t -> t.ordinal() == nextOridinal)
                .findFirst()
                .orElse(ToolMode.NORMAL_SCRIPT);
        return toolMode.getItem();
    }

    @NotNull
    public static ToolMode getType(@NotNull ItemStack item) {
        String name = ItemUtils.getName(item);
        return Objects.requireNonNull(StreamUtils.fOrElse(values(), t -> name.contains(t.mode), null));
    }
}