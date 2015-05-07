package com.samples.daggersamples;

import dagger.ObjectGraph;

/**
 * Represents a contract that provides a scoped object graph.
 */
public interface ScopedGraphProvider {
    /**
     * Returns a scoped object graph.
     *
     * @return Current scoped object graph.
     */
    ObjectGraph getGraph();
}
