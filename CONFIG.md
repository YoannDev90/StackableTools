# Stackable Tools Configuration

This mod allows tools to be stacked in your inventory. The configuration file is automatically created on first launch.

## Configuration File Location

The configuration file is located at:
```
config/stackabletools.json
```

## Configuration Options

### `maxStackSize`
- **Type:** Integer
- **Default:** `8`
- **Range:** 1-64
- **Description:** The maximum number of tools that can be stacked together.

### `enableLogging`
- **Type:** Boolean
- **Default:** `false`
- **Description:** Enables debug logging for troubleshooting. When enabled, the mod will log detailed information about tool stacking operations.

## Example Configuration

```json
{
  "maxStackSize": 8,
  "enableLogging": false
}
```

## How to Use

1. Launch Minecraft with the mod installed at least once to generate the default configuration file.
2. Close Minecraft.
3. Navigate to the `config` folder in your Minecraft directory.
4. Open `stackabletools.json` with a text editor.
5. Modify the values as desired:
   - Change `maxStackSize` to control how many tools can stack (e.g., `16` for larger stacks, `4` for smaller stacks)
   - Set `enableLogging` to `true` if you want to see debug messages in the logs
6. Save the file.
7. Launch Minecraft again.

## Notes

- If `maxStackSize` is set to an invalid value (less than 1 or greater than 64), it will automatically reset to the default value of 8.
- Changes to the configuration file require a game restart to take effect.
- When tools are used and damaged, they are automatically separated from the stack, and the intact tools are returned to your inventory.

