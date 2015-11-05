package unnamed.integration;

public interface IIntegrationModule {
    String name();

    boolean canLoad();

    void load();
}