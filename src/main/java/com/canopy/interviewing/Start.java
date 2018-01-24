package com.canopy.interviewing;

import io.vertx.rxjava.core.Vertx;

public class Start {
    public static void main(String[] args) {
        // meant to be run from the TopStoriesVerticleTest method for now
        Vertx vertx = Vertx.vertx();
        vertx.rxDeployVerticle(TopStoriesVerticle.class.getName())
                .subscribe(deployID -> {
                            vertx.close();
                        },
                        error -> {
                            error.printStackTrace();
                            vertx.close();
                        });
    }
}
