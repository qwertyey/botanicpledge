package yerova.botanicpledge.common.events;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;
import vazkii.botania.common.handler.ModSounds;
import yerova.botanicpledge.setup.BotanicPledge;
import yerova.botanicpledge.common.utils.AttributedItemsUtils;

@Mod.EventBusSubscriber(modid = BotanicPledge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttributedItemsEventHandler {


    @SubscribeEvent
    public static void handleDamage(LivingAttackEvent e) {
        if (!e.getEntityLiving().level.isClientSide) {

            if (e.getEntityLiving() instanceof Player player) {
                for (SlotResult result : CuriosApi.getCuriosHelper().findCurios(e.getEntityLiving(), "necklace", "divine_core")) {
                    ItemStack stack = result.stack();

                    if (!e.isCanceled() && stack.hasTag() && stack.getTag().contains(BotanicPledge.MOD_ID + ".stats")) {
                        CompoundTag shield = stack.getOrCreateTagElement(BotanicPledge.MOD_ID + ".stats");

                        int def = Math.max(0, shield.getInt("Shield") - (int) Math.ceil(e.getAmount()));
                        shield.putInt("Shield", def);

                        if (def <= 0) {
                            return;
                        } else {
                            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.holyCloak, SoundSource.PLAYERS, 1F, 1F);

                            e.setCanceled(true);
                        }
                    }

                }


            }
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent e) {

        // Don't do anything client side
        if (e.world.isClientSide) {
            return;
        }
        if (e.phase == TickEvent.Phase.START) {
            for (Player player : e.world.players()) {
                if (player instanceof ServerPlayer serverPlayer) {

                    AttributedItemsUtils.SyncShieldValuesToClient(serverPlayer);

                }
            }
        }
        if (e.phase == TickEvent.Phase.END) {

        }
    }

}
