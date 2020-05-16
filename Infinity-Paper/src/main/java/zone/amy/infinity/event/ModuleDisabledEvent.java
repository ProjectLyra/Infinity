package zone.amy.infinity.event;

import zone.amy.infinity.module.InfinityModule;

public class ModuleDisabledEvent extends ModuleEvent {
    public ModuleDisabledEvent(InfinityModule module) {
        super(module);
    }
}
