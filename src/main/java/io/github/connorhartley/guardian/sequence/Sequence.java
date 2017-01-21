/*
 * MIT License
 *
 * Copyright (c) 2017 Connor Hartley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.connorhartley.guardian.sequence;

import io.github.connorhartley.guardian.detection.check.CheckProvider;
import io.github.connorhartley.guardian.sequence.action.Action;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;

import java.util.ArrayList;
import java.util.List;

public class Sequence {

    private final User user;
    private final CheckProvider checkProvider;

    private final List<Action> actions = new ArrayList<>();
    private final List<Event> completeEvents = new ArrayList<>();

    private long last = System.currentTimeMillis();

    private boolean cancelled = false;
    private boolean finished = false;

    public Sequence(User user, CheckProvider checkProvider, List<Action> actions) {
        this.user = user;
        this.checkProvider = checkProvider;
        this.actions.addAll(actions);
    }

    // ## Do this section...

    <T extends Event> boolean check(User user, T event) {
        return false;
    }

    // Human has passed with no detection of exploitation.
    boolean pass(User user, Action action, Cause cause) {
        return false;
    }

    // Human has failed with a detection of exploitaton.
    boolean fail(User user, Action action, Cause cause) {
        return false;
    }

    // ## End of section needing completion.

    boolean hasExpired() {
        if (this.actions.isEmpty()) {
            return false;
        }

        Action action = this.actions.get(0);
        long now = System.currentTimeMillis();

        return action != null && this.last + ((action.getExpire() / 20) * 1000) < now;
    }

    boolean isCancelled() {
        return this.cancelled;
    }

    boolean isFinished() {
        return this.finished;
    }

    public User getUser() {
        return this.user;
    }

    public CheckProvider getProvider() {
        return this.checkProvider;
    }

    public List<Event> getCompleteEvents() {
        return this.completeEvents;
    }

}
