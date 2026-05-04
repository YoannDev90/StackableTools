# StackableTools Game Tests

This directory contains automated game tests for the StackableTools mod using Fabric's Game Test Framework.

## Current Status

⚠️ **Tests Created**: Both server and mixin tests are fully implemented  
⚠️ **Compilation Issue**: Game test framework classes not resolving in compile phase  
⚠️ **Build Status**: Main build succeeds (tests disabled for now)

## Overview

Game tests are automated tests that run within the Minecraft game environment, allowing comprehensive testing of:
- Server-side logic (item stacking rules)
- Client-side behavior (inventory UI interactions)
- Integration between mod features

## Test Structure

### Server-Side Tests (`StackableToolsGameTest.kt`)

Tests for core stacking logic:

- **Identical Items**: Verify two fresh items of the same type can stack
- **Damaged Tools**: Confirm damaged tools cannot stack with undamaged ones
- **Different Items**: Ensure different item types cannot stack together
- **Empty Stacks**: Verify empty stacks cannot participate in stacking
- **Category Detection**: Test that items are correctly identified as stackable based on active categories
- **Item Types**: Tests for tools, potions, enchanted books, weapons, armor, and elytra
- **Non-Stackable Items**: Confirm regular items (dirt, etc.) remain unstackable

### Mixin-Specific Tests (`StackableToolsMixinGameTest.kt`)

Tests for behavior injected by Mixins into Minecraft's core classes:

#### ArmorItemMixin
- **testArmorItemMixinMaxStackSize**: Armor pieces return correct max stack size
- **testArmorItemMixinMultipleTypes**: All armor types respect configured limits
- **testArmorItemMixinDoesNotAffectTools**: Tools use their own stack size limits

#### ElytraItemMixin
- **testElytraItemMixinMaxStackSize**: Elytra returns correct max stack size

#### ItemStackMixin (Damage Behavior)
- **testItemStackMixinToolSeparation**: Fresh stacked tools detect separation scenario
- **testItemStackMixinPreventsDamageSeparation**: Already-damaged tools skip separation
- **testItemStackMixinNoInfiniteRecursion**: Re-entry guard prevents stack overflow
- **testItemStackMixinOnlyAffectsTools**: Only tool-type items trigger separation

#### ArmorSlotMixin
- **testArmorSlotMixinRejectsStacks**: Equipment slots limit to 1 item
- **testArmorSlotMixinMultipleArmorTypes**: All armor types handled correctly

#### ScreenHandlerMixin
- **testScreenHandlerMixinDetectsStackableItems**: Inventory click detection works
- **testScreenHandlerMixinRejectsIncompatible**: Incompatible items don't merge

#### InventoryMixin
- **testInventoryMixinStackMerging**: Stack merge logic activates
- **testInventoryMixinRespectsCategoryMaxStackSize**: Max sizes enforced per category

#### Combined Behavior
- **testCombinedMixinBehavior**: All mixins work together correctly

### Client-Side Tests (`StackableToolsClientGameTest.kt`)

Tests for client-side inventory and UI behavior:

- **Client Recognition**: Verify client correctly identifies stackable items
- **Damaged Tool Rejection**: Confirm client rejects stacking damaged tools
- **Multiple Item Types**: Test handling of various stackable item types simultaneously
- **Client-Server Consistency**: Ensure client and server use identical stacking logic
- **Armor Handling**: Test armor piece recognition and rejection of mixed armor types
- **Empty Slots**: Verify correct handling of empty inventory slots
- **Non-Stackable Items**: Confirm non-stackable items are rejected

## Running Tests

### Run Server Game Tests

```bash
./gradlew build
```

Server game tests run automatically during the build process.

### Run Client Game Tests

```bash
./gradlew runClientGameTest
```

### Run Specific Test

To run a specific test, use:

```bash
./gradlew gametest --tests=*TestClassName*
```

## CI/CD Integration

Game tests can be integrated into GitHub Actions workflows:

### Server Tests
Server tests run automatically with the standard build job.

### Client Tests
Add to your GitHub Actions workflow:

```yaml
client_game_test:
  runs-on: ubuntu-24.04
  steps:
    - uses: actions/checkout@v4
    - uses: actions/setup-java@v4
      with:
        distribution: temurin
        java-version: 21
    - run: ./gradlew runProductionClientGameTest
    - if: always()
      uses: actions/upload-artifact@v4
      with:
        path: build/run/clientGameTest/screenshots
```

## Test Coverage

The test suite covers:

1. **Stacking Rules**: Core logic for determining if items can stack
2. **Item Categories**: Tests for all stackable item categories
3. **Damage Handling**: Verification that damaged items follow stacking rules
4. **Configuration**: Tests ensure config affects stacking behavior
5. **Edge Cases**: Empty stacks, mixed types, and non-stackable items
6. **Mixin Injection**: Behavior added to Minecraft classes:
   - Item max stack size overrides (ArmorItemMixin, ElytraItemMixin)
   - Tool damage separation logic (ItemStackMixin)
   - Inventory merge behavior (InventoryMixin)
   - Armor slot restrictions (ArmorSlotMixin)
   - Screen handler click handling (ScreenHandlerMixin)
7. **Integration**: Combined mixin behavior and cross-feature interaction

## Architecture

Tests follow Fabric's `CustomTestMethodInvoker` pattern:

- Each test is a method annotated with `@GameTest`
- Tests receive a `GameTestHelper` for assertions and context manipulation
- `invokeTestMethod()` prepares the test environment (loads config)
- Tests succeed when `context.succeed()` is called, fail when `context.fail()` is called

## Test Assertions

Tests use `GameTestHelper` methods:

- `context.succeed()` - Mark test as passed
- `context.fail(message)` - Mark test as failed with message
- `context.setBlock()` - Modify world blocks (if needed)
- `context.assertBlockPresent()` - Check block presence (if needed)

## Adding New Tests

To add a new test:

1. Create a method in `StackableToolsGameTest` or `StackableToolsClientGameTest`
2. Annotate with `@GameTest`
3. Accept `GameTestHelper` as parameter
4. Use assertions and `context.succeed()` or `context.fail()`

Example:

```kotlin
@GameTest
public fun testMyFeature(context: GameTestHelper) {
    val item = ItemStack(Items.DIAMOND_PICKAXE, 1)
    
    if (StackableToolsUtils.isStackableItem(item)) {
        context.succeed()
    } else {
        context.fail("Item should be stackable")
    }
}
```

## Troubleshooting

### Tests not running
- Verify `fabric.mod.json` in `src/gametest/resources/` exists
- Check that entry points reference correct test class names
- Ensure Kotlin adapter is specified: `"adapter": "kotlin"`

### Client tests fail on GitHub Actions
- Add JVM arg: `-Dfabric.client.gametest.disableNetworkSynchronizer=true`
- Install XVFB for headless rendering: `apt install xvfb`

### Configuration not loaded in tests
- Tests automatically call `ConfigManager.loadConfig()` in `invokeTestMethod()`
- Ensure config file exists at `config/stackabletools.toml`

## Future Enhancements

Potential tests to add:

- Mixin behavior validation (inventory handling)
- Max stack size enforcement
- Configuration hot-reload during gameplay
- Performance benchmarks for cache invalidation
- Network synchronization tests (multiplayer)
