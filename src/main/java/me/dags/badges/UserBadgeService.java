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
import me.dags.badges.service.BadgeService;
import org.spongepowered.api.entity.living.player.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * @author dags <dags@dags.me>
 */

class UserBadgeService implements BadgeService {

    private final Map<String, BadgePatch> playerPatches = new ConcurrentHashMap<>();
    private final Map<String, List<Badge>> individualBadges = new ConcurrentHashMap<>();
    private final List<Badge> permissionBadges = Collections.synchronizedList(new ArrayList<>());

    private final List<Consumer<BadgeService>> reloadConsumers = new ArrayList<>();

    final BadgesPlugin plugin;

    UserBadgeService(BadgesPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public BadgePatch getPatch(User user) {
        BadgePatch patch = playerPatches.get(user.getIdentifier());
        if (patch == null) {
            patch = recalculateUserPatch(user);
        }
        return patch;
    }

    @Override
    public BadgePatch recalculateUserPatch(User user) {
        BadgePatch patch = new UserPatch(user.getIdentifier(), this);

        List<Badge> individual = individualBadges.get(user.getIdentifier());
        if (individual != null) {
            individual.forEach(patch::attachBadge);
        }

        permissionBadges.stream().filter(badge -> badge.canWear(user)).forEach(patch::attachBadge);
        playerPatches.put(user.getIdentifier(), patch);
        return patch;
    }

    @Override
    public void registerReloader(Consumer<BadgeService> consumer) {
        reloadConsumers.add(consumer);
    }

    @Override
    public void registerPermissionBadge(Badge badge) {
        permissionBadges.add(badge);
    }

    @Override
    public void registerIdentifierBadge(String userIdentifier, Badge badge) {
        List<Badge> list = individualBadges.get(userIdentifier);
        if (list == null) {
            individualBadges.put(userIdentifier, list = new ArrayList<>());
        }
        list.add(badge);
    }

    @Override
    public void removePermissionBadge(String identifier) {
        remove(permissionBadges, identifier);
    }

    @Override
    public void removeIdentifierBadges(String identifier) {
        individualBadges.remove(identifier);
    }

    @Override
    public void removeIdentifierBadges(String identifier, String badgeIdentifier) {
        List<Badge> list = individualBadges.get(identifier);
        if (list != null) {
            remove(list, identifier);
        }
    }

    private void remove(List<Badge> badges, String identifier) {
        Iterator<Badge> iterator = badges.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().identifier().equals(identifier)) {
                iterator.remove();
                return;
            }
        }
    }

    void clear() {
        playerPatches.clear();
        individualBadges.clear();
        permissionBadges.clear();
    }

    void onReloadComplete() {
        reloadConsumers.forEach(consumer -> consumer.accept(this));
    }
}
