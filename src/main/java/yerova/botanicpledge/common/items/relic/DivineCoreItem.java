package yerova.botanicpledge.common.items.relic;


import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;
import vazkii.botania.api.item.IRelic;
import vazkii.botania.api.mana.ManaItemHandler;
import vazkii.botania.common.item.relic.ItemRelic;
import vazkii.botania.common.item.relic.RelicImpl;
import yerova.botanicpledge.common.utils.BPConstants;
import yerova.botanicpledge.common.utils.PlayerUtils;

import java.util.List;
import java.util.UUID;

public class DivineCoreItem extends ItemRelic implements ICurioItem {

    public DivineCoreItem(Properties props) {
        super(props);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        ICurioItem.super.curioTick(slotContext, stack);
        if (slotContext.entity() instanceof Player player) {

            //Draconic Evolution Armor should not be used together with this mod, because it might lead to total unbalance of Power
            if (!PlayerUtils.checkForArmorFromMod(player, BPConstants.DRACONIC_EVOLUTION_MODID)) {

                if (player.tickCount % 20 == 0) {
                    if (player.flyDist > 0) {
                        setManaCost(stack, getManaCost(stack) * BPConstants.MANA_TICK_COST_WHILE_FLIGHT_CONVERSION_RATE);
                    }
                    ManaItemHandler.instance().requestManaExact(stack, player, getManaCost(stack), true);
                }
            }
        }
    }

    public static IRelic makeRelic(ItemStack stack) {
        return new RelicImpl(stack, null);
    }

