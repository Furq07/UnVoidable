package dev.furq.unvoidable.commands

import dev.furq.unvoidable.UnVoidable
import dev.furq.unvoidable.listeners.VoidCheckListener
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import java.io.File

class UnVoidableCommand(private val plugin: UnVoidable, private val voidCheckListener: VoidCheckListener) : CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label.equals("unvoidable", ignoreCase = true) && args.isNotEmpty()) {
            when (args[0].lowercase()) {
                "reload" -> {
                        plugin.reloadConfig()
                        voidCheckListener.updateWorldConfigs()
                        sender.sendMessage("§aReloaded UnVoidable successfully!")
                }
                "setspawn" -> {
                    if (sender is Player) {
                        val config = plugin.config
                        val worldsConfig = config.getConfigurationSection("worlds") ?: config.createSection("worlds")
                        val worldName = sender.world.name
                        val newSafeCords = String.format("%.3f,%.3f,%.3f", sender.location.x, sender.location.y, sender.location.z)
                        val newFacing = String.format("%.3f,%.3f", sender.location.yaw, sender.location.pitch)

                        val worldConfig = worldsConfig.getConfigurationSection(worldName)
                            ?: worldsConfig.createSection(worldName)
                        worldConfig.set("safe-cords", newSafeCords)
                        worldConfig.set("facing", newFacing)
                        if (!worldConfig.contains("void-y-level")) {
                            worldConfig.set("void-y-level", -64)
                        }
                        try {
                            saveConfig(config, File(plugin.dataFolder, "config.yml"))
                            plugin.reloadConfig()
                            voidCheckListener.updateWorldConfigs()
                            sender.sendMessage("§aSet the safe spawn for $worldName successfully.")
                        } catch (e: Exception) {
                            sender.sendMessage("§cFailed to save updated configuration: ${e.message}")
                        }
                    } else {
                        sender.sendMessage("§cThis command can only be run by a player.")
                    }
                }
                "setvoidlevel" -> {
                    if (sender is Player) {
                            try {
                                val newVoidLevel = args[1].toInt()
                                val config = plugin.config
                                val worldsConfig = config.getConfigurationSection("worlds") ?: config.createSection("worlds")
                                val worldName = sender.world.name

                                val worldConfig = worldsConfig.getConfigurationSection(worldName) ?: worldsConfig.createSection(worldName)
                                worldConfig.set("void-y-level", newVoidLevel)
                                if (!worldConfig.contains("safe-cords")) {
                                    worldConfig.set("safe-cords", "0.500, 80.000, 0.500")
                                }
                                if (!worldConfig.contains("facing")) {
                                    worldConfig.set("facing", "0.000, 0.000")
                                }
                                try {
                                    saveConfig(config, File(plugin.dataFolder, "config.yml"))
                                    plugin.reloadConfig()
                                    voidCheckListener.updateWorldConfigs()
                                    sender.sendMessage("§aSet the Void Y Level for $worldName successfully.")
                                } catch (e: Exception) {
                                    sender.sendMessage("§cFailed to save updated configuration: ${e.message}")
                                }
                            } catch (ex: NumberFormatException) {
                                sender.sendMessage("§cInvalid void level. Command Syntax: /unvoidable setvoidlevel <y-void-level>")
                            }
                    } else {
                        sender.sendMessage("§cThis command can only be run by a player.")
                    }
                }
                else -> sender.sendMessage("§cUnknown subcommand.")
            }
        }
        return true
    }

    private fun saveConfig(config: FileConfiguration, file: File) {
        try {
            config.save(file)
        } catch (e: Exception) {
            plugin.logger.warning("Failed to save configuration: ${e.message}")
        }
    }
}