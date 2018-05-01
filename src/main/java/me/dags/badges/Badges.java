package me.dags.badges;

import com.google.inject.Inject;
import java.io.IOException;
import java.util.Map;
import me.dags.commandbus.CommandBus;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.Description;
import me.dags.commandbus.annotation.Join;
import me.dags.commandbus.annotation.Permission;
import me.dags.commandbus.annotation.Src;
import me.dags.commandbus.fmt.Fmt;
import me.dags.textmu.MarkupSpec;
import me.dags.textmu.MarkupTemplate;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.scheduler.Task;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "badges", name = "Badges", version = "1.3.0", description = "*_*")
public class Badges {

    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private final BadgeRegistry registry = new BadgeRegistry(this);
    private final MarkupSpec markupSpec = MarkupSpec.create();

    private volatile boolean updateScheduled = false;

    private ChatListener chatListener = null;
    private MarkupTemplate badge = MarkupSpec.create().template("");
    private MarkupTemplate patch = MarkupSpec.create().template("");

    @Inject
    public Badges(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
    }

    @Listener
    public void pre(GamePreInitializationEvent event) {
        Sponge.getRegistry().registerModule(Badge.class, registry);
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create(this).register(this).submit();
        reload(null);
    }

    @Listener
    public void reload(GameReloadEvent event) {
        ConfigurationNode config = loadConfig();

        registry.clearInternal();
        badge = markupSpec.template(config.getNode("template", "badge").getString("{.}"));
        patch = markupSpec.template(config.getNode("template", "patch").getString("\\[{badges:badge}] "));

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getNode("permission_badges").getChildrenMap().entrySet()) {
            String identifier = entry.getKey().toString();
            String badge = entry.getValue().getString();
            registry.register(new PermissionBadge(identifier, markupSpec.render(badge)));
        }

        for (Map.Entry<Object, ? extends ConfigurationNode> entry : config.getNode("option_badges").getChildrenMap().entrySet()) {
            String identifier = entry.getKey().toString();
            String badge = entry.getValue().getString();
            registry.register(new OptionBadge(identifier, markupSpec.render(badge)));
        }

        saveConfig(config);
        scheduleUpdate();
    }

    @Permission
    @Command("badge create perm <name> <badge>")
    @Description("Create a badge that is given to users with the permission 'badges.badge.<name>'")
    public void createPerm(@Src CommandSource source, String name, @Join String badge) {
        ConfigurationNode config = loadConfig();
        config.getNode("permission_badges", name).setValue(badge);
        saveConfig(config);

        Fmt.info("Created permission badge ").stress(name).info(": ").append(markupSpec.render(badge)).tell(source);
        refresh(source);
    }

    @Permission
    @Command("badge create option <name> <badge>")
    @Description("Create a badge that is given to users with the option <name>")
    public void createOption(@Src CommandSource source, String name, @Join String badge) {
        ConfigurationNode config = loadConfig();
        config.getNode("option_badges", name).setValue(badge);
        saveConfig(config);

        Fmt.info("Created option badge ").stress(name).info(": ").append(markupSpec.render(badge)).tell(source);
        refresh(source);
    }

    @Permission
    @Command("badge refresh")
    public void refresh(@Src CommandSource source) {
        Fmt.info("Reloading badges...").tell(source);
        reload(null);
    }

    synchronized void scheduleUpdate() {
        if (updateScheduled) {
            return;
        }

        updateScheduled = true;

        Task.builder().execute(() -> {
            if (chatListener != null) {
                Sponge.getEventManager().unregisterListeners(chatListener);
            }

            chatListener = new ChatListener(badge, patch);
            Sponge.getEventManager().registerListeners(this, chatListener);

            updateScheduled = false;
        }).submit(this);
    }

    private CommentedConfigurationNode loadConfig() {
        try {
            return loader.load(ConfigurationOptions.defaults().setShouldCopyDefaults(true));
        } catch (IOException e) {
            return loader.createEmptyNode();
        }
    }

    private void saveConfig(ConfigurationNode config) {
        try {
            loader.save(config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
