package me.dags.badges;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;

/**
 * @author dags <dags@dags.me>
 */
public interface Badge extends CatalogType, TextRepresentable {

    boolean applicableTo(Player player);

    @Override
    default String getName() {
        return getId();
    }

    static Badge permission(String identifier, String permission, Text badge) {
        return new PermissionBadge(identifier, permission, badge);
    }

    static Badge option(String identifier, String option, Text badge) {
        return new OptionBadge(identifier, option, badge);
    }
}
