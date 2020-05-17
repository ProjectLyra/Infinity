package zone.amy.infinity.event;

import lombok.Getter;
import org.bukkit.event.HandlerList;
import zone.amy.infinity.module.InfinityModule;

public class ModuleDisabledEvent extends ModuleEvent {
    @Getter private static final HandlerList handlerList = new HandlerList();
    @Getter(lazy = true) private final HandlerList handlers = getHandlerList();

    public ModuleDisabledEvent(InfinityModule module) {
        super(module);
    }
}
