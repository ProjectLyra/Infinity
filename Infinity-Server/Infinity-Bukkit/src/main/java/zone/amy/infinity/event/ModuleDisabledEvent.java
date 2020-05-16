package zone.amy.infinity.event;

import zone.amy.infinity.modules.InfinityModule;

public class ModuleDisabledEvent extends ModuleEvent {
    public ModuleDisabledEvent(InfinityModule module) {
        super(module);
    }
}
