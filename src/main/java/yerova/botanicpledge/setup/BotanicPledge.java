package yerova.botanicpledge.setup;

import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;
import yerova.botanicpledge.client.KeyBindsInit;
import yerova.botanicpledge.client.events.ForgeClientInitializer;
import yerova.botanicpledge.client.render.entities.AsgardBladeRenderer;
import yerova.botanicpledge.client.render.items.MariasCoreRenderer;
import yerova.botanicpledge.client.render.entities.YggdFocusRenderer;
import yerova.botanicpledge.client.render.entities.YggdrafoliumRenderer;
import yerova.botanicpledge.client.render.items.MarinasCoreRenderer;
import yerova.botanicpledge.client.render.screen.ProtectorHUD;
import yerova.botanicpledge.common.events.ForgeCommonInitializer;
import yerova.botanicpledge.common.network.Networking;

import static net.minecraftforge.client.gui.ForgeIngameGui.HOTBAR_ELEMENT;

@Mod(BotanicPledge.MOD_ID)
public class BotanicPledge {

    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "botanicpledge";

    public BotanicPledge() {

        IEventBus forgeBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus eventBus = MinecraftForge.EVENT_BUS;


        eventBus.addGenericListener(ItemStack.class, ForgeCommonInitializer::attachItemCaps);
        eventBus.addGenericListener(BlockEntity.class, ForgeClientInitializer::attachBeCapabilities);


        APIRegistry.setup();

        ItemInit.ITEMS.register(forgeBus);
        EntityInit.ENTITY.register(forgeBus);
        BlockInit.BLOCKS.register(forgeBus);
        BlockEntityInit.BLOCK_ENTITIES.register(forgeBus);


        forgeBus.addListener(this::setup);
        forgeBus.addListener(this::enqueueIMC);
        forgeBus.addListener(this::processIMC);
        forgeBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        Networking.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE,
                () -> new SlotTypeMessage.Builder("divine_core")
                        .icon(new ResourceLocation("curios:slot/divine_core"))
                        .priority(16)
                        .size(1)
                        .build());
    }

    private void processIMC(final InterModProcessEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {

        KeyBindsInit.register(event);

        EntityRenderers.register(EntityInit.YGGD_FOCUS.get(), YggdFocusRenderer::new);
        EntityRenderers.register(EntityInit.YGGDRAFOLIUM.get(), YggdrafoliumRenderer::new);
        EntityRenderers.register(EntityInit.ASGARD_BLADE.get(), AsgardBladeRenderer::new);


        CuriosRendererRegistry.register(ItemInit.MARIAS_CORE.get(), MariasCoreRenderer::new);
        CuriosRendererRegistry.register(ItemInit.MARINAS_CORE.get(), MarinasCoreRenderer::new);

        ItemBlockRenderTypes.setRenderLayer(BlockInit.THUNDER_LILY.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.MANA_BUFFER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.RITUAL_CENTER.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.RITUAL_PEDESTAL.get(), RenderType.translucent());
        ItemBlockRenderTypes.setRenderLayer(BlockInit.YGGDRAL_SPREADER.get(), RenderType.translucent());


        OverlayRegistry.registerOverlayAbove(HOTBAR_ELEMENT, "name", ProtectorHUD.PROTECTOR_HUD);


    }


}
