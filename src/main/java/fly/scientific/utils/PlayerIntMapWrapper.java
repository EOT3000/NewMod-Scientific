package fly.scientific.utils;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerIntMapWrapper {
    private Map<UUID, Integer> players = new HashMap<>();

    public void add(Player player) {
        players.put(player.getUniqueId(), players.getOrDefault(player.getUniqueId(), 0)+1);
    }

    public Map<UUID, Integer> getPlayers() {
        return new HashMap<>(players);
    }

    public PlayerIntMapWrapper(Map<UUID, Integer> map) {
        this.players = map;
    }
}
