package rmit.saintgiong.discoveryapi.internal.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Country {
    private static final Map<String, Country> REGISTRY = new ConcurrentHashMap<>();

    private final String code;
    private final String name;
    private final String dialCode;
    private final int shardId;

    @JsonCreator
    public Country(
            @JsonProperty("code") String code,
            @JsonProperty("name") String name,
            @JsonProperty("dialCode") String dialCode,
            @JsonProperty("shardId") int shardId) {
        this.code = code;
        this.name = name;
        this.dialCode = dialCode;
        this.shardId = shardId;
    }

    public static void initialize(List<Country> countries) {
        REGISTRY.clear();
        countries.forEach(c -> REGISTRY.put(c.code, c));
    }

    public static Country getByCode(String code) {
        Country country = REGISTRY.get(code);
        if (country == null) {
            throw new IllegalArgumentException("Unknown country code: " + code);
        }
        return country;
    }

    @JsonCreator
    public static Country fromCode(String code) {
        return getByCode(code);
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDialCode() {
        return dialCode;
    }

    public int getShardId() {
        return shardId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Country country = (Country) o;
        return Objects.equals(code, country.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return "Country{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", dialCode='" + dialCode + '\'' +
                ", shardId=" + shardId +
                '}';
    }
}
