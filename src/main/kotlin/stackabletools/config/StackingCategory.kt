package stackabletools.config

/**
 * Enumeration of categories of items that can be handled by the stacking mod.
 */
enum class StackingCategory(val key: String) {
    TOOLS("tools"),
    POTIONS("potions"),
    ARMORS("armors"),
    ENCHANTED_BOOKS("enchant_book"),
    WEAPONS("weapons"),
    ELYTRA("elytra"),
    ALL("all");

    companion object {
        fun fromString(value: String): StackingCategory? {
            val normalized = value.trim().lowercase()
            return values().find {
                val singular = it.key.removeSuffix("s")
                it.key == normalized || it.name.lowercase() == normalized || singular == normalized
            }
        }
    }
}
