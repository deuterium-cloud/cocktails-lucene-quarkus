package cloud.deuterium.config;

import cloud.deuterium.model.Cocktail;
import cloud.deuterium.model.CocktailBuilder;
import cloud.deuterium.model.Ingredient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexableField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Milan Stojkovic 27-Sep-2022
 */

@ApplicationScoped
public class CocktailMapper {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;

    public CocktailMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Document toDocument(Cocktail cocktail) {

        Document document = new Document();

        if (cocktail.name() != null) {
            document.add(new TextField("name", cocktail.name(), Field.Store.YES));
        }
        if (cocktail.glass() != null) {
            document.add(new TextField("glass", cocktail.glass(), Field.Store.YES));
        }
        if (cocktail.category() != null) {
            document.add(new TextField("category", cocktail.category(), Field.Store.YES));
        }
        if (cocktail.garnish() != null) {
            document.add(new StoredField("garnish", cocktail.garnish()));
        }
        if (cocktail.preparation() != null) {
            document.add(new StoredField("preparation", cocktail.preparation()));
        }
        if (cocktail.url() != null) {
            document.add(new StoredField("url", cocktail.url()));
        }
        if (cocktail.ingredients() != null && !cocktail.ingredients().isEmpty()) {
            document.add(new TextField("ingredientsString", getIngredientsString(cocktail.ingredients()), Field.Store.YES));
            document.add(new StoredField("ingredientsJson", getIngredientsJson(cocktail.ingredients())));
        }

        return document;
    }

    public Cocktail toCocktail(Document document) {

        CocktailBuilder builder = CocktailBuilder.getBuilder();

        IndexableField idField = document.getField("id");
        if (idField != null) {
            builder.id(idField.numericValue().longValue());
        }
        IndexableField nameField = document.getField("name");
        if (nameField != null) {
            builder.name(nameField.stringValue());
        }
        IndexableField glassField = document.getField("glass");
        if (glassField != null) {
            builder.glass(glassField.stringValue());
        }
        IndexableField categoryField = document.getField("category");
        if (categoryField != null) {
            builder.category(categoryField.stringValue());
        }
        IndexableField garnishField = document.getField("garnish");
        if (garnishField != null) {
            builder.garnish(garnishField.stringValue());
        }
        IndexableField preparationField = document.getField("preparation");
        if (preparationField != null) {
            builder.preparation(preparationField.stringValue());
        }
        IndexableField urlField = document.getField("url");
        if (urlField != null) {
            builder.url(urlField.stringValue());
        }

        IndexableField ingredientsJson = document.getField("ingredientsJson");
        if (ingredientsJson != null) {
            try {
                Ingredient[] ingredients = objectMapper.readValue(ingredientsJson.stringValue(), Ingredient[].class);
                builder.ingredients(Arrays.asList(ingredients));
            } catch (JsonProcessingException e) {
                LOGGER.error("Cannot parse Ingredients json string: {} : {}", ingredientsJson, e.getMessage());
                builder.ingredients(Collections.emptyList());
            }
        } else {
            builder.ingredients(Collections.emptyList());
        }

        return builder.build();
    }

    private String getIngredientsString(List<Ingredient> ingredients) {

        return ingredients.stream()
                .collect(
                        Collectors.teeing(
                                stringCollector(Ingredient::ingredient), // allIngredients
                                stringCollector(Ingredient::label), // allLabels
                                (allIngredients, allLabels) -> allIngredients + " " + allLabels
                        )
                );
    }

    private Collector<Ingredient, ?, String> stringCollector(Function<Ingredient, String> function) {

        return Collectors.mapping(
                function,
                Collectors.filtering(
                        Objects::nonNull,
                        Collectors.joining(" ")
                )
        );
    }

    private String getIngredientsJson(List<Ingredient> ingredients) {

        try {
            return objectMapper.writeValueAsString(ingredients);
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot transform Ingredients to Json string: {}", e.getMessage());
            return "[]";
        }
    }
}
