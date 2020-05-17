package zone.amy.infinity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import zone.amy.infinity.event.ModuleEnabledEvent;
import zone.amy.infinity.module.InfinityModule;

import java.util.ArrayList;
import java.util.List;

public class ModuleListener implements Listener {
    List<InfinityModule> modules = new ArrayList<>();


    @EventHandler
    public void onModuleEnable(ModuleEnabledEvent event) {
        modules.add(event.getModule());
    }

    @EventHandler
    public void onModuleDisable(ModuleEnabledEvent event) {
        modules.remove(event.getModule());
    }
}
