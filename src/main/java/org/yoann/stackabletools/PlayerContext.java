package org.yoann.stackabletools;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

public class PlayerContext {
    private static final ThreadLocal<PlayerEntity> currentPlayer = new ThreadLocal<>();
    
    public static void set(PlayerEntity player) {
        currentPlayer.set(player);
    }
    
    public static PlayerEntity get() {
        return currentPlayer.get();
    }
    
    public static void clear() {
        currentPlayer.remove();
    }

    /**
     * Adds a stack of intact tools to the player's inventory.
     * First tries to combine them with existing intact tools,
     * then adds to an empty slot, or drops them if the inventory is full.
     */
    public static void giveOrDropIntactTools(PlayerEntity player, ItemStack intactTools) {
        if (player == null || player.getWorld().isClient) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        int remainingCount = intactTools.getCount();
        int originalCount = remainingCount;
        int maxStackSize = StackableToolsMod.getConfig().getMaxStackSize();

        // First, try to combine with existing stacks of intact tools of the same type
        int stacksCombined = 0;

        for (int i = 0; i < inventory.size(); i++) {
            if (remainingCount <= 0) break;

            ItemStack slotStack = inventory.getStack(i);

            // Check if it's the same item, intact (damage == 0), and not full (count < maxStackSize)
            if (!slotStack.isEmpty()
                && ItemStack.canCombine(slotStack, intactTools)
                && slotStack.getDamage() == 0
                && slotStack.getCount() < maxStackSize) {

                int spaceAvailable = maxStackSize - slotStack.getCount();
                int toAdd = Math.min(spaceAvailable, remainingCount);

                slotStack.setCount(slotStack.getCount() + toAdd);
                remainingCount -= toAdd;
                stacksCombined++;
            }
        }

        // If there are remaining tools, try to add them to an empty slot
        if (remainingCount > 0) {
            ItemStack remaining = intactTools.copy();
            remaining.setCount(remainingCount);

            if (!inventory.insertStack(remaining)) {
                // If the inventory is full, drop the remaining items
                if (StackableToolsMod.getConfig().isLoggingEnabled()) {
                    StackableToolsMod.LOGGER.debug("Player inventory full, dropping {} intact tools", remainingCount);
                }
                ItemEntity itemEntity = new ItemEntity(
                    player.getWorld(),
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    remaining
                );
                itemEntity.setPickupDelay(0);
                player.getWorld().spawnEntity(itemEntity);
            }
        }

        if (stacksCombined > 0 || remainingCount < originalCount) {
            if (StackableToolsMod.getConfig().isLoggingEnabled()) {
                StackableToolsMod.LOGGER.debug("Returned {} intact tools to player (combined into {} stacks)",
                    originalCount - remainingCount, stacksCombined);
            }
        }
    }
}
