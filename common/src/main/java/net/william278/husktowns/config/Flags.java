/*
 * This file is part of HuskTowns, licensed under the Apache License 2.0.
 *
 *  Copyright (c) William278 <will27528@gmail.com>
 *  Copyright (c) contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package net.william278.husktowns.config;

import net.william278.annotaml.YamlComment;
import net.william278.annotaml.YamlFile;
import net.william278.annotaml.YamlKey;
import net.william278.husktowns.claim.Flag;
import net.william278.husktowns.listener.Operation;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@YamlFile(header = """
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃    HuskTowns Flags Config    ┃
        ┃    Developed by William278   ┃
        ┣━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
        ┣╸ This file is for configuring flags. Flag IDs map to a list of permitted operations.
        ┗╸ Config Help: https://william278.net/docs/husktowns/config-files""")
public class Flags {

    @YamlComment("A map of flag IDs to operations that flag permits." +
                 "Display names of flags correspond to a \"town_rule_name_\" locale in your messages file.")
    @YamlKey("flags")
    public Map<String, List<String>> flags = new LinkedHashMap<>(
            Flag.getDefaults().stream().collect(Collectors.toMap(
                    Flag::getName,
                    flag -> flag.getAllowedOperations().stream().map(Enum::name).collect(Collectors.toList())
            ))
    );

    private Flags() {
    }

    /**
     * Get the set of {@link Flag flags} being used by the plugin
     *
     * @return the set of flags
     */
    @NotNull
    public Set<Flag> getFlagSet() {
        final Set<Flag> flagSet = new LinkedHashSet<>();
        for (Map.Entry<String, List<String>> entry : flags.entrySet()) {
            flagSet.add(Flag.of(
                    entry.getKey(),
                    entry.getValue().stream()
                            .map(a -> Operation.Type.fromId(a).orElseThrow(
                                    () -> new IllegalArgumentException("Invalid operation type in flags config: " + a)))
                            .collect(Collectors.toUnmodifiableSet())
            ));
        }
        return flagSet;
    }

    /**
     * Set the set of {@link Flag flags} being used by the plugin
     *
     * @param flags the set of flags to use
     */
    public void setFlags(@NotNull Set<Flag> flags) {
        this.flags = flags.stream().collect(Collectors.toMap(
                Flag::getName,
                flag -> flag.getAllowedOperations().stream().map(Enum::name).collect(Collectors.toList())
        ));
    }

    /**
     * Lookup a {@link Flag} by its ID
     *
     * @param flagId the ID of the flag to lookup
     * @return the flag, if found
     */
    public Optional<Flag> getFlag(@NotNull String flagId) {
        return getFlagSet().stream().filter(flag -> flag.getName().equalsIgnoreCase(flagId)).findFirst();
    }

}
