package zone.amy.infinity.event;

import zone.amy.infinity.modules.InfinityModule;

public class ModuleEnabledEvent extends ModuleEvent {
    public ModuleEnabledEvent(InfinityModule module) {
        super(module);
    }
}
