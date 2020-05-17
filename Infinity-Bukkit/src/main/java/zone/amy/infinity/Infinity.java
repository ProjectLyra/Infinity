package zone.amy.infinity;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import zone.amy.infinity.module.InfinityModule;
import zone.amy.infinity.session.IsolationListener;
import zone.amy.infinity.session.SessionManager;

/**
 * Keep track of session and local data, run events for them
 */
@InfinityModule.Meta(
        name = "Infinity",
        description = "Handles internal logic for generic Infinity implementations"
)
public class Infinity extends InfinityModule {
    @Getter private static Infinity instance;

    @Getter private SessionManager sessionManager;

    // Event Methods

    @Override
    protected void onModuleEnable() {
        instance = this;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server reloading, please rejoin."); // TODO: Language
        }

        this.sessionManager = new SessionManager();
        getServer().getPluginManager().registerEvents(new IsolationListener(sessionManager), this);
    }
    // Action Methods
}
