package work.novablog.mcplugin.mcweb.record;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.ListTag;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TickData {
    private final CompoundTag data;

    public TickData() {
        data = new CompoundTag();
    }

    /**
     * eventsの各々の要素をoperatorで処理して、その配列をnameキーのListTagとしてTickDataに保存する
     * operatorでnullを返した場合、そのデータは保存されない
     * @param name ListTagのキー
     * @param events 処理するイベントデータ
     * @param operator eventsの各々の要素を加工してCompoundTagとして返す処理
     */
    public <K, V> void saveEvents(String name, Map<K, V> events, MapElementToCompoundTagOperator<K, V> operator) {
        var eventsTag = new ListTag<>(CompoundTag.class);
        events.forEach((key, value) -> {
            var result = operator.operate(key, value);
            if(result != null) eventsTag.add(result);
        });
        if(eventsTag.size() != 0) data.put(name, eventsTag);
    }

    @FunctionalInterface
    public interface MapElementToCompoundTagOperator<K, V> {
        @Nullable CompoundTag operate(K key, V value);
    }

    public CompoundTag getCompoundTag() {
        return data;
    }
}
