package me.dags.badges.redo;

import com.google.inject.Inject;
import me.dags.commandbus.CommandBus;
import me.dags.commandbus.annotation.*;
import me.dags.commandbus.format.FMT;
import me.dags.textmu.MarkupSpec;
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
import org.spongepowered.api.plugin.Plugin;

import java.io.IOException;

/**
 * @author dags <dags@dags.me>
 */
@Plugin(id = "badges", name = "Badges", version = "1.1", description = "*_*")
public class Badges {

    static final String BADGE_PERM = "badges.badge.";
    private final ConfigurationLoader<CommentedConfigurationNode> loader;
    private ChatListener chatListener = null;

    @Inject
    public Badges(@DefaultConfig(sharedRoot = false) ConfigurationLoader<CommentedConfigurationNode> loader) {
        this.loader = loader;
    }

    @Listener
    public void init(GameInitializationEvent event) {
        CommandBus.create().register(this).submit(this);
        reload(null);
    }

    @Listener
    public void reload(GameReloadEvent event) {
        ConfigurationNode config = loadConfig();

        if (chatListener != null) {
            Sponge.getEventManager().unregisterListeners(chatListener);
        }

        chatListener = new ChatListener(config);
        saveConfig(config);

        Sponge.getEventManager().registerListeners(this, chatListener);
    }

    @Permission("badges.command.create")
    @Command(alias = "create", parent = "badge")
    public void create(@Caller CommandSource source, @One("name") String name, @Join("badge") String badge) {
        ConfigurationNode config = loadConfig();
        config.getNode("badges", name).setValue(badge);
        saveConfig(config);
        FMT.info("Created badge ").stress(name).info(": ").append(MarkupSpec.create().render(badge)).tell(source);
        refresh(source);
    }

    @Permission("badges.command.reload")
    @Command(alias = "refresh", parent = "badge")
    public void refresh(@Caller CommandSource source) {
        FMT.info("Reloading badges...").tell(source);
        reload(null);
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
