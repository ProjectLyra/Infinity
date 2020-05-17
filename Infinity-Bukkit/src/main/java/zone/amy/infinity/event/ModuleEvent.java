package zone.amy.infinity.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import zone.amy.infinity.module.InfinityModule;

@RequiredArgsConstructor
public abstract class ModuleEvent extends Event {
    @Getter private static final HandlerList handlerList = new HandlerList();

    @Getter private final InfinityModule module;

}
