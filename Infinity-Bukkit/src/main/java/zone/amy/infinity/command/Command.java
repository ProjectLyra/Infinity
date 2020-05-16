package zone.amy.infinity.command;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class Command {
    @Getter private Meta meta;
    @Getter private List<Command> subCommands;
    @Getter private CommandHandler handler;

    public Command(CommandHandler handler) {
        this.handler = handler;

        meta = getClass().getAnnotation(Meta.class);
        if (meta == null) throw new IllegalStateException("Command \"" + getClass().getName() + "\" was not annotated with metadata");

        try {
            subCommands = new ArrayList<>();
            for (Class<? extends Command> subCommandClass : getMeta().subCommands()) {
                subCommands.add(subCommandClass.getConstructor(CommandHandler.class).newInstance(handler));
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public abstract boolean run(Player sender, String[] arguments, Command[] chain, Map<String, Object> data, boolean endsChain) ;
    public void onTreeFinished(Player sender, Command[] chain, Map<String, Object> data) {};

    public final String getUsage() {
        return getMeta().names()[0] + (!getMeta().syntax().equals("") ? " " + getMeta().syntax() : "");
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Meta {
        String[] names();
        String syntax() default "";
        String description();
        String permission();
        int argumentCount() default 0;
        Class<? extends Command>[] subCommands() default {};
    }
}
