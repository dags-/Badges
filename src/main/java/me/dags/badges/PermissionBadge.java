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

import me.dags.badges.service.Badge;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * @author dags <dags@dags.me>
 */

@ConfigSerializable
class PermissionBadge implements Badge {

    private static final TextSerializer serializer = TextSerializers.FORMATTING_CODE;

    @Setting
    private String name = "some-badge";
    @Setting
    private String badge = "&a*";
    @Setting
    private String description = "Some description of what the badge represents";

    private transient Text text = Text.EMPTY;

    public PermissionBadge() {}

    PermissionBadge(String name, String badge, String description) {
        this.name = name;
        this.badge = badge;
        this.description = description;
    }

    @Override
    public String identifier() {
        return name;
    }

    @Override
    public Text getText() {
        return getBadge();
    }

    @Override
    public boolean canWear(User user) {
        return user.hasPermission("badges." + name);
    }

    @Override
    public String toString() {
        return "name=" + name + ",badge=" + badge + ",desc=" + description;
    }

    private Text getBadge() {
        if (text == Text.EMPTY) {
            Text badgeText = serializer.deserialize(badge);
            Text hoverText = serializer.deserialize(description);
            text = badgeText.toBuilder().onHover(TextActions.showText(hoverText)).build();
        }
        return text;
    }
}
