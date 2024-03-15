package me.gabber235.typewriter.content.modes

import com.github.shynixn.mccoroutine.bukkit.launch
import kotlinx.coroutines.delay
import me.gabber235.typewriter.content.ContentContext
import me.gabber235.typewriter.content.ContentMode
import me.gabber235.typewriter.content.entryId
import me.gabber235.typewriter.content.fieldPath
import me.gabber235.typewriter.entry.Entry
import me.gabber235.typewriter.entry.Ref
import me.gabber235.typewriter.entry.entries.SystemTrigger.CONTENT_END
import me.gabber235.typewriter.entry.fieldValue
import me.gabber235.typewriter.entry.triggerFor
import me.gabber235.typewriter.logger
import me.gabber235.typewriter.plugin
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.milliseconds

abstract class ImmediateFieldValueContentMode<T : Any>(context: ContentContext, player: Player) :
    ContentMode(context, player) {

    override fun setup() {
        val entryId = context.entryId
        if (entryId == null) {
            logger.severe("No entryId found for ${this::class.simpleName}. This is a bug. Please report it.")
            CONTENT_END triggerFor player
            return
        }

        val fieldPath = context.fieldPath
        if (fieldPath == null) {
            logger.severe("No fieldPath found for ${this::class.simpleName}. This is a bug. Please report it.")
            CONTENT_END triggerFor player
            return
        }

        // Needs to complete the initialisation so that we can properly get the value and end the content mode
        plugin.launch {
            delay(100.milliseconds)
            val value = value()

            Ref(entryId, Entry::class).fieldValue(fieldPath, value)
            CONTENT_END triggerFor player
        }
    }

    abstract fun value(): T

}