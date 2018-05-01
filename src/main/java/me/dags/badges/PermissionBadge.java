package me.dags.badges;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * @author dags <dags@dags.me>
 */
class PermissionBadge implements Badge{

    private static final String BADGE_PERM = "badges.badge.";

    private final String permission;
    private final String identifier;
    private final Text text;

    PermissionBadge(String identifier, Text badge) {
        this(identifier, PermissionBadge.BADGE_PERM + identifier, badge);
    }

    PermissionBadge(String identifier, String permission, Text badge) {
        this.identifier = identifier;
        this.permission = permission;
        this.text = badge;
    }

    @Override
    public boolean applicableTo(Player player) {
        return player.hasPermission(permission);
    }

    @Override
    public Text toText() {
        return text;
    }

    @Override
    public String getId() {
        return identifier;
    }
}
