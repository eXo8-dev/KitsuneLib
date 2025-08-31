package me.eXo8_.kitsunelib.builder.impl;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.common.collect.Multimap;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import me.eXo8_.kitsunelib.builder.Builder;
import me.eXo8_.kitsunelib.utils.ColorUtil;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ItemBuilder implements Builder<ItemStack>
{
    private final ItemStack item;
    private final ItemMeta meta;

    public ItemBuilder(ItemStack item)
    {
        this.item = item;
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(Material material) {
        this(new ItemStack(material));
    }

    public ItemBuilder setAmount(int amount)
    {
        item.setAmount(amount);
        return this;
    }

    @ApiStatus.Experimental
    public <T> ItemBuilder setData(DataComponentType.@NotNull Valued<T> type, @NotNull DataComponentBuilder<T> valueBuilder) {
        item.setData(type, valueBuilder);
        return this;
    }

    @ApiStatus.Experimental
    public <T> ItemBuilder setData(DataComponentType.@NotNull Valued<T> type, @NotNull T value) {
        item.setData(type, value);
        return this;
    }

    @ApiStatus.Experimental
    public ItemBuilder setData(DataComponentType.@NotNull NonValued type) {
        item.setData(type);
        return this;
    }

    public ItemBuilder setCustomModelData(@Nullable Integer data) {
        meta.setCustomModelData(data);
        return this;
    }

    public ItemBuilder setEnchantable(@Nullable Integer enchantable) {
        meta.setEnchantable(enchantable);
        return this;
    }

    public ItemBuilder setTooltipStyle(@Nullable NamespacedKey tooltipStyle) {
        meta.setTooltipStyle(tooltipStyle);
        return this;
    }

    public ItemBuilder setItemModel(@Nullable NamespacedKey itemModel) {
        meta.setItemModel(itemModel);
        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    public ItemBuilder customName(@Nullable Component customName) {
        meta.customName(ColorUtil.parse(customName));
        return this;
    }

    @ApiStatus.Obsolete(since = "1.21.4")
    public ItemBuilder displayName(@Nullable Component displayName) {
        meta.displayName(ColorUtil.parse(displayName));
        return this;
    }

    @Deprecated
    public ItemBuilder setDisplayName(String string) {
        meta.setDisplayName(ColorUtil.parse(string));
        return this;
    }

    public ItemBuilder itemName(@Nullable Component name) {
        meta.itemName(ColorUtil.parse(name));
        return this;
    }

    @Deprecated
    public ItemBuilder setItemName(String string) {
        meta.setItemName(ColorUtil.parse(string));
        return this;
    }

    @Deprecated
    public ItemBuilder setLore(@Nullable List<String> lore)
    {
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(ColorUtil.parse(s));
        meta.setLore(l);
        return this;
    }


    public ItemBuilder setLore(String... lore)
    {
        List<String> l = new ArrayList<>();
        for (String s : lore) l.add(ColorUtil.parse(s));
        meta.setLore(l);
        return this;
    }

    public ItemBuilder setItemFlags(Set<ItemFlag> itemsFlags)
    {
        for (ItemFlag itemsFlag : itemsFlags) meta.addItemFlags(itemsFlag);
        return this;
    }

    public ItemBuilder setPersistent(NamespacedKey key, PersistentDataType type, Object value)
    {
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(key, type, value);
        return this;
    }

    public ItemBuilder setPersistent(Map<String, String> map)
    {
        if (map != null && !map.isEmpty())
            map.forEach((key, value) -> meta.getPersistentDataContainer().set(new NamespacedKey(key, key), PersistentDataType.STRING, value));
        return this;
    }

    public ItemBuilder setEnchantments(Map<Enchantment, Integer> enchantments)
    {
        if (!enchantments.isEmpty())
        {
            if (item.getType() == Material.ENCHANTED_BOOK)
            {
                EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                enchantments.keySet().forEach(enchantment -> {
                    if (enchantment != null) enchantmentStorageMeta.addStoredEnchant(enchantment, enchantments.get(enchantment), false);
                    item.setItemMeta(enchantmentStorageMeta);
                });
            }
            else
            {
                enchantments.forEach((enchantment, lvl) -> {
                    meta.addEnchant(enchantment, lvl ,true);
                    item.setItemMeta(meta);
                });
            }
        }
        return this;
    }

    public ItemBuilder setMaxStackSize(int i)
    {
        if (i <= 0) return this;
        meta.setMaxStackSize(i);
        return this;
    }

    public ItemBuilder setEquipmentSlot(EquipmentSlot slot)
    {
        EquippableComponent equippableComponent = meta.getEquippable();
        equippableComponent.setSlot(slot);
        meta.setEquippable(equippableComponent);
        return this;
    }

    public ItemBuilder setEquippableModel(NamespacedKey nk) {
        EquippableComponent equippableComponent = meta.getEquippable();
        equippableComponent.setModel(nk);
        meta.setEquippable(equippableComponent);
        return this;
    }


    public ItemBuilder setAttributeModifier(Map<Attribute, AttributeModifier> map)
    {
        for (Map.Entry<Attribute, AttributeModifier> entry : map.entrySet())
            meta.addAttributeModifier(entry.getKey(), entry.getValue());

        return this;
    }

    public ItemBuilder setSkull(String value)
    {
        if (item.getType() != Material.PLAYER_HEAD) return this;
        if (!(meta instanceof SkullMeta skullMeta)) return this;

        UUID uuid = UUID.randomUUID();
        PlayerProfile profile = Bukkit.createProfile(uuid, uuid.toString().substring(0, 16));
        profile.setProperty(new ProfileProperty("textures", value));
        skullMeta.setPlayerProfile(profile);

        return this;
    }

    public ItemBuilder setSkull(OfflinePlayer offlinePlayer)
    {
        if (item.getType() != Material.PLAYER_HEAD) return this;

        SkullMeta skullMeta = (SkullMeta) meta;
        skullMeta.setOwningPlayer(offlinePlayer);

        return this;

    }
    public ItemBuilder setEffects(List<PotionEffect> potionEffects) {
        if (!item.getType().name().contains("POTION")) return this;

        PotionMeta potionMeta = (PotionMeta) meta;
        potionEffects.forEach(potionEffect -> potionMeta.addCustomEffect(potionEffect, false));
        item.setItemMeta(meta);
        return this;
    }



    @Deprecated
    public ItemBuilder setLoreComponents(@Nullable List<BaseComponent[]> lore) {
        meta.setLoreComponents(lore);
        return this;
    }

    public ItemBuilder setCustomModelDataComponent(@Nullable CustomModelDataComponent customModelData) {
        meta.setCustomModelDataComponent(customModelData);
        return this;
    }

    public ItemBuilder removeEnchantments() {
        meta.removeEnchantments();
        return this;
    }
    public ItemBuilder addItemFlags(@NotNull ItemFlag... itemFlags) {
        meta.addItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder removeItemFlags(@NotNull ItemFlag... itemFlags) {
        meta.removeItemFlags(itemFlags);
        return this;
    }

    public ItemBuilder setHideTooltip(boolean hideTooltip) {
        meta.setHideTooltip(hideTooltip);
        return this;
    }

    public ItemBuilder setEnchantmentGlintOverride(@Nullable Boolean override) {
        meta.setEnchantmentGlintOverride(override);
        return this;
    }

    public ItemBuilder setGlider(boolean glider) {
        meta.setGlider(glider);
        return this;
    }

    @Deprecated(since = "1.21.2")
    public ItemBuilder setFireResistant(boolean fireResistant) {
        meta.setFireResistant(fireResistant);
        return this;
    }

    public ItemBuilder setDamageResistant(@Nullable Tag<DamageType> tag) {
        meta.setDamageResistant(tag);
        return this;
    }

    public ItemBuilder setMaxStackSize(@Nullable Integer max) {
        meta.setMaxStackSize(max);
        return this;
    }

    public ItemBuilder setRarity(@Nullable ItemRarity rarity) {
        meta.setRarity(rarity);
        return this;
    }

    public ItemBuilder setUseRemainder(@Nullable ItemStack remainder) {
        meta.setUseRemainder(remainder);
        return this;
    }

    public ItemBuilder setUseCooldown(@Nullable UseCooldownComponent cooldown) {
        meta.setUseCooldown(cooldown);
        return this;
    }

    public ItemBuilder setFood(@Nullable FoodComponent food) {
        meta.setFood(food);
        return this;
    }

    public ItemBuilder setTool(@Nullable ToolComponent tool) {
        meta.setTool(tool);
        return this;
    }

    public ItemBuilder setEquippable(@Nullable EquippableComponent equippable) {
        meta.setEquippable(equippable);
        return this;
    }

    public ItemBuilder setJukeboxPlayable(@Nullable JukeboxPlayableComponent jukeboxPlayable) {
        meta.setJukeboxPlayable(jukeboxPlayable);
        return this;
    }

    public ItemBuilder setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> attributeModifiers) {
        meta.setAttributeModifiers(attributeModifiers);
        return this;
    }

    @ApiStatus.Internal
    public ItemBuilder setVersion(int version) {
        meta.setVersion(version);
        return this;
    }

    @Deprecated(forRemoval = true, since = "1.14")
    public ItemBuilder setCanDestroy(Set<Material> canDestroy) {
        meta.setCanDestroy(canDestroy);
        return this;
    }

    @Deprecated(forRemoval = true, since = "1.14")
    public ItemBuilder setCanPlaceOn(Set<Material> canPlaceOn) {
        meta.setCanPlaceOn(canPlaceOn);
        return this;
    }

    @Override
    public ItemStack build()
    {
        item.setItemMeta(meta);
        return item;
    }
}
