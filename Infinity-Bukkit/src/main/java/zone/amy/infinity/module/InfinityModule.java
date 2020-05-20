package zone.amy.infinity.module;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import zone.amy.infinity.command.Command;
import zone.amy.infinity.command.CommandHandler;
import zone.amy.infinity.event.ModuleDisabledEvent;
import zone.amy.infinity.event.ModuleEnabledEvent;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class InfinityModule extends JavaPlugin {
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Meta {
        String name();
        String description();
    }

    @Getter private Meta meta;

    @Override
    public void onEnable() {
        if (getServer().getPluginManager().getPlugin("Infinity") == null) {
            throw new IllegalStateException("Infinity Core was not found.");
        }

        meta = getClass().getAnnotation(Meta.class);
        if (meta == null) throw new IllegalStateException(getClass().getName() + " was not annotated with metadata.");


        try {
            onModuleEnable();
            getServer().getPluginManager().callEvent(new ModuleEnabledEvent(this));
            // TODO: Add notification of enabling to local log
        } catch (IllegalStateException e) {
            getLogger().severe("Unable to enable module \"" + getClass().getName() + "\":");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public final void onDisable() {
        onModuleDisable();
        getServer().getPluginManager().callEvent(new ModuleDisabledEvent(this));
        getLogger().info("Gracefully disabled module \"" + getMeta().name() + "\"");
    }

    /* Methods for Implementation */
    protected void onModuleEnable() {}
    protected void onModuleDisable() {}
}
