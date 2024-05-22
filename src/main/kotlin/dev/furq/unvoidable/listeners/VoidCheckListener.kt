package dev.furq.unvoidable.listeners

import dev.furq.unvoidable.UnVoidable
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.scheduler.BukkitRunnable

class VoidCheckListener(private val plugin: UnVoidable) : Listener {

    private var worldConfigs: Map<String, WorldConfig> = emptyMap()

    init {
        updateWorldConfigs()
    }

    fun updateWorldConfigs() {
        val config = plugin.config
        val worldsConfig = config.getConfigurationSection("worlds")
        worldConfigs = worldsConfig?.getKeys(false)?.associateWith { worldKey ->
            val worldConfig = worldsConfig.getConfigurationSection(worldKey)
            if (worldConfig != null) {
                WorldConfig(
                    safeCords = worldConfig.getString("safe-cords"),
                    voidYLevel = worldConfig.getInt("void-y-level"),
                    facing = worldConfig.getString("facing")
                )
            } else {
                null
            }
        }?.filterValues { it != null }?.mapValues { it.value!! }
            ?: emptyMap()
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val yLevel = player.location.y.toInt()
        val worldConfig = worldConfigs[player.world.name]

        worldConfig?.let {
            if (player.velocity.y < 0) {
                if (yLevel >= it.voidYLevel - 1 && yLevel <= it.voidYLevel + 1) {
                    val safeCords = it.safeCords?.split(",")
                    val facingValue = it.facing
                    if (safeCords != null && safeCords.size == 3) {
                        val x = safeCords[0].toDouble()
                        val y = safeCords[1].toDouble()
                        val z = safeCords[2].toDouble()
                        var yaw: Float? = null
                        var pitch: Float? = null

                        if (facingValue != null && facingValue != "false") {
                            val facingData = facingValue.split(",")
                            if (facingData.size == 2) {
                                yaw = facingData[0].toFloatOrNull()
                                pitch = facingData[1].toFloatOrNull()
                            }
                        }

                        val targetYaw = yaw ?: player.location.yaw
                        val targetPitch = pitch ?: player.location.pitch
                        val location = Location(player.world, x, y, z, targetYaw, targetPitch)

                        object : BukkitRunnable() {
                            override fun run() {
                                player.teleport(location)
                                player.sendMessage("§aTeleported to a safe location.")
                                var endermanTeleportSound: Sound? = null
                                try {
                                     endermanTeleportSound = Sound.valueOf("ENTITY_ENDERMAN_TELEPORT")
                                } catch (e: IllegalArgumentException) {
                                     endermanTeleportSound = Sound.valueOf("ENDERMAN_TELEPORT")
                                }
                                player.playSound(player.location, endermanTeleportSound, 1.0f, 1.0f)
                            }
                        }.runTask(plugin)
                    }
                }
            }
        }
    }

    private data class WorldConfig(val safeCords: String?, val voidYLevel: Int, val facing: String)
}