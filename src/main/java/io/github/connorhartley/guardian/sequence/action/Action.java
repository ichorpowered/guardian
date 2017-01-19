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
package io.github.connorhartley.guardian.sequence.action;

import io.github.connorhartley.guardian.sequence.condition.Condition;
import org.spongepowered.api.event.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Action<H, T extends Event> {

    private final List<Condition<H, T>> conditions = new ArrayList<>();
    private final List<Condition> successfulListeners = new ArrayList<>();
    private final List<Condition> failedListeners = new ArrayList<>();

    private final Class<T> event;

    private int delay;
    private int expire;

    Action(Class<T> event, Condition<H, T>... conditions) {
        this(event);
        this.conditions.addAll(Arrays.asList(conditions));
    }

    public Action(Class<T> event, List<Condition<H, T>> conditions) {
        this(event);
        this.conditions.addAll(conditions);
    }

    public Action(Class<T> event) {
        this.event = event;
    }

    void addCondition(Condition<H, T> condition) {

    }

    void setDelay(int delay) {

    }

    void setExpire(int expire) {

    }

    public void succeed(H human, Event event) {

    }

    public boolean fail(H human, Event event) {
        return false;
    }

    public boolean testConditions(H human, T event) {
        return false;
    }

    public Class<T> getEvent() {
        return event;
    }

    public List<Condition<H, T>> getConditions() {
        return this.conditions;
    }

    public int getDelay() {
        return this.delay;
    }

    public int getExpire() {
        return this.expire;
    }

    void onSuccess(Condition condition) {
        this.successfulListeners.add(condition);
    }

    void onFailure(Condition condition) {
        this.failedListeners.add(condition);
    }

}
