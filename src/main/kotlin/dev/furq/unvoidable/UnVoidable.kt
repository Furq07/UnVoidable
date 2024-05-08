package dev.furq.unvoidable

import dev.furq.unvoidable.commands.UnVoidableCommand
import dev.furq.unvoidable.listeners.VoidCheckListener
import dev.furq.unvoidable.tabcompleter.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class UnVoidable : JavaPlugin() {
    private lateinit var voidCheckListener: VoidCheckListener

    override fun onEnable() {
        voidCheckListener = VoidCheckListener(this)
        server.pluginManager.registerEvents(voidCheckListener, this)
        saveDefaultConfig()
        reloadConfig()
        voidCheckListener.updateWorldConfigs()
        logger.info("Thank you for using my plugin - Furq")
        getCommand("unvoidable")?.setExecutor(UnVoidableCommand(this, voidCheckListener))
        getCommand("unvoidable")?.tabCompleter = TabCompleter()
    }

    override fun onDisable() {
        logger.info("GoodBye!")
    }
}
