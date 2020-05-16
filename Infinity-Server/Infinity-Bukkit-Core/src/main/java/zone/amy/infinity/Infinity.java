package zone.amy.infinity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zone.amy.infinity.event.ModuleEnabledEvent;
import zone.amy.infinity.modules.InfinityModule;
import zone.amy.infinity.session.SessionConfiguration;
import zone.amy.infinity.sessions.InfinitySession;

import java.util.HashMap;
import java.util.Map;

/**
 * Keep track of sessions and local data, run events for them
 */
@InfinityModule.Meta(
        name = "Infinity",
        description = "Handles internal logic for generic Infinity implementations"
)
public class Infinity extends InfinityModule {
    @Getter private static Infinity instance;

    @Getter private InfinityControlModule controller;
    @Getter(AccessLevel.PACKAGE) private Map<Class<? extends SessionConfiguration>, Class<? extends InfinitySession>> sessionClasses = new HashMap<>();

    // Event Methods

    @Override
    protected void onModuleEnable() {
        instance = this;

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.RED + "Server reloading, please rejoin."); // TODO: Language
        }

        getServer().getPluginManager().registerEvents(new ModuleListener(this), this);
    }

    @RequiredArgsConstructor
    private class ModuleListener implements Listener {
        private final Infinity infinity;

        @EventHandler
        public void moduleEnabledEvent(ModuleEnabledEvent event) {
            if (event.getModule() instanceof InfinityControlModule) {
                infinity.controller = (InfinityControlModule) event.getModule();
                infinity.getController().setHook(new CoreAgent());
                getLogger().info("Control Module \"" + infinity.getController().getMeta().name() + "\" hooked");
            }
        }
    }

    // Action Methods

    public void registerSessionClass(
            Class<? extends SessionConfiguration> config,
            Class<? extends InfinitySession> type) {
        sessionClasses.put(config, type);
    }

}
