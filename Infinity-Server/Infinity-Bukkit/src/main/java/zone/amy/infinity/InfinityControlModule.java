package zone.amy.infinity;

import lombok.Setter;
import zone.amy.infinity.modules.InfinityModule;

/**
 * Handles networking and actual control of the infinity server
 */
abstract class InfinityControlModule extends InfinityModule {
    @Setter private CoreHook hook;

    // Get Players (inc methods for db metadata)
}
