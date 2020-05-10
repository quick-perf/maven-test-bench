package org.quickperf.maven.bench;

/**
 * Define a command to perform in order to make a bench test for a project.
 */
public interface Command {

    /**
     * Execute a command.
     *
     * @return the output result.
     */
    String execute();
}
