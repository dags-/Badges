package me.dags.badges;

import me.dags.textmu.MarkupTemplate;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */
public class ChatListener {

    private final Collection<Badge> badges;
    private final MarkupTemplate badge;
    private final MarkupTemplate patch;

    ChatListener(MarkupTemplate badge, MarkupTemplate patch) {
        this.badges = Sponge.getRegistry().getAllOf(Badge.class);
        this.badge = badge;
        this.patch = patch;
    }

    @Listener(order = Order.LAST)
    public void chat(MessageChannelEvent.Chat event, @Root Player player) {
        List<Badge> badges = this.badges.stream().filter(b -> b.applicableTo(player)).collect(Collectors.toList());
        if (!badges.isEmpty()) {
            event.getFormatter().getHeader().insert(0, patch.with("badges", badges).with("badge", badge));
        }
    }
}
