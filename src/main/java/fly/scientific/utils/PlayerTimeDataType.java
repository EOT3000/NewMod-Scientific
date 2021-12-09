package fly.scientific.utils;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerTimeDataType implements PersistentDataType<String, PlayerIntMapWrapper> {
    public static PlayerTimeDataType PLAYER_TIME_TYPE = new PlayerTimeDataType();

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<PlayerIntMapWrapper> getComplexType() {
        return PlayerIntMapWrapper.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull PlayerIntMapWrapper map, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        String s = "";

        for(Map.Entry<UUID, Integer> entry : map.getPlayers().entrySet()) {
            s+=(";"+entry.getKey().toString()+":"+entry.getValue().toString());
        }

        return s.replaceFirst(";", "");
    }

    @Override
    public @NotNull PlayerIntMapWrapper fromPrimitive(@NotNull String s, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
        Map<UUID, Integer> map = new HashMap<>();

        for(String string : s.split(";")) {
            String[] spl = string.split(":");

            map.put(UUID.fromString(spl[0]), Integer.parseInt(spl[1]));
        }

        return new PlayerIntMapWrapper(map);
    }
}
