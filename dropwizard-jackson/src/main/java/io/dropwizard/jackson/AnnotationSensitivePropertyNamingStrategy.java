package io.dropwizard.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;

/**
 * A {@link PropertyNamingStrategy} implementation which, if the declaring class of a property is
 * annotated with {@link JsonSnakeCase}, uses a
 * {@link com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy}, and uses
 * the default {@link PropertyNamingStrategy} otherwise.
 */
public class AnnotationSensitivePropertyNamingStrategy extends PropertyNamingStrategy {
    private static final long serialVersionUID = -1372862028366311230L;

    /**
     * The snake case naming strategy to use, if a class is annotated with {@link JsonSnakeCase}.
     */
    private final PropertyNamingStrategy snakeCase = new PropertyNamingStrategies.SnakeCaseStrategy();

    @Override
    public String nameForConstructorParameter(MapperConfig<?> config,
                                              AnnotatedParameter ctorParam,
                                              String defaultName) {
        if (ctorParam == null) {
            return defaultName;
        } else if (ctorParam.getDeclaringClass().isAnnotationPresent(JsonSnakeCase.class)) {
            return snakeCase.nameForConstructorParameter(config, ctorParam, defaultName);
        }
        return super.nameForConstructorParameter(config, ctorParam, defaultName);
    }

    @Override
    public String nameForField(MapperConfig<?> config,
                               AnnotatedField field,
                               String defaultName) {
        if (field == null) {
            return defaultName;
        } else if (field.getDeclaringClass().isAnnotationPresent(JsonSnakeCase.class)) {
            return snakeCase.nameForField(config, field, defaultName);
        }

        return super.nameForField(config, field, defaultName);
    }

    @Override
    public String nameForGetterMethod(MapperConfig<?> config,
                                      AnnotatedMethod method,
                                      String defaultName) {
        if (method == null) {
            return defaultName;
        } else if (method.getDeclaringClass().isAnnotationPresent(JsonSnakeCase.class)) {
            return snakeCase.nameForGetterMethod(config, method, defaultName);
        }
        return super.nameForGetterMethod(config, method, defaultName);
    }

    @Override
    public String nameForSetterMethod(MapperConfig<?> config,
                                      AnnotatedMethod method,
                                      String defaultName) {
        if (method == null) {
            return defaultName;
        } else if (method.getDeclaringClass().isAnnotationPresent(JsonSnakeCase.class)) {
            return snakeCase.nameForSetterMethod(config, method, defaultName);
        }
        return super.nameForSetterMethod(config, method, defaultName);
    }
}
