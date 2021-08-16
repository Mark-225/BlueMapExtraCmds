package de.mark225.bluemapextracmds.util.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

public class EnumDataType<T extends Enum> implements PersistentDataType<String, T> {

    private Class<T> enumClass;
    private T fallbackValue;

    public EnumDataType(Class<T> enumClass, T fallbackValue){
        this.enumClass = enumClass;
        this.fallbackValue = fallbackValue;
    }

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<T> getComplexType() {
        return enumClass;
    }

    @Override
    public String toPrimitive(Enum complex, PersistentDataAdapterContext context) {
        return complex.name();
    }

    @Override
    public T fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        if(!enumClass.isEnum()) return null;
        Optional<T> optional = Arrays.stream(enumClass.getEnumConstants()).filter(entry -> entry.name().equals(primitive)).findFirst();
        if(!optional.isPresent()) return fallbackValue;
        return optional.get();
    }
}
