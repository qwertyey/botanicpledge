package yerova.botanicpledge.common.items.relic;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;
import top.theillusivec4.curios.api.SlotContext;
import yerova.botanicpledge.common.utils.AttributedItemsUtils;
import yerova.botanicpledge.common.utils.BotanicPledgeConstants;

public class MarinasCore extends DivineCoreItem {

    private static final int maxShield = 3400;
    private static final int defRegenPerTick = 35;
    private static final int maxCharge = 1_000_000;

    public MarinasCore(Item.Properties properties) {
        super(properties);
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        AttributedItemsUtils.handleShieldRegenOnCurioTick(slotContext.entity(), stack, maxShield, defRegenPerTick, maxCharge);
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if(stack.getTag() == null || !(stack.getTag().contains(BotanicPledgeConstants.TAG_STATS_SUBSTAT))){
            stack.getOrCreateTagElement(BotanicPledgeConstants.TAG_STATS_SUBSTAT).merge(BotanicPledgeConstants.INIT_CORE_SHIELD_TAG(maxCharge, maxShield));
        }
        return super.initCapabilities(stack, nbt);
    }

}
