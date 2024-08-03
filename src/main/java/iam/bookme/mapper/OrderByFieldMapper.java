package iam.bookme.mapper;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/*
This class is responsible for mapping the orderBy field from the API to the corresponding database field.
It allows for adding new mappings and providing a default value for unknown fields.
 */
@NoArgsConstructor
public class OrderByFieldMapper {
    private final Map<String, String> mappings = new HashMap<>();
    @Setter
    private String defaultValue;

    public void addMapping(String apiField, String dbField) {
        mappings.put(apiField, dbField);
    }

    public String map(String key) {
        if (StringUtils.hasText(key)) {
            Assert.isTrue(mappings.containsKey(key), "Field: [%s] is an unknown sorting field".formatted(key));
        }
        return this.mappings.getOrDefault(key, defaultValue);
    }

}
