package me.dags.badges;

import me.dags.textmu.MarkupSpec;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.TextRepresentable;

/**
 * @author dags <dags@dags.me>
 */
public class Badge implements TextRepresentable {

    private final String identifier;
    private final Text text;

    Badge(String identifier, String badge) {
        this.identifier = identifier;
        this.text = MarkupSpec.create().render(badge);
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean applicableTo(User user) {
        return user.hasPermission(Badges.BADGE_PERM + getIdentifier());
    }

    @Override
    public Text toText() {
        return text;
    }
}
