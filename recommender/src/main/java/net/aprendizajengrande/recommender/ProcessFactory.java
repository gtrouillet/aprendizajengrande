package net.aprendizajengrande.recommender;

import java.io.File;

/**
 * Interface for Process creation methods
 * 
 * @author Germán E. Trouillet
 */
public interface ProcessFactory {

    /**
     * Creates a new {@link Process} for the program and arguments given in the
     * command parameter, sets it's working directory in executionDirectory, and
     * start the execution
     * 
     * @param executionDirectory
     *            the working directory of the process
     * @param command
     *            the program and arguments to execute
     * @return the {@link Process}
     * @throws Exception
     *             if any error occurs during the initialization and start of
     *             the process
     */
    public Process createProccess(File executionDirectory, String... command)
            throws Exception;

}