    public static UUID getCoreUUID(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME);
        String tagCoreUuidMostLegacy = "coreUUIDMost";
        String tagCoreUuidLeastLegacy = "coreUUIDLeast";
        if (tag.contains(tagCoreUuidMostLegacy) && tag.contains(tagCoreUuidLeastLegacy)) {
            UUID uuid = new UUID(tag.getLong(tagCoreUuidMostLegacy), tag.getLong(tagCoreUuidLeastLegacy));
            tag.putUUID(BPConstants.TAG_CORE_UUID, uuid);
        }
        if (!tag.hasUUID(BPConstants.TAG_CORE_UUID)) {
            UUID uuid = UUID.randomUUID();
            tag.putUUID(BPConstants.TAG_CORE_UUID, uuid);
        }
        return tag.getUUID(BPConstants.TAG_CORE_UUID);
    }

    @Override
    public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {

        if (slotContext.entity() instanceof Player player) {

            //Draconic Evolution Armor should not be used together with this mod, because it might lead to total unbalance of Power
            if (!PlayerUtils.checkForArmorFromMod(player, BPConstants.DRACONIC_EVOLUTION_MODID)) {

                for (int i = 0; i < BPConstants.ATTRIBUTE_LIST().size(); i++) {
                    double addValue = getAttributeValueFromAttributeLevel(stack, BPConstants.attributeNames().get(i));
                    AttributeModifier statModifier = new AttributeModifier(getCoreUUID(stack), "Divine Core", addValue, AttributeModifier.Operation.ADDITION);
                    if (!player.getAttribute(BPConstants.ATTRIBUTE_LIST().get(i)).hasModifier(statModifier)) {
                        player.getAttribute(BPConstants.ATTRIBUTE_LIST().get(i)).addPermanentModifier(statModifier);
                    }
                }
                if (stack.getTag().contains(BPConstants.STATS_TAG_NAME)
                        && stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME).contains("may_fly")) {
                    this.startFlying(player);
                }
            }
        }
    }

    @Override
    public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {

        if (slotContext.entity() instanceof Player player) {

            //Draconic Evolution Armor should not be used together with this mod, because it might lead to total unbalance of Power
            if(!PlayerUtils.checkForArmorFromMod(player, BPConstants.DRACONIC_EVOLUTION_MODID)) {
                if (newStack.getItem() != stack.getItem()) {
                    for (int i = 0; i < BPConstants.ATTRIBUTE_LIST().size(); i++) {
                        double reducerValue = getAttributeValueFromAttributeLevel(stack, BPConstants.attributeNames().get(i));
                        AttributeModifier statModifier = new AttributeModifier(getCoreUUID(stack), "Divine Core", reducerValue, AttributeModifier.Operation.ADDITION);
                        AttributeInstance statAttribute = player.getAttribute(BPConstants.ATTRIBUTE_LIST().get(i));
                        if (statAttribute.hasModifier(statModifier)) {
                            statAttribute.removeModifier(statModifier);
                            if (BPConstants.attributeNames().get(i).equals(BPConstants.attributeNames().get(2))) {
                                if (player.getHealth() > slotContext.entity().getMaxHealth()) {
                                    player.hurt(BPConstants.HEALTH_SET_DMG_SRC, slotContext.entity().getAbsorptionAmount() + (float) reducerValue);
                                }
                            }
                        }
                    }
                    if (stack.getTag().contains(BPConstants.STATS_TAG_NAME)
                            && stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME).contains("may_fly")) {
                        this.stopFlying(player);
                    }
                }
            }
        }
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return !PlayerUtils.checkForArmorFromMod((Player) slotContext.entity(), BPConstants.DRACONIC_EVOLUTION_MODID);
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return !PlayerUtils.checkForArmorFromMod((Player) slotContext.entity(), BPConstants.DRACONIC_EVOLUTION_MODID);
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return !PlayerUtils.checkForArmorFromMod((Player) slotContext.entity(), BPConstants.DRACONIC_EVOLUTION_MODID);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flags) {
        CompoundTag statsTag = getStatsSubstat(stack);
        int maximum = getMaxAttributeLevel(stack);

        if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
            for (String s : statsTag.getAllKeys()) {
                if (BPConstants.attributeNames().contains(s)) {
                    tooltip.add(new TextComponent(
                            new TranslatableComponent(s).getString() +
                                    ": Level: " + getStatsSubstat(stack).getInt(s) +
                                    " / " + maximum).withStyle(ChatFormatting.BLUE));
                }
                if (s.equals(BPConstants.JUMP_HEIGHT_TAG_NAME)) {
                    tooltip.add(new TextComponent(
                            new TranslatableComponent(BPConstants.JUMP_HEIGHT_TAG_NAME).getString() +
                                    ": Level: " + getStatsSubstat(stack).getInt(BPConstants.JUMP_HEIGHT_TAG_NAME) +
                                    " / " + maximum).withStyle(ChatFormatting.BLUE));
                }
                if (s.equals(BPConstants.MAY_FLY_TAG_NAME)) {
                    tooltip.add(new TextComponent(
                            new TranslatableComponent(BPConstants.MAY_FLY_TAG_NAME).getString() +
                                    ": Level: " + getStatsSubstat(stack).getInt(BPConstants.MAY_FLY_TAG_NAME) +
                                    " / " + maximum).withStyle(ChatFormatting.BLUE));
                }
            }
        }

        if (Screen.hasShiftDown() && !Screen.hasControlDown()) {

            for (String s : statsTag.getAllKeys()) {
                if (BPConstants.attributeNames().contains(s)) {
                    tooltip.add(new TextComponent("+" + getAttributeValueFromAttributeLevel(stack, s) +
                            " " + new TranslatableComponent(s).getString()).withStyle(ChatFormatting.BLUE));
                }
                if (s.equals(BPConstants.JUMP_HEIGHT_TAG_NAME)) {
                    tooltip.add(new TextComponent("+" + getAttributeValueFromAttributeLevel(stack, s) +
                            " " + new TranslatableComponent(BPConstants.JUMP_HEIGHT_TAG_NAME).getString()).withStyle(ChatFormatting.BLUE));
                }
                if (s.equals(BPConstants.MAY_FLY_TAG_NAME)) {
                    tooltip.add(new TextComponent(new TranslatableComponent(BPConstants.MAY_FLY_TAG_NAME).getString()).withStyle(ChatFormatting.BLUE));
                }
            }

        }

        if ((!Screen.hasShiftDown() && !Screen.hasControlDown()) || (Screen.hasShiftDown() && Screen.hasControlDown())) {
            tooltip.add(new TranslatableComponent("tooltip_core_rank",
                    new TextComponent(String.valueOf(DivineCoreItem.getCoreRank(stack))).withStyle(ChatFormatting.YELLOW),
                    new TextComponent(String.valueOf(BPConstants.MAX_CORE_RANK)).withStyle(ChatFormatting.YELLOW))
                    .withStyle(ChatFormatting.LIGHT_PURPLE));
            tooltip.add(new TextComponent(""));
            tooltip.add(new TranslatableComponent("tooltip_mana_cost", new TextComponent(String.valueOf(this.getManaCost(stack))).withStyle(ChatFormatting.AQUA)));
            tooltip.add(new TranslatableComponent("show_tooltip_stat_values", new TextComponent("LShift").withStyle(ChatFormatting.BLUE)));
            tooltip.add(new TranslatableComponent("show_tooltip_stat_levels", new TextComponent("Control").withStyle(ChatFormatting.BLUE)));
        }
        super.appendHoverText(stack, world, tooltip, flags);
    }

    private void startFlying(Player player) {
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();


    }

    private void stopFlying(Player player) {
        if (player.isSpectator() || player.isCreative()) return;
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
    }

    public static void setManaCost(ItemStack stack, int manaCost) {
        if (stack.getItem() instanceof DivineCoreItem) {
            stack.getOrCreateTagElement(BPConstants.SHIELD_TAG_NAME).putInt(BPConstants.MANA_COST_TAG_NAME, manaCost);
        }
    }

    public int getManaCost(ItemStack stack) {
        return Math.max(stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME).getInt(BPConstants.MANA_COST_TAG_NAME), BPConstants.BASIC_MANA_COST);
    }

    public static int getCoreRank(ItemStack stack) {
        int coreRank = BPConstants.MIN_CORE_RANK;
        if (stack.getItem() instanceof DivineCoreItem && stack.getTag() != null && stack.getTag().contains(BPConstants.STATS_TAG_NAME)) {
            coreRank = stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME).getInt(BPConstants.CORE_RANK_TAG_NAME);
            if (coreRank >= BPConstants.MAX_CORE_RANK) {
                coreRank = BPConstants.MAX_CORE_RANK;
            } else if (coreRank <= BPConstants.MIN_CORE_RANK) {
                coreRank = BPConstants.MIN_CORE_RANK;
            }
        }
        return coreRank;
    }

    public static void setCoreRank(ItemStack stack, int toSetRank) {
        if (toSetRank >= BPConstants.MAX_CORE_RANK) {
            toSetRank = BPConstants.MAX_CORE_RANK;
        } else if (toSetRank <= BPConstants.MIN_CORE_RANK) {
            toSetRank = BPConstants.MIN_CORE_RANK;
        }
        if (stack.getItem() instanceof DivineCoreItem) {
            stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME).putInt(BPConstants.CORE_RANK_TAG_NAME, toSetRank);
        }
    }

    public static void levelUpCoreAttribute(ItemStack stack, String attributeName, int amount) {
        if (stack.getItem() instanceof DivineCoreItem) {
            int level = getStatsSubstat(stack).getInt(attributeName) + amount;
            if (level <= getMaxAttributeLevel(stack) && level > 0) {
                getStatsSubstat(stack).putInt(attributeName, level);
            }
        }
    }

    public static int getMaxAttributeLevel(ItemStack stack) {
        int maxLevel = -1;
        if (stack.getItem() instanceof DivineCoreItem) {
            maxLevel = DivineCoreItem.getCoreRank(stack) * BPConstants.CORE_MAX_LEVEL_INCREASE_PER_RANK;
        }
        return maxLevel;
    }

    public static CompoundTag getStatsSubstat(ItemStack stack) {
        CompoundTag bpTag = ItemStack.EMPTY.getTag();
        if (stack.getItem() instanceof DivineCoreItem && stack.getTag() != null && stack.getTag().contains(BPConstants.STATS_TAG_NAME)) {
            bpTag = stack.getOrCreateTagElement(BPConstants.STATS_TAG_NAME);
        }
        return bpTag;
    }

    public static int getShieldValueAccordingToRank(ItemStack stack, int defaultValue) {
        int toReturn = 0;
        if (stack.getItem() instanceof DivineCoreItem) {
            toReturn = DivineCoreItem.getCoreRank(stack) * defaultValue;
        }
        return toReturn;
    }

    public static boolean levelUpAttributePossible(ItemStack stack, String attributeName, int value) {
        if (!(stack.getItem() instanceof DivineCoreItem)) return false;
        return getMaxAttributeLevel(stack) >= getStatsSubstat(stack).getInt(attributeName) + value;
    }

    public static double getAttributeValueFromAttributeLevel(ItemStack stack, String attributeName) {
        int attributeLevel = getStatsSubstat(stack).getInt(attributeName);

        return switch (attributeName) {
            case (BPConstants.ARMOR_TAG_NAME) -> attributeLevel * BPConstants.ARMOR_LEVEL_UP_VALUE;
            case (BPConstants.ARMOR_TOUGHNESS_TAG_NAME) -> attributeLevel * BPConstants.ARMOR_TOUGHNESS_LEVEL_UP_VALUE;
            case (BPConstants.MAX_HEALTH_TAG_NAME) -> attributeLevel * BPConstants.MAX_HEALTH_LEVEL_UP_VALUE;
            case (BPConstants.ATTACK_DAMAGE_TAG_NAME) -> attributeLevel * BPConstants.ATTACK_DAMAGE_LEVEL_UP_VALUE;
            case (BPConstants.KNOCKBACK_RESISTANCE_TAG_NAME) ->
                    attributeLevel * BPConstants.KNOCKBACK_RESISTANCE_LEVEL_UP_VALUE;
            case (BPConstants.MOVEMENT_SPEED_TAG_NAME) -> attributeLevel * BPConstants.MOVEMENT_SPEED_LEVEL_UP_VALUE;
            case (BPConstants.ATTACK_SPEED_TAG_NAME) -> attributeLevel * BPConstants.ATTACK_SPEED_LEVEL_UP_VALUE;
            case (BPConstants.MAY_FLY_TAG_NAME) -> attributeLevel * BPConstants.MAY_FLY_LEVEL_UP_VALUE;
            case (BPConstants.JUMP_HEIGHT_TAG_NAME) -> attributeLevel * BPConstants.JUMP_HEIGHT_LEVEL_UP_VALUE;
            default -> 0;
        };
    }

    public static boolean playerHasCoreWithRankEquipped(Player player, int rank) {
        boolean returner = false;
        for (SlotResult result : CuriosApi.getCuriosHelper().findCurios(player, "divine_core")) {
            ItemStack stack = result.stack();
            if (stack.getItem() instanceof DivineCoreItem && DivineCoreItem.getCoreRank(stack) >= rank) {
                returner = true;
            }
        }
        return returner;
    }

}
