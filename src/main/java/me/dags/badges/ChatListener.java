package me.dags.badges;

import com.google.common.collect.ImmutableList;
import me.dags.textmu.MarkupSpec;
import me.dags.textmu.MarkupTemplate;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.message.MessageChannelEvent;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author dags <dags@dags.me>
 */
public class ChatListener {

    private final List<Badge> badges;
    private final MarkupTemplate badge;
    private final MarkupTemplate patch;

    ChatListener(ConfigurationNode node) {
        String badgeTemplate = node.getNode("template", "badge").getString("{.}");
        String patchTemplate = node.getNode("template", "patch").getString("\\[{badges:badge}] ");
        ImmutableList.Builder<Badge> builder = ImmutableList.builder();
        for (Map.Entry<Object, ? extends ConfigurationNode> entry : node.getNode("badges").getChildrenMap().entrySet()) {
            String identifier = entry.getKey().toString();
            String badge = entry.getValue().getString();
            builder.add(new Badge(identifier, badge));
        }
        this.badge = MarkupSpec.create().template(badgeTemplate);
        this.patch = MarkupSpec.create().template(patchTemplate);
        this.badges = builder.build();
    }

    @Listener(order = Order.LAST)
    public void chat(MessageChannelEvent.Chat event, @Root Player player) {
        List<Badge> badges = this.badges.stream().filter(b -> b.applicableTo(player)).collect(Collectors.toList());
        if (!badges.isEmpty()) {
            event.getFormatter().getHeader().insert(0, patch.with("badges", badges).with("badge", badge));
        }
    }
}
