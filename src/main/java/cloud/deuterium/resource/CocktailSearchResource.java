package cloud.deuterium.resource;

import cloud.deuterium.model.Cocktail;
import cloud.deuterium.service.SearchCocktails;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Milan Stojkovic 24-Sep-2022
 */

@Path("/v1/search")
public class CocktailSearchResource {

    private final SearchCocktails searchCocktails;

    public CocktailSearchResource(SearchCocktails searchCocktails) {
        this.searchCocktails = searchCocktails;
    }

    @Path("/cocktails")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cocktail> searchCocktails(@QueryParam("name") String name,
                                          @QueryParam("category") String category,
                                          @QueryParam("glass") String glass) throws IOException {
        return searchCocktails.search(name, category, glass);
    }

    @Path("/ingredients")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Cocktail> searchIngredients(@QueryParam("term") String term) throws IOException {
        if (term == null || term.isBlank()) return Collections.emptyList();
        return searchCocktails.searchIngredients(term);
    }
}
