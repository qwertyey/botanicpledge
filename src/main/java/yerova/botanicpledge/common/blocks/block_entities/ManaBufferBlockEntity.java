package yerova.botanicpledge.common.blocks.block_entities;

import com.google.common.base.Predicates;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.lwjgl.opengl.GL11;
import vazkii.botania.api.BotaniaAPIClient;
import vazkii.botania.api.block.IWandHUD;
import vazkii.botania.api.block.IWandable;
import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.api.mana.IManaReceiver;
import vazkii.botania.api.mana.spark.IManaSpark;
import vazkii.botania.api.mana.spark.ISparkAttachable;
import vazkii.botania.client.core.helper.RenderHelper;
import vazkii.botania.client.fx.WispParticleData;
import vazkii.botania.client.gui.HUDHandler;
import vazkii.botania.common.block.tile.mana.IThrottledPacket;
import vazkii.botania.common.block.tile.mana.TilePool;
import vazkii.botania.common.item.ItemManaTablet;
import vazkii.botania.common.item.ModItems;
import yerova.botanicpledge.setup.BPBlockEntities;

import javax.annotation.Nullable;
import java.util.List;

public class ManaBufferBlockEntity extends BlockEntity implements IManaReceiver, ISparkAttachable, IThrottledPacket, IWandable {
    public static final int MAX_MANA = 264_000_000;
    public static final int TRANSFER_SPEED = 16000;
    private static final BlockPos[] POOL_LOCATIONS = {new BlockPos(1, 0, 0), new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0)};
    private static final String TAG_MANA = "mana";
    private static boolean sendPacket = false;
    private int mana;

    public ManaBufferBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BPBlockEntities.MANA_BUFFER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, ManaBufferBlockEntity e) {
        if(level.isClientSide) {



        } else {

            if (level.getBlockEntity(blockPos) instanceof ManaBufferBlockEntity entity) {

                if(level instanceof ServerLevel serverLevel) {
                    int color = 0x08e8de;

                    float r = (color >> 16 & 0xFF) / 255F;
                    float g = (color >> 8 & 0xFF) / 255F;
                    float b = (color & 0xFF) / 255F;

                    for (int i = 0; i < 5; i++) {
                        WispParticleData data = WispParticleData.wisp(0.7F * ((float) e.mana / MAX_MANA), r, g, b, true);

                        serverLevel.sendParticles(data,blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5,
                                10,0, 0, 0,(float) (Math.random() - 0.95F) * 0.01F);


                    }


                    for (BlockPos poolPos : POOL_LOCATIONS) {
                        if (level.getBlockEntity(blockPos.offset(poolPos)) instanceof TilePool) {


                            TilePool tilePool = (TilePool) level.getBlockEntity(blockPos.offset(poolPos));
                            int manaToGet = Math.min(TRANSFER_SPEED, tilePool.getCurrentMana());
                            int spaceLeft = Math.max(0, MAX_MANA - tilePool.getCurrentMana());
                            int current = Math.min(spaceLeft, manaToGet);
                            tilePool.receiveMana(-current);
                            ((ManaBufferBlockEntity) level.getBlockEntity(blockPos)).receiveMana(current);
                        } else if (level.getBlockEntity(blockPos.offset(poolPos)) instanceof ManaBufferBlockEntity) {
                            ManaBufferBlockEntity buffer = (ManaBufferBlockEntity) level.getBlockEntity(blockPos.offset(poolPos));
                            int manaToGet = Math.min(TRANSFER_SPEED, buffer.getCurrentMana());
                            int spaceLeft = Math.max(0, MAX_MANA - buffer.getCurrentMana());
                            int current = Math.min(spaceLeft, manaToGet);
                            buffer.receiveMana(-current);
                            ((ManaBufferBlockEntity) level.getBlockEntity(blockPos)).receiveMana(current);
                        }
                    }

                    if (level.getBlockEntity(blockPos.offset(0, 1, 0)) instanceof TilePool) {
                        ManaBufferBlockEntity sender = ((ManaBufferBlockEntity) level.getBlockEntity(blockPos));
                        TilePool receiver = (TilePool) level.getBlockEntity(blockPos.offset(0, 1, 0));
                        int manaToGet = Math.min(TRANSFER_SPEED, sender.getCurrentMana());
                        int space = Math.max(0, receiver.manaCap - receiver.getCurrentMana());
                        int current = Math.min(space, manaToGet);

                        sender.receiveMana(-current);
                        receiver.receiveMana(current);
                    } else if (level.getBlockEntity(blockPos.offset(0, 1, 0)) instanceof ManaBufferBlockEntity) {
                        ManaBufferBlockEntity sender = ((ManaBufferBlockEntity) level.getBlockEntity(blockPos));
                        ManaBufferBlockEntity receiver = (ManaBufferBlockEntity) level.getBlockEntity(blockPos.offset(0, 1, 0));
                        int manaToGet = Math.min(TRANSFER_SPEED, sender.getCurrentMana());
                        int space = Math.max(0, MAX_MANA - receiver.getCurrentMana());
                        int current = Math.min(space, manaToGet);

                        sender.receiveMana(-current);
                        receiver.receiveMana(current);
                    }
                }


            }


        }

    }



    @Override
    public Level getManaReceiverLevel() {
        return level;
    }

    @Override
    public BlockPos getManaReceiverPos() {
        return worldPosition;
    }

    @Override
    public void load(CompoundTag tag) {
        mana = tag.getInt(TAG_MANA);
    }


    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt(TAG_MANA, mana);
    }


    @Override
    public boolean canAttachSpark(ItemStack stack) {
        return true;
    }

    @Override
    public void attachSpark(IManaSpark entity) {
        ISparkAttachable.super.attachSpark(entity);
    }

    @Override
    public int getAvailableSpaceForMana() {
        int space = Math.max(0, MAX_MANA - getCurrentMana());
        if (space > 0) {
            return space;
        } else {
            return 0;
        }
    }

    @Override
    public IManaSpark getAttachedSpark() {

        List<Entity> sparks = level.getEntitiesOfClass(Entity.class, new AABB(worldPosition.above(), worldPosition.above().offset(1, 1, 1)), Predicates.instanceOf(IManaSpark.class));
        if (sparks.size() == 1) {
            Entity e = sparks.get(0);
            return (IManaSpark) e;
        }

        return null;
    }

    @Override
    public boolean areIncomingTranfersDone() {
        return false;
    }

    @Override
    public boolean isFull() {
        return getCurrentMana() >= MAX_MANA;
    }

    @Override
    public void receiveMana(int mana) {
        int old = this.mana;
        this.mana = Math.max(0, Math.min(getCurrentMana() + mana, MAX_MANA));
        if (old != this.mana) {
            markDispatchable();
        }
    }

    @Override
    public boolean canReceiveManaFromBursts() {
        return true;
    }

    @Override
    public int getCurrentMana() {
        return mana;
    }

    @Override
    public void markDispatchable() {
        sendPacket = true;
    }

    @Override
    public boolean onUsedByWand(@Nullable Player player, ItemStack stack, Direction side) {
        if (player == null || player.isShiftKeyDown()) {
            VanillaPacketDispatcher.dispatchTEToNearbyPlayers(this);
        }
        return true;
    }

    public static class WandHud implements IWandHUD {
        private final ManaBufferBlockEntity pool;

        public WandHud(ManaBufferBlockEntity pool) {
            this.pool = pool;
        }

        @Override
        public void renderHUD(PoseStack ms, Minecraft mc) {
            ItemStack poolStack = new ItemStack(pool.getBlockState().getBlock());
            String name = poolStack.getHoverName().getString();
            int color = 0x4444FF;
            BotaniaAPIClient.instance().drawSimpleManaHUD(ms, color, pool.getCurrentMana(), ManaBufferBlockEntity.MAX_MANA, name);

            int x = Minecraft.getInstance().getWindow().getGuiScaledWidth() / 2 - 11;
            int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() / 2 + 30;

            int u = 22;
            int v = 38;

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            RenderSystem.setShaderTexture(0, HUDHandler.manaBar);
            RenderHelper.drawTexturedModalRect(ms, x, y, u, v, 22, 15);
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            ItemStack tablet = new ItemStack(ModItems.manaTablet);
            ItemManaTablet.setStackCreative(tablet);

            mc.getItemRenderer().renderAndDecorateItem(tablet, x - 20, y);
            mc.getItemRenderer().renderAndDecorateItem(poolStack, x + 26, y);

            RenderSystem.disableBlend();
        }
    }
}
