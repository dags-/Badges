package me.dags.badges;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * @author dags <dags@dags.me>
 */
class OptionBadge implements Badge {

    private final String identifier;
    private final String option;
    private final Text badge;

    OptionBadge(String identifier, Text badge) {
        this(identifier, identifier, badge);
    }

    OptionBadge(String identifier, String option, Text badge) {
        this.identifier = identifier;
        this.option = option;
        this.badge = badge;
    }

    @Override
    public boolean applicableTo(Player player) {
        return player.getOption(option).isPresent();
    }

    @Override
    public String getId() {
        return identifier;
    }

    @Override
    public Text toText() {
        return badge;
    }
}
