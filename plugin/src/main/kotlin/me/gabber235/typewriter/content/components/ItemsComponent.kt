package me.gabber235.typewriter.content.components

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Offer a way to have items in the player's inventory.
 */
interface ItemsComponent {
    fun items(player: Player): Map<Int, IntractableItem>
}

infix fun ItemStack.onInteract(action: (ItemInteraction) -> Unit) = IntractableItem(this, action)
infix operator fun ItemStack.invoke(action: (ItemInteraction) -> Unit) = IntractableItem(this, action)

data class IntractableItem(
    val item: ItemStack,
    val action: (ItemInteraction) -> Unit,
)

data class ItemInteraction(
    val type: ItemInteractionType,
    val slot: Int,
)

enum class ItemInteractionType {
    INVENTORY_CLICK,
    LEFT_CLICK,
    RIGHT_CLICK,
    SHIFT_LEFT_CLICK,
    SHIFT_RIGHT_CLICK,
    DROP,
    SWAP,
}
