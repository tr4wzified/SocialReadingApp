package com.example.myread;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class Threads {
    private static final Threads singleton = new Threads( );

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private Threads() { }

    /* Static 'instance' method */
    public static Threads getInstance( ) {
        return singleton;
    }

    public ExecutorService threadPool = newFixedThreadPool(4);
}
