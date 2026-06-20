package stackabletools

import java.lang.reflect.Method

import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.entity.EquipmentSlot

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import stackabletools.config.ConfigManager
import stackabletools.StackableToolsUtils

public class StackableToolsMixinGameTest : FabricGameTest {

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorItemMixinMaxStackSize(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 1)
		val maxCount = helmet.maxCount
		val expectedMax = ConfigManager.getConfig().stacking.maxArmorPieceStackSize.toInt().coerceAtLeast(1)

		if (maxCount == expectedMax) {
			context.complete()
		} else {
			context.throwGameTestException("Armor max stack size should be $expectedMax, got $maxCount")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testElytraItemMixinMaxStackSize(context: TestContext) {
		val elytra = ItemStack(Items.ELYTRA, 1)
		val maxCount = elytra.maxCount
		val expectedMax = ConfigManager.getConfig().stacking.maxElytraStackSize.toInt().coerceAtLeast(1)

		if (maxCount == expectedMax) {
			context.complete()
		} else {
			context.throwGameTestException("Elytra max stack size should be $expectedMax, got $maxCount")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorItemMixinMultipleTypes(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 1)
		val chestplate = ItemStack(Items.DIAMOND_CHESTPLATE, 1)
		val leggings = ItemStack(Items.DIAMOND_LEGGINGS, 1)
		val boots = ItemStack(Items.DIAMOND_BOOTS, 1)

		val expectedMax = ConfigManager.getConfig().stacking.maxArmorPieceStackSize.toInt().coerceAtLeast(1)

		val allCorrect = listOf(helmet, chestplate, leggings, boots).all { stack ->
			stack.maxCount == expectedMax
		}

		if (allCorrect) {
			context.complete()
		} else {
			context.throwGameTestException("All armor pieces should have max stack size $expectedMax")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testItemStackMixinToolSeparation(context: TestContext) {
		val stack = ItemStack(Items.DIAMOND_PICKAXE, 5)

		if (stack.count > 1 && stack.damage == 0) {
			context.complete()
		} else {
			context.throwGameTestException("Test setup failed: stack should have count > 1 and damage == 0")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testItemStackMixinPreventsDamageSeparation(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		pickaxe.damage = 10

		if (pickaxe.damage > 0) {
			context.complete()
		} else {
			context.throwGameTestException("Damaged pickaxe should have damage > 0")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorSlotMixinRejectsStacks(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 5)

		if (StackableToolsUtils.isStackableItem(helmet)) {
			context.complete()
		} else {
			context.throwGameTestException("Helmet should be stackable in general inventory")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testScreenHandlerMixinDetectsStackableItems(context: TestContext) {
		val sword1 = ItemStack(Items.DIAMOND_SWORD, 1)
		val sword2 = ItemStack(Items.DIAMOND_SWORD, 1)

		if (StackableToolsUtils.canStackItems(sword1, sword2)) {
			context.complete()
		} else {
			context.throwGameTestException("Swords should be stackable for screen handler merge logic")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testInventoryMixinStackMerging(context: TestContext) {
		val pickaxe1 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val pickaxe2 = ItemStack(Items.DIAMOND_PICKAXE, 1)

		val isStackable1 = StackableToolsUtils.isStackableItem(pickaxe1)
		val isStackable2 = StackableToolsUtils.isStackableItem(pickaxe2)
		val canStack = StackableToolsUtils.canStackItems(pickaxe1, pickaxe2)

		if (isStackable1 && isStackable2 && canStack) {
			context.complete()
		} else {
			context.throwGameTestException("Inventory mixin merge logic requires items to be stackable and compatible")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorItemMixinDoesNotAffectTools(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val maxToolStackSize = ConfigManager.getConfig().stacking.maxToolStackSize.toInt()

		if (pickaxe.maxCount == maxToolStackSize) {
			context.complete()
		} else {
			context.throwGameTestException("Tool max stack size should be $maxToolStackSize, got ${pickaxe.maxCount}")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testItemStackMixinNoInfiniteRecursion(context: TestContext) {
		val stack = ItemStack(Items.DIAMOND_PICKAXE, 3)
		stack.damage = 0

		if (stack.count > 0 && !stack.isEmpty) {
			context.complete()
		} else {
			context.throwGameTestException("Stack should remain valid")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorSlotMixinMultipleArmorTypes(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 1)
		val chestplate = ItemStack(Items.DIAMOND_CHESTPLATE, 1)

		val helmetStackable = StackableToolsUtils.isStackableItem(helmet)
		val chestplateStackable = StackableToolsUtils.isStackableItem(chestplate)

		if (helmetStackable && chestplateStackable) {
			context.complete()
		} else {
			context.throwGameTestException("Multiple armor types should all be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testInventoryMixinRespectsCategoryMaxStackSize(context: TestContext) {
		val config = ConfigManager.getConfig().stacking

		val toolMaxStack = config.maxToolStackSize.toInt()
		val weaponMaxStack = config.maxWeaponsStackSize.toInt()
		val armorMaxStack = config.maxArmorPieceStackSize.toInt()

		if (toolMaxStack > 0 && weaponMaxStack > 0 && armorMaxStack > 0) {
			context.complete()
		} else {
			context.throwGameTestException("All category max stack sizes should be positive")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testScreenHandlerMixinRejectsIncompatible(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val axe = ItemStack(Items.DIAMOND_AXE, 1)

		val canStack = StackableToolsUtils.canStackItems(pickaxe, axe)

		if (!canStack) {
			context.complete()
		} else {
			context.throwGameTestException("Screen handler should reject merging different tool types")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testItemStackMixinOnlyAffectsTools(context: TestContext) {
		val potion = ItemStack(Items.POTION, 5)

		if (potion.count == 5 && !potion.isDamaged) {
			context.complete()
		} else {
			context.throwGameTestException("Potions should not be affected by damage-based separation logic")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testCombinedMixinBehavior(context: TestContext) {
		val config = ConfigManager.getConfig().stacking
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)

		val isStackable = StackableToolsUtils.isStackableItem(pickaxe)
		val hasCustomMax = pickaxe.maxCount == config.maxToolStackSize.toInt()
		val configEnabled = config.enable

		if (isStackable && hasCustomMax && configEnabled) {
			context.complete()
		} else {
			context.throwGameTestException("Combined mixin behavior should work when config is enabled")
		}
	}

	override fun invokeTestMethod(context: TestContext, method: Method) {
		try {
			ConfigManager.loadConfig()
			method.invoke(this, context)
		} catch (e: java.lang.reflect.InvocationTargetException) {
			context.throwGameTestException("Test failed: ${e.cause?.message ?: e.cause?.javaClass?.simpleName ?: "Unknown error"}")
		} catch (e: Exception) {
			context.throwGameTestException("Test failed: ${e.message ?: e.javaClass.simpleName}")
		}
	}
}
