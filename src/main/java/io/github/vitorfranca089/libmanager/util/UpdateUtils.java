package io.github.vitorfranca089.libmanager.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class UpdateUtils {

    public static void updateIfNotEmpty(String newValue, Consumer<String> setter) {
        Optional.ofNullable(newValue)
                .filter(value -> !value.isBlank())
                .ifPresent(setter);
    }

    public static void updateIfValidYear(int newValue, IntConsumer setter) {
        if (newValue > 0) {
            setter.accept(newValue);
        }
    }

}
