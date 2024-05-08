package dev.furq.unvoidable.tabcompleter

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TabCompleter : TabCompleter {
    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String>? {
        if (command.name.equals("unvoidable", ignoreCase = true)) {
            if (args.size == 1) {
                return listOf("reload")
            } else if (args.size == 2) {
                when (args[0].lowercase()) {
                  //  "reload" -> return listOf("option1", "option2")
                   else -> return null
                }
            }
        }
        return null
    }
}