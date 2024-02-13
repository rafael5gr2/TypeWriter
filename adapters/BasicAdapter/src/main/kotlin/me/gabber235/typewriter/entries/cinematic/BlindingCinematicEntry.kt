package me.gabber235.typewriter.entries.cinematic

import me.gabber235.typewriter.adapters.Colors
import me.gabber235.typewriter.adapters.Entry
import me.gabber235.typewriter.adapters.modifiers.Segments
import me.gabber235.typewriter.entry.Criteria
import me.gabber235.typewriter.entry.cinematic.SimpleCinematicAction
import me.gabber235.typewriter.entry.entries.CinematicAction
import me.gabber235.typewriter.entry.entries.CinematicEntry
import me.gabber235.typewriter.entry.entries.Segment
import me.gabber235.typewriter.utils.EffectStateProvider
import me.gabber235.typewriter.utils.PlayerState
import me.gabber235.typewriter.utils.ThreadType.SYNC
import me.gabber235.typewriter.utils.restore
import me.gabber235.typewriter.utils.state
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType.BLINDNESS

@Entry("blinding_cinematic", "Blind the player so the screen looks black", Colors.CYAN, "heroicons-solid:eye-off")
/**
 * The `Blinding Cinematic` entry is used to blind the player so the screen looks black.
 *
 * ## How could this be used?
 * When starting a cinematic, if you have a [Camera Cinematic Entry](./camera_cinematic)
 * where you wait for a few frames to get it loading in.
 */
class BlindingCinematicEntry(
    override val id: String,
    override val name: String,
    override val criteria: List<Criteria>,
    @Segments(icon = "heroicons-solid:eye-off")
    val segments: List<BlindingSegment>,
) : CinematicEntry {
    override fun createSimulated(player: Player): CinematicAction? = null
    override fun create(player: Player): CinematicAction {
        return BlindingCinematicAction(
            player,
            this,
        )
    }
}

data class BlindingSegment(
    override val startFrame: Int,
    override val endFrame: Int,
) : Segment

class BlindingCinematicAction(
    private val player: Player,
    entry: BlindingCinematicEntry,
) : SimpleCinematicAction<BlindingSegment>() {

    private var state: PlayerState? = null

    override val segments: List<BlindingSegment> = entry.segments

    override suspend fun startSegment(segment: BlindingSegment) {
        super.startSegment(segment)
        state = player.state(EffectStateProvider(BLINDNESS))

        SYNC.switchContext {
            player.addPotionEffect(PotionEffect(BLINDNESS, 10000000, 1, false, false, false))
        }
    }

    override suspend fun stopSegment(segment: BlindingSegment) {
        super.stopSegment(segment)
        restoreState()
    }

    private suspend fun restoreState() {
        val state = state ?: return
        this.state = null
        SYNC.switchContext {
            player.restore(state)
        }
    }

    override suspend fun teardown() {
        super.teardown()

        if (state != null) {
            restoreState()
        }
    }
}
