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

package me.dags.badges.service;

import org.spongepowered.api.entity.living.player.User;

import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */

public interface BadgeService {

    BadgePatch getPatch(User user);

    BadgePatch recalculateUserPatch(User user);

    void registerReloader(Consumer<BadgeService> consumer);

    void registerPermissionBadge(Badge badge);

    void registerIdentifierBadge(String identifier, Badge badge);

    void removePermissionBadge(String identifier);

    void removeIdentifierBadges(String identifier);

    void removeIdentifierBadges(String identifier, String badgeIdentifier);

}
