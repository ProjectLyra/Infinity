package zone.amy.infinity.lib;

public interface RepresentableObject<T extends ExternalRepresentation> {
    T getExternalRepresentation();
}
