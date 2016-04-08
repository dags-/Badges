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

import me.dags.dlib.commands.CommandMessenger;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

/**
 * @author dags <dags@dags.me>
 */

@ConfigSerializable
public class Config {

    private static final TextSerializer serializer = TextSerializers.FORMATTING_CODE;

    @Setting
    private String patchStart = "[";
    @Setting
    private String patchEnd = "]";
    @Setting
    private boolean prefixChat = true;
    @Setting
    private CommandMessenger messenger = new CommandMessenger();

    private transient Text start = Text.EMPTY;
    private transient Text end = Text.EMPTY;

    Text getStart() {
        if (start == Text.EMPTY) {
            start = serializer.deserialize(patchStart);
        }
        return start;
    }

    Text getEnd() {
        if (end == Text.EMPTY) {
            end = serializer.deserialize(patchEnd);
        }
        return end;
    }

    CommandMessenger.Builder message() {
        return messenger.builder();
    }

    boolean prefixChat() {
        return prefixChat;
    }
}
