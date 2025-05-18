package org.example.shared;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Map;

import static org.springframework.util.StringUtils.capitalize;

@Slf4j
public class Utils {
    /**
     * Copies properties from one object to another using reflection.
     *
     * @param source      The source object from which properties are copied.
     * @param destination The destination object to which properties are copied.
     */
    public static <SourceClass, DestinationClass> void copyPropertiesWithReflection(
        SourceClass source,
        DestinationClass destination
    ) {
        var sourceClass = source.getClass();
        var destinationClass = destination.getClass();

        for (Field field : sourceClass.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value == null) continue;
                String methodName = "set" + capitalize(field.getName());
                destinationClass.getMethod(methodName, field.getType()).invoke(destination, value);
            } catch (Exception e) {
                throw new RuntimeException(
                    "Error while checking update permissions for field: " + field.getName() +
                    " in class: " + sourceClass.getName(), e
                );
            } finally {
                field.setAccessible(false);
            }
        }
    }

    public static <DestinationClass> void copyPropertiesFromMapWithReflection(
        Map<String, Object> source,
        DestinationClass destination
    ) {
        var destinationClass = destination.getClass();

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String fieldName = entry.getKey();
            Object value = entry.getValue();
            if (value == null) continue;
            String methodName = "set" + capitalize(fieldName);
            try {
                destinationClass.getMethod(methodName, value.getClass()).invoke(destination, value);
            } catch (Exception e) {
                log.error(
                    "Error while setting field: {} in class: {}",
                    fieldName,
                    destinationClass.getName(),
                    e
                );

                throw new RuntimeException(
                    "Error while setting field: " + fieldName + " in class: " +
                    destinationClass.getName(), e
                );
            }
        }
    }
}
