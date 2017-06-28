package me.dags.badges;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.registry.AdditionalCatalogRegistryModule;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author dags <dags@dags.me>
 */
public class BadgeRegistry implements AdditionalCatalogRegistryModule<Badge> {

    private final Map<String, Badge> external = new ConcurrentHashMap<>();
    private final Map<String, Badge> registry = new ConcurrentHashMap<>();
    private final Badges plugin;

    BadgeRegistry(Badges plugin) {
        this.plugin = plugin;
    }

    void register(Badge badge) {
        registry.put(badge.getId(), badge);
    }

    void clearInternal() {
        registry.clear();
        registry.putAll(external);
    }

    @Override
    public void registerAdditionalCatalog(Badge badge) {
        registry.put(badge.getId(), badge);
        external.put(badge.getName(), badge);
        plugin.scheduleUpdate();
    }

    @Override
    public Optional<Badge> getById(String id) {
        return Optional.ofNullable(registry.get(id));
    }

    @Override
    public Collection<Badge> getAll() {
        return ImmutableList.copyOf(registry.values());
    }
}
