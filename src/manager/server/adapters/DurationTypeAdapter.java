package manager.server.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;

// Кастомный TypeAdapter для сериализации и десериализации объектов Duration.
public class DurationTypeAdapter extends TypeAdapter<Duration> {

    // Записывает Duration в JSON как количество минут.
    @Override
    public void write(JsonWriter jsonWriter, Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue(); // Если duration null, пишем null в JSON
        } else {
            jsonWriter.value(duration.toMinutes()); // Преобразуем Duration в минуты и записываем как число
        }
    }

    // Читает Duration из JSON как количество минут.
    @Override
    public Duration read(JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == com.google.gson.stream.JsonToken.NULL) { // проверяет, является ли следующий токен
            // в JSON потоке null
            jsonReader.nextNull(); // Пропускаем null
            return null; // Возвращаем null, если в JSON null
        }
        long minutes = jsonReader.nextLong(); // Читаем число (минуты)
        return Duration.ofMinutes(minutes); // Создаём Duration из минут
    }
}
