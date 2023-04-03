package engineer.skyouo.plugins.minesweeper;

import engineer.skyouo.plugins.minesweeper.commands.MinesweeperCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Minesweeper extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("minesweeper").setExecutor(new MinesweeperCommand());

        HeadUtil.init();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setResourcePack(
                "https://cdn.discordapp.com/attachments/1042351571930984448/1042352420866510908/HeadsInChat.zip"
        );
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
