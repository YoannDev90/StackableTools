package stackabletools.mixin

import net.minecraft.item.ArmorItem
import net.minecraft.item.Item
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Pseudo
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
import stackabletools.config.ConfigManager

/**
 * Mixin aimed at allowing armor pieces to be stacked beyond 1.
 * Injects into Item.getMaxCount to provide a dynamic limit based on the configuration.
 */
@Mixin(Item::class)
abstract class ArmorItemMixin {

    /**
     * Overrides the maximum stack size for any Item if it's an instance of ArmorItem.
     */
    @Inject(method = ["getMaxCount"], at = [At("HEAD")], cancellable = true)
    private fun onGetMaxCount(cir: CallbackInfoReturnable<Int>) {
        val item = this as Any as Item
        if (item is ArmorItem) {
            val config = try { ConfigManager.getConfig() } catch (e: Exception) { null }
            if (config != null && config.stacking.enable) {
                cir.returnValue = config.stacking.maxArmorPieceStackSize.toInt().coerceAtLeast(1)
            }
        }
    }
}
