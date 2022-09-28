package cloud.deuterium.model;

import java.util.List;

/**
 * Created by Milan Stojkovic 28-Sep-2022
 */
public class CocktailBuilder {

    private Long id;
    private String name;
    private String glass;
    private String category;
    private String garnish;
    private String preparation;
    private String url;
    private List<Ingredient> ingredients;

    private CocktailBuilder() {
    }

    public static CocktailBuilder getBuilder() {
        return new CocktailBuilder();
    }

    public CocktailBuilder id(Long id) {
        this.id = id;
        return this;
    }

    public CocktailBuilder name(String name) {
        this.name = name;
        return this;
    }

    public CocktailBuilder glass(String glass) {
        this.glass = glass;
        return this;
    }

    public CocktailBuilder category(String category) {
        this.category = category;
        return this;
    }

    public CocktailBuilder garnish(String garnish) {
        this.garnish = garnish;
        return this;
    }

    public CocktailBuilder preparation(String preparation) {
        this.preparation = preparation;
        return this;
    }

    public CocktailBuilder url(String url) {
        this.url = url;
        return this;
    }

    public CocktailBuilder ingredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
        return this;
    }

    public Cocktail build() {
        return new Cocktail(
                this.id,
                this.name,
                this.glass,
                this.category,
                this.garnish,
                this.preparation,
                this.url,
                this.ingredients
        );
    }
}
