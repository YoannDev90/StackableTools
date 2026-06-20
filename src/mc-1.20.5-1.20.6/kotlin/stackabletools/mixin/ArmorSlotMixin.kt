package stackabletools.mixin

import net.minecraft.item.ItemStack
import net.minecraft.item.ArmorItem
import net.minecraft.item.EnchantedBookItem
import net.minecraft.item.ElytraItem
import net.minecraft.item.PotionItem
import net.minecraft.item.SwordItem
import net.minecraft.item.ToolItem
import net.minecraft.item.TridentItem
import net.minecraft.screen.slot.Slot
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager

/**
 * Mixin targeting armor slots to ensure they never accept more than 1 item,
 * even if our mod makes the item stackable in the general inventory.
 */
@Mixin(Slot::class)
abstract class ArmorSlotMixin {

    /**
     * Forces the maximum item count for armor slots to be 1.
     * This prevents stacking items like Elytras or Armor pieces in the equipment slots.
     */
    @Inject(method = ["getMaxItemCount(Lnet/minecraft/item/ItemStack;)I"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxItemCount(stack: ItemStack, cir: CallbackInfoReturnable<Int>) {
        val slot = this as Slot

        if (slot.javaClass.name.contains("PlayerScreenHandler") &&
            (slot.index in 5..8 || slot.javaClass.simpleName in listOf("OffhandSlot", "ArmorSlot"))) {
            cir.returnValue = 1
            return
        }

        if (stack.isEmpty || !StackableToolsUtils.isStackableItem(stack)) return

        val cfg = ConfigManager.getConfig().stacking
        val maxStack = when (stack.item) {
            is SwordItem, is TridentItem -> cfg.maxWeaponsStackSize
            is ToolItem -> cfg.maxToolStackSize
            is PotionItem -> cfg.maxPotionStackSize
            is EnchantedBookItem -> cfg.maxEnchantedBooksStackSize
            is ElytraItem -> cfg.maxElytraStackSize
            is ArmorItem -> cfg.maxArmorPieceStackSize
            else -> cfg.maxStackSize
        }.toInt().coerceAtLeast(1)

        cir.returnValue = maxStack
        cir.cancel()
    }
}
