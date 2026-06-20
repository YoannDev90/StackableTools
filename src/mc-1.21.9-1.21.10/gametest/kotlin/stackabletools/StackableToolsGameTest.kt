package stackabletools

import java.lang.reflect.Method

import net.minecraft.test.GameTest
import net.minecraft.test.TestContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.block.Blocks

import net.fabricmc.fabric.api.gametest.v1.FabricGameTest
import stackabletools.StackableToolsUtils
import stackabletools.config.ConfigManager
import stackabletools.config.StackableToolsConfig
import stackabletools.config.StackingCategory

public class StackableToolsGameTest : FabricGameTest {

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testIdenticalToolsCanStack(context: TestContext) {
		val pickaxe1 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val pickaxe2 = ItemStack(Items.DIAMOND_PICKAXE, 1)

		if (StackableToolsUtils.canStackItems(pickaxe1, pickaxe2)) {
			context.complete()
		} else {
			context.throwGameTestException("Identical diamond pickaxes should be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testDamagedToolsCannotStack(context: TestContext) {
		val pickaxe1 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val pickaxe2 = ItemStack(Items.DIAMOND_PICKAXE, 1)

		pickaxe2.damage = 5

		if (!StackableToolsUtils.canStackItems(pickaxe1, pickaxe2)) {
			context.complete()
		} else {
			context.throwGameTestException("Damaged and undamaged tools should not be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testDifferentItemsCannotStack(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val axe = ItemStack(Items.DIAMOND_AXE, 1)

		if (!StackableToolsUtils.canStackItems(pickaxe, axe)) {
			context.complete()
		} else {
			context.throwGameTestException("Different tool types should not be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testEmptyStacksCannotStack(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val empty = ItemStack.EMPTY

		if (!StackableToolsUtils.canStackItems(pickaxe, empty)) {
			context.complete()
		} else {
			context.throwGameTestException("Empty stacks should not be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testToolsAreStackableWhenCategoryActive(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)

		if (StackableToolsUtils.isStackableItem(pickaxe)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond pickaxe should be stackable when tools category is active")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testPotionsAreStackableWhenCategoryActive(context: TestContext) {
		val potion = ItemStack(Items.POTION, 1)

		if (StackableToolsUtils.isStackableItem(potion)) {
			context.complete()
		} else {
			context.throwGameTestException("Potion should be stackable when potions category is active")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testEnchantedBooksAreStackable(context: TestContext) {
		val enchantedBook = ItemStack(Items.ENCHANTED_BOOK, 1)

		if (StackableToolsUtils.isStackableItem(enchantedBook)) {
			context.complete()
		} else {
			context.throwGameTestException("Enchanted book should be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testWeaponsAreStackableWhenCategoryActive(context: TestContext) {
		val sword = ItemStack(Items.DIAMOND_SWORD, 1)

		if (StackableToolsUtils.isStackableItem(sword)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond sword should be stackable when weapons category is active")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testArmorIsStackableWhenCategoryActive(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 1)

		if (StackableToolsUtils.isStackableItem(helmet)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond helmet should be stackable when armors category is active")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testElytraIsStackableWhenCategoryActive(context: TestContext) {
		val elytra = ItemStack(Items.ELYTRA, 1)

		if (StackableToolsUtils.isStackableItem(elytra)) {
			context.complete()
		} else {
			context.throwGameTestException("Elytra should be stackable when elytra category is active")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testRegularItemsNotStackable(context: TestContext) {
		val dirt = ItemStack(Items.DIRT, 1)

		if (!StackableToolsUtils.isStackableItem(dirt)) {
			context.complete()
		} else {
			context.throwGameTestException("Dirt should not be stackable by default")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testEmptyStackNotStackable(context: TestContext) {
		val empty = ItemStack.EMPTY

		if (!StackableToolsUtils.isStackableItem(empty)) {
			context.complete()
		} else {
			context.throwGameTestException("Empty stack should not be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testIdenticalSwordsCanStack(context: TestContext) {
		val sword1 = ItemStack(Items.DIAMOND_SWORD, 1)
		val sword2 = ItemStack(Items.DIAMOND_SWORD, 1)

		if (StackableToolsUtils.canStackItems(sword1, sword2)) {
			context.complete()
		} else {
			context.throwGameTestException("Identical swords without damage should be stackable")
		}
	}

	@GameTest(templateName = "fabric-gametest-api-v1:empty")
	public fun testDifferentlyDamagedArmorCannotStack(context: TestContext) {
		val helmet1 = ItemStack(Items.DIAMOND_HELMET, 1)
		val helmet2 = ItemStack(Items.DIAMOND_HELMET, 1)

		helmet2.damage = 10

		if (!StackableToolsUtils.canStackItems(helmet1, helmet2)) {
			context.complete()
		} else {
			context.throwGameTestException("Armor with different damage levels should not be stackable")
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
