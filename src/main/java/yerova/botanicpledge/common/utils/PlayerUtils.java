package yerova.botanicpledge.common.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PlayerUtils {
    public static boolean removeItemFromInventory(@NotNull Player player, @NotNull ItemStack stack, int amount) {
        if (!player.getInventory().contains(stack)) return false;
        if (stack.getCount() < amount) return false;

        if (stack.getCount() == amount) {
            player.getInventory().removeItem(stack);
            return true;
        }

        if (stack.getCount() > amount) {
            for (ItemStack s : player.getInventory().items) {
                if (s.equals(stack, true)) {
                    s.setCount(s.getCount() - amount);
                    return true;
                }
            }
        }
        return false;
    }

    public static Player getStackOwner(@NotNull Level level, @NotNull ItemStack stack) {
        for (Player p : level.players()) {
            if (p.getInventory().contains(stack)) return p;
        }
        return level.getNearestPlayer(0, 0, 0, Double.MAX_VALUE, false);
    }
}
