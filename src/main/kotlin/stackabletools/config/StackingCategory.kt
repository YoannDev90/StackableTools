package stackabletools.config

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
            return values().find { it.key == normalized || it.name.lowercase() == normalized || it.key + "s" == normalized }
        }
    }
}
