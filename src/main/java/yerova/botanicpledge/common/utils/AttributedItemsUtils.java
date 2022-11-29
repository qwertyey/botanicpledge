package yerova.botanicpledge.common.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.botania.api.mana.ManaItemHandler;
import yerova.botanicpledge.common.network.Networking;
import yerova.botanicpledge.common.network.SyncProtector;




public class AttributedItemsUtils {



    public static void handleShieldRegenOnCurioTick(LivingEntity player, ItemStack stack, int maxShield, int defRegenPerTick, int maxCharge) {
        if (!(player instanceof ServerPlayer)) return;
        ServerPlayer serverPlayer = (ServerPlayer) player;

        CompoundTag stats = stack.getOrCreateTagElement(BotanicPledgeConstants.TAG_STATS_SUBSTAT);


        //Normal Stats
        stats.putInt(BotanicPledgeConstants.MAX_SHIELD_TAG_NAME, maxShield);
        stats.putInt(BotanicPledgeConstants.MAX_CHARGE_TAG_NAME, maxCharge);

        int charge = stats.getInt(BotanicPledgeConstants.CHARGE_TAG_NAME);
        if (charge < maxCharge)
            charge += ManaItemHandler.instance().requestMana(stack, serverPlayer, maxCharge - charge, true);

        int shield = stats.getInt(BotanicPledgeConstants.SHIELD_TAG_NAME);
        if (shield < maxShield) {
            if (defRegenPerTick + shield >= maxShield) defRegenPerTick = maxShield - shield;

            if (charge >= defRegenPerTick * 4) {
                charge -= defRegenPerTick * 4;
                shield += defRegenPerTick;
            }
            stats.putInt(BotanicPledgeConstants.SHIELD_TAG_NAME, shield);
        }
        stats.putInt(BotanicPledgeConstants.CHARGE_TAG_NAME, charge);
    }

    public static void SyncShieldValuesToClient(ServerPlayer serverPlayer) {
        boolean success = false;
        for (SlotResult result : CuriosApi.getCuriosHelper().findCurios(serverPlayer, "necklace", "divine_core")) {
            if (result.stack().hasTag() && result.stack().getTag().contains(BotanicPledgeConstants.TAG_STATS_SUBSTAT)) {

                CompoundTag shield = result.stack().getOrCreateTagElement(BotanicPledgeConstants.TAG_STATS_SUBSTAT);

                Networking.sendToPlayer(new SyncProtector(
                        shield.getInt(BotanicPledgeConstants.CHARGE_TAG_NAME),
                        shield.getInt(BotanicPledgeConstants.MAX_CHARGE_TAG_NAME),
                        shield.getInt(BotanicPledgeConstants.SHIELD_TAG_NAME),
                        shield.getInt(BotanicPledgeConstants.MAX_SHIELD_TAG_NAME)), serverPlayer);

                success = true;
            }
        }

        if (!success) {
            Networking.sendToPlayer(new SyncProtector(0, 0, 0, 0), serverPlayer);
        }
    }


}
