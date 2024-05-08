package dev.furq.unvoidable.commands

import dev.furq.unvoidable.UnVoidable
import dev.furq.unvoidable.listeners.VoidCheckListener
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

class UnVoidableCommand(private val plugin: UnVoidable, private val voidCheckListener: VoidCheckListener) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (command.label.equals("unvoidable", ignoreCase = true) && args.isNotEmpty() && args[0].equals("reload", ignoreCase = true)) {
            plugin.reloadConfig()
            voidCheckListener.updateWorldConfigs()
            sender.sendMessage("§aReloaded UnVoidable successfully!")
        }
        return false
    }
}