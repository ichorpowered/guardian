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
package io.github.connorhartley.guardian.punishment;

import com.google.common.reflect.TypeToken;
import io.github.connorhartley.guardian.Guardian;
import io.github.connorhartley.guardian.detection.Detection;
import io.github.connorhartley.guardian.storage.StorageSupplier;
import io.github.connorhartley.guardian.storage.container.StorageKey;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.util.Tuple;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Punishment Controller
 *
 * Controls punishment execution and registration.
 */
public class PunishmentController {

    private final Guardian plugin;
    private final HashMap<Detection, List<PunishmentType>> registry = new HashMap<>();

    public PunishmentController(Guardian plugin) {
        this.plugin = plugin;
    }

    /**
     * Execute
     *
     * <p>Executes a punishment to be handled by the appropriate handler
     * bound to the {@link Detection}.</p>
     *
     * @param detection The detection
     * @param user The user
     * @param punishment Information about this punishment
     */
     public <E, F extends StorageSupplier<File>> void execute(Detection<E, F> detection, User user, Punishment punishment) {
        Map<String, Tuple<Double, Double>> detectionPunishLevels = new HashMap<>();
        List<String> currentPunishLevels = new ArrayList<>();

        if (detection.getConfiguration().get().get(new StorageKey<>("punishment-levels"),
                new TypeToken<Map<String, Tuple<Double, Double>>>(){}).isPresent()) {
            detectionPunishLevels = detection.getConfiguration().get().get(new StorageKey<>("punishment-levels"),
                            new TypeToken<Map<String, Tuple<Double, Double>>>(){}).get().getValue();
        }

        for (Map.Entry<String, Tuple<Double, Double>> entry : detectionPunishLevels.entrySet()) {
            if (punishment.getProbability() >= entry.getValue().getFirst() &&
                    punishment.getProbability() <= entry.getValue().getSecond() &&
                    entry.getValue().getFirst() != -1 && entry.getValue().getSecond() != -1) {
                currentPunishLevels.add(entry.getKey());
            }
        }

        if (this.registry.get(detection) != null) {
            for (PunishmentType punishmentType : this.registry.get(detection)) {
                currentPunishLevels.forEach(currentPunishLevel -> {
                    String[] level = StringUtils.split(currentPunishLevel, "&");

                    if (punishmentType.getName().equals(level[0])) {
                        punishmentType.handle(new String[]{ level[1] }, user, punishment);
                    }
                });
            }
        }
    }

    /**
     * Bind
     *
     * <p>Registers a punishment handler to a {@link Detection}.</p>
     *
     * @param punishmentClass The punishment handler
     * @param detection The detection
     */
    public void bind(Class<? extends PunishmentType> punishmentClass, Detection detection) {
        if (this.registry.get(detection) == null) {
            List<PunishmentType> punishmentTypes = new ArrayList<>();

            try {
                Constructor<?> ctor = punishmentClass.getConstructor(Guardian.class, Detection.class);
                PunishmentType newPunishment = (PunishmentType) ctor.newInstance(this.plugin, detection);

                punishmentTypes.add(newPunishment);
                this.registry.put(detection, punishmentTypes);
            } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            boolean exists = false;
            for (PunishmentType punishmentSearch : this.registry.get(detection)) {
                if (punishmentSearch.getClass().equals(punishmentClass)) {
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                List<PunishmentType> punishmentTypes = new ArrayList<>();
                punishmentTypes.addAll(this.registry.get(detection));

                try {
                    Constructor<?> ctor = punishmentClass.getConstructor(Guardian.class, Detection.class);
                    PunishmentType newPunishment = (PunishmentType) ctor.newInstance(this.plugin, detection);

                    punishmentTypes.add(newPunishment);
                    this.registry.put(detection, punishmentTypes);
                } catch (NoSuchMethodException | InstantiationException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Unbind
     *
     * <p>Unregisters a punishment handler to a {@link Detection}.</p>
     *
     * @param punishmentClass The punishment handler
     * @param detection The detection
     */
    public void unbind(Class<? extends PunishmentType> punishmentClass, Detection detection) {
        if (this.registry.get(detection) != null) {
            this.registry.get(detection).removeIf(punishmentType -> punishmentType.getClass().equals(punishmentClass));
        }
    }

}
