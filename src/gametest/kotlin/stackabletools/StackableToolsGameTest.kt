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

/**
 * Server-side game tests for StackableTools mod.
 * Tests core functionality like item stacking logic and configuration.
 */
public class StackableToolsGameTest : FabricGameTest {
	
	/**
	 * Test that two identical diamond pickaxes can be stacked.
	 */
	@GameTest
	public fun testIdenticalToolsCanStack(context: TestContext) {
		val pickaxe1 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val pickaxe2 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		
		if (StackableToolsUtils.canStackItems(pickaxe1, pickaxe2)) {
			context.complete()
		} else {
			context.throwGameTestException("Identical diamond pickaxes should be stackable")
		}
	}

	/**
	 * Test that damaged tools cannot be stacked.
	 */
	@GameTest
	public fun testDamagedToolsCannotStack(context: TestContext) {
		val pickaxe1 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val pickaxe2 = ItemStack(Items.DIAMOND_PICKAXE, 1)
		
		// Damage second pickaxe
		pickaxe2.damage = 5
		
		if (!StackableToolsUtils.canStackItems(pickaxe1, pickaxe2)) {
			context.complete()
		} else {
			context.throwGameTestException("Damaged and undamaged tools should not be stackable")
		}
	}

	/**
	 * Test that different item types cannot be stacked.
	 */
	@GameTest
	public fun testDifferentItemsCannotStack(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val axe = ItemStack(Items.DIAMOND_AXE, 1)
		
		if (!StackableToolsUtils.canStackItems(pickaxe, axe)) {
			context.complete()
		} else {
			context.throwGameTestException("Different tool types should not be stackable")
		}
	}

	/**
	 * Test that empty stacks cannot be stacked.
	 */
	@GameTest
	public fun testEmptyStacksCannotStack(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		val empty = ItemStack.EMPTY
		
		if (!StackableToolsUtils.canStackItems(pickaxe, empty)) {
			context.complete()
		} else {
			context.throwGameTestException("Empty stacks should not be stackable")
		}
	}

	/**
	 * Test that diamond pickaxes are identified as stackable when tools category is active.
	 */
	@GameTest
	public fun testToolsAreStackableWhenCategoryActive(context: TestContext) {
		val pickaxe = ItemStack(Items.DIAMOND_PICKAXE, 1)
		
		if (StackableToolsUtils.isStackableItem(pickaxe)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond pickaxe should be stackable when tools category is active")
		}
	}

	/**
	 * Test that potions are identified as stackable when potions category is active.
	 */
	@GameTest
	public fun testPotionsAreStackableWhenCategoryActive(context: TestContext) {
		val potion = ItemStack(Items.POTION, 1)
		
		if (StackableToolsUtils.isStackableItem(potion)) {
			context.complete()
		} else {
			context.throwGameTestException("Potion should be stackable when potions category is active")
		}
	}

	/**
	 * Test that enchanted books are identified as stackable when enchanted books category is active.
	 */
	@GameTest
	public fun testEnchantedBooksAreStackable(context: TestContext) {
		val enchantedBook = ItemStack(Items.ENCHANTED_BOOK, 1)
		
		if (StackableToolsUtils.isStackableItem(enchantedBook)) {
			context.complete()
		} else {
			context.throwGameTestException("Enchanted book should be stackable")
		}
	}

	/**
	 * Test that weapons (swords) are identified as stackable when weapons category is active.
	 */
	@GameTest
	public fun testWeaponsAreStackableWhenCategoryActive(context: TestContext) {
		val sword = ItemStack(Items.DIAMOND_SWORD, 1)
		
		if (StackableToolsUtils.isStackableItem(sword)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond sword should be stackable when weapons category is active")
		}
	}

	/**
	 * Test that armor pieces are identified as stackable when armors category is active.
	 */
	@GameTest
	public fun testArmorIsStackableWhenCategoryActive(context: TestContext) {
		val helmet = ItemStack(Items.DIAMOND_HELMET, 1)
		
		if (StackableToolsUtils.isStackableItem(helmet)) {
			context.complete()
		} else {
			context.throwGameTestException("Diamond helmet should be stackable when armors category is active")
		}
	}

	/**
	 * Test that elytra is identified as stackable when elytra category is active.
	 */
	@GameTest
	public fun testElytraIsStackableWhenCategoryActive(context: TestContext) {
		val elytra = ItemStack(Items.ELYTRA, 1)
		
		if (StackableToolsUtils.isStackableItem(elytra)) {
			context.complete()
		} else {
			context.throwGameTestException("Elytra should be stackable when elytra category is active")
		}
	}

	/**
	 * Test that regular items (like dirt) are not stackable.
	 */
	@GameTest
	public fun testRegularItemsNotStackable(context: TestContext) {
		val dirt = ItemStack(Items.DIRT, 1)
		
		if (!StackableToolsUtils.isStackableItem(dirt)) {
			context.complete()
		} else {
			context.throwGameTestException("Dirt should not be stackable by default")
		}
	}

	/**
	 * Test that empty stacks are not stackable.
	 */
	@GameTest
	public fun testEmptyStackNotStackable(context: TestContext) {
		val empty = ItemStack.EMPTY
		
		if (!StackableToolsUtils.isStackableItem(empty)) {
			context.complete()
		} else {
			context.throwGameTestException("Empty stack should not be stackable")
		}
	}

	/**
	 * Test that two identical swords without damage can be stacked.
	 */
	@GameTest
	public fun testIdenticalSwordsCanStack(context: TestContext) {
		val sword1 = ItemStack(Items.DIAMOND_SWORD, 1)
		val sword2 = ItemStack(Items.DIAMOND_SWORD, 1)
		
		if (StackableToolsUtils.canStackItems(sword1, sword2)) {
			context.complete()
		} else {
			context.throwGameTestException("Identical swords without damage should be stackable")
		}
	}

	/**
	 * Test that armor pieces with different damage levels cannot be stacked.
	 */
	@GameTest
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

	/**
	 * Override to prepare the test environment before each test method.
	 */
	override fun invokeTestMethod(context: TestContext, method: Method) {
		try {
			// Ensure configuration is loaded before test
			ConfigManager.loadConfig()
			
			// Run the test method
			method.invoke(this, context)
		} catch (e: Exception) {
			context.throwGameTestException("Test failed with exception: ${e.message}")
		}
	}
}
