package cloud.deuterium.service;

import cloud.deuterium.config.CocktailMapper;
import cloud.deuterium.model.Cocktail;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Milan Stojkovic 24-Sep-2022
 */

@ApplicationScoped
public class SearchCocktails {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private static final int LIMIT = 10;

    private final Directory memoryIndex;
    private final CocktailMapper mapper;

    public SearchCocktails(Directory memoryIndex, CocktailMapper mapper) {
        this.memoryIndex = memoryIndex;
        this.mapper = mapper;
    }

    public List<Cocktail> search(String name, String category, String glass) throws IOException {
        BooleanQuery query = getQuery(name, category, glass);
        return searchForCocktails(query);
    }

    public List<Cocktail> searchIngredients(String term) throws IOException {
        WildcardQuery query = new WildcardQuery(new Term("ingredientsString", "*%s*".formatted(term.toLowerCase())));
        return searchForCocktails(query);
    }

    private List<Cocktail> searchForCocktails(Query query) throws IOException {
        IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(memoryIndex));
        TopDocs docs = searcher.search(query, LIMIT);
        List<Document> documentList = getList(docs.scoreDocs, searcher);

        return documentList.stream()
                .map(mapper::toCocktail)
                .toList();
    }

    private static BooleanQuery getQuery(String name, String category, String glass) {
        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        if (name != null) {
            builder.add(new TermQuery(new Term("name", name.toLowerCase())), BooleanClause.Occur.SHOULD);
        }
        if (category != null) {
            builder.add(new TermQuery(new Term("category", category.toLowerCase())), BooleanClause.Occur.SHOULD);
        }
        if (glass != null) {
            builder.add(new TermQuery(new Term("glass", glass.toLowerCase())), BooleanClause.Occur.SHOULD);
        }

        return builder.build();
    }

    private List<Document> getList(ScoreDoc[] scoreDocs, IndexSearcher searcher) throws IOException {
        List<Document> documents = new ArrayList<>();
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docID = scoreDoc.doc;
            Document document = searcher.doc(docID);
            documents.add(document);
        }
        return documents;
    }

}
