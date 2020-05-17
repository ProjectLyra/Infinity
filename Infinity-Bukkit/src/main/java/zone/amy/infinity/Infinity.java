package zone.amy.infinity;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zone.amy.infinity.event.ModuleEnabledEvent;
import zone.amy.infinity.module.InfinityModule;
import zone.amy.infinity.session.IsolationListener;
import zone.amy.infinity.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Keep track of session and local data, run events for them
 */
@InfinityModule.Meta(
        name = "Infinity",
        description = "Handles internal logic for generic Infinity implementations"
)
public class Infinity extends InfinityModule implements Listener {
    @Getter private static Infinity instance;

    @Getter private SessionManager sessionManager;

    private ModuleListener moduleListener;

    // Event Methods

    @Override
    protected void onModuleEnable() {
        instance = this;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server reloading, please rejoin."); // TODO: Language
        }

        this.sessionManager = new SessionManager();
        getServer().getPluginManager().registerEvents(new IsolationListener(sessionManager), this);

        moduleListener = new ModuleListener();
        getServer().getPluginManager().registerEvents(moduleListener, this);

    }

    @Override
    protected void onModuleDisable() {
    }

    public List<InfinityModule> getModuleList() {
        return new ArrayList<>(moduleListener.modules);
    }
}
