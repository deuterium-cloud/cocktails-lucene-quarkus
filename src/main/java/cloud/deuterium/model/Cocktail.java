package cloud.deuterium.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Created by Milan Stojkovic 21-Aug-2022
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Cocktail(
    Long id,
    String name,
    String glass,
    String category,
    String garnish,
    String preparation,
    String url,
    List<Ingredient> ingredients
){}
