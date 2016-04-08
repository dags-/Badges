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
import me.dags.badges.service.BadgePatch;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */

class UserPatch implements BadgePatch {

    private final List<Badge> badges = new ArrayList<>();
    private final UserBadgeService service;
    private final String identifier;

    UserPatch(String identifier, UserBadgeService service) {
        this.identifier = identifier;
        this.service = service;
    }

    @Override
    public void attachBadge(Badge badge) {
        badges.add(badge);
    }

    @Override
    public void clear() {
        badges.clear();
    }

    @Override
    public String identifier() {
        return identifier;
    }

    @Override
    public Text getText() {
        if (badges.isEmpty()) {
            return Text.EMPTY;
        }
        Text.Builder builder = Text.builder();
        builder.append(service.plugin.config().getStart());
        badges.stream().forEach(badge -> builder.append(badge.getText()));
        builder.append(service.plugin.config().getEnd());
        return builder.toText();
    }

    @Override
    public boolean canWear(User user) {
        return true;
    }
}
