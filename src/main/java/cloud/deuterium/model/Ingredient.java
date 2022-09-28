package cloud.deuterium.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by Milan Stojkovic 21-Aug-2022
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Ingredient(
        Long id,
        String unit,
        Double amount,
        String ingredient,
        String label,
        String special
) {
}
