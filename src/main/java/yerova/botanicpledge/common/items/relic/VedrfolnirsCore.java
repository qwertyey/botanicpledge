package yerova.botanicpledge.common.items.relic;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import vazkii.botania.common.item.CustomCreativeTabContents;
import yerova.botanicpledge.common.capabilities.Attribute;
import yerova.botanicpledge.common.capabilities.provider.CoreAttributeProvider;
import yerova.botanicpledge.common.utils.BPItemUtils;


public class VedrfolnirsCore extends DivineCoreItem implements CustomCreativeTabContents {

    private static final int maxShield = 600;
    private static final int defRegenPerTick = 4;
    private static final int maxCharge = 2_000_000;
    private static final int manaCost = 4;


    public VedrfolnirsCore(Item.Properties properties) {
        super(properties);
    }


    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        super.curioTick(slotContext, stack);
        BPItemUtils.handleShieldRegenOnCurioTick(slotContext.entity(), stack);
        stack.getCapability(CoreAttributeProvider.CORE_ATTRIBUTE).ifPresent(attribute -> {
            attribute.setMaxCharge(getShieldValueAccordingToRank(stack, maxCharge));
            attribute.setMaxShield(getShieldValueAccordingToRank(stack, maxShield));
            attribute.setDefRegenPerTick(getShieldValueAccordingToRank(stack, defRegenPerTick));
        });
    }

    public static CoreAttributeProvider getCoreAttribute() {
        return new CoreAttributeProvider(maxCharge, maxShield, defRegenPerTick, manaCost, 4, Attribute.Rune.EquipmentType.DIVINE_CORE);
    }


    @Override
    public void addToCreativeTab(Item me, CreativeModeTab.Output output) {

        for (int i : LEVELS) {
            ItemStack full = new ItemStack(this);
            setMana(full, i);
            output.accept(full);
        }

    }
}
