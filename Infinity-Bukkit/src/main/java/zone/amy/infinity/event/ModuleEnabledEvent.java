package zone.amy.infinity.event;

import zone.amy.infinity.module.InfinityModule;

public class ModuleEnabledEvent extends ModuleEvent {
    public ModuleEnabledEvent(InfinityModule module) {
        super(module);
    }
}
