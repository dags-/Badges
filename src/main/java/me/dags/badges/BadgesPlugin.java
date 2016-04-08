/*
 * The MIT License (MIT)
 *
 * Copyright (c) dags <https://dags.me>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.dags.badges;

import com.google.inject.Inject;
import me.dags.badges.service.BadgePatch;
import me.dags.badges.service.BadgeService;
import me.dags.commandbus.CommandBus;
import me.dags.commandbus.annotation.Caller;
import me.dags.commandbus.annotation.Command;
import me.dags.commandbus.annotation.Join;
import me.dags.commandbus.annotation.One;
import me.dags.dlib.config.ConfigLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * @author dags <dags@dags.me>
 */

@Plugin(name = "Badges", id = "me.dags.badges", version = "1.0")
public class BadgesPlugin {

    @Inject
    @ConfigDir(sharedRoot = false)
    private Path configDir;
    private final ConfigLoader loader = ConfigLoader.newInstance();
    private final UserBadgeService service = new UserBadgeService(this);

    private Config config = new Config();

    @Listener(order = Order.EARLY)
    public void preInit(GamePreInitializationEvent event) {
        reload();
        CommandBus.newInstance().register(this).submit(this);
        Sponge.getServiceManager().setProvider(this, BadgeService.class, service);
    }

    @Listener (order = Order.LAST)
    public void onChat(MessageChannelEvent.Chat event, @First Player sender) {
        if (config().prefixChat()) {
            BadgePatch patch = service.getPatch(sender);
            event.setMessage(patch.getText().concat(event.getMessage()));
        }
    }

    @Listener
    public void onQuit(ClientConnectionEvent.Disconnect event) {
        service.removeIdentifierBadges(event.getTargetEntity().getIdentifier());
    }

    Config config() {
        return config;
    }

    private void reload() {
        service.clear();
        loadConfig();
        loadBadges();
        service.onReloadComplete();
    }

    private Config loadConfig() {
        Path configPath = configDir.resolve("config.conf");
        Optional<Config> optional = loader.fromHocon(configPath, Config.class);
        Config config = optional.orElse(new Config());
        if (!optional.isPresent()) {
            loader.toHocon(config, configPath);
        }
        return config;
    }

    private void loadBadges() {
        Path dir = configDir.resolve("badges");
        List<PermissionBadge> badges = loader.loadAll(dir, ".conf", p -> loader.fromHocon(p, PermissionBadge.class));
        badges.forEach(service::registerPermissionBadge);
    }

    @Command(aliases = "create", parent = "badge", perm = "badges.command.create")
    public void createBadge(@Caller CommandSource source, @One("name") String name, @One("badge") String badge, @Join("description") String desc) {
        PermissionBadge permBadge = new PermissionBadge(name, badge, desc);
        config().message().info("Created badge: ").append(permBadge.getText()).tell(source);
        Path save = configDir.resolve("badges").resolve(permBadge.identifier() + ".conf");
        loader.toHocon(permBadge, save);
        service.registerPermissionBadge(permBadge);
    }

    @Command(aliases = "reload", parent = "badge", perm = "badges.command.create")
    public void reload(@Caller CommandSource source) {
        config().message().info("Reloading... ").error("(note - this may break other plugins using badges!)").tell(source);
        refreshAll(source);
        reload();
    }

    @Command(aliases = "refresh", parent = "badge", perm = "badges.command.refresh.user")
    public void refresh(@Caller CommandSource source, @One("target") Player target) {
        config().message().info("Refreshing user ").info(target.getName()).tell(source);
        config().message().stress(source.getName()).info(" refreshed your badges").tell(target);
        service.recalculateUserPatch(target);
    }

    @Command(aliases = "all", parent = "badge refresh", perm = "badges.command.refresh.all")
    public void refreshAll(@Caller CommandSource source) {
        config().message().info("Refreshing all users...").tell(source);
        Sponge.getServer().getOnlinePlayers().forEach(service::recalculateUserPatch);
    }
}
