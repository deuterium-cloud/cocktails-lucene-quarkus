package cloud.deuterium.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

/**
 * Created by Milan Stojkovic 24-Sep-2022
 */

@ApplicationScoped
public class AppConfig {

    @ApplicationScoped
    @Produces
    public Directory memoryIndex(){
        return new ByteBuffersDirectory();
    }

    @ApplicationScoped
    @Produces
    public ObjectMapper objectMapper(){
        return new ObjectMapper();
    }
}
