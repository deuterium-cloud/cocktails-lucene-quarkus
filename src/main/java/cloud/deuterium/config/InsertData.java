package cloud.deuterium.config;

import cloud.deuterium.model.Cocktail;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.runtime.Startup;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Milan Stojkovic 22-Sep-2022
 */

@Startup
@ApplicationScoped
public class InsertData {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final Directory memoryIndex;
    private final CocktailMapper mapper;

    public InsertData(ObjectMapper objectMapper, Directory memoryIndex, CocktailMapper mapper) {
        this.objectMapper = objectMapper;
        this.memoryIndex = memoryIndex;
        this.mapper = mapper;
    }

    @PostConstruct
    void initialize() throws IOException {

        InputStream is = this.getClass()
                .getClassLoader()
                .getResourceAsStream("cocktails.json");

        Cocktail[] cocktails = objectMapper.readValue(is, Cocktail[].class);

        try (Analyzer analyzer = new StandardAnalyzer();
             IndexWriter writer = new IndexWriter(memoryIndex, new IndexWriterConfig(analyzer))) {

            for (Cocktail cocktail : cocktails) {
                Document document = mapper.toDocument(cocktail);
                writer.addDocument(document);
            }
            writer.commit();
        }
        LOGGER.info("Initial data successfully inserted: {} cocktails", cocktails.length);
    }
}
