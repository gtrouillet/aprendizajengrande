package net.aprendizajengrande.recommender;

import java.io.File;

import org.springframework.stereotype.Component;

/**
 * Default implementation of {@link ProcessFactory} using the
 * {@link ProcessBuilder} class
 * 
 * @author Germán E. Trouillet
 */
@Component("processFactory")
public class DefaultProcessFactory implements ProcessFactory {

    public Process createProccess(File executionDirectory, String... command)
            throws Exception {
        Process p = new ProcessBuilder(command).directory(executionDirectory)
                .start();
        return p;
    }

}
