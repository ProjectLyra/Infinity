package zone.amy.infinity.command;

public class Command {
    public @interface Meta {
        String[] keywords();
        String description();
    }
}
