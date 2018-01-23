package com.canopy.interviewing;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class TopStoriesVerticleTest {

    @Rule
    public RunTestOnContext rule = new RunTestOnContext();

    Vertx vertx;
    WebClient client;

    @Before
    public void setUp(TestContext context) {
        vertx = new Vertx(rule.vertx());
        client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(TopStoriesVerticle.TOP_STORIES_PORT));
        Async async = context.async();

        vertx.rxDeployVerticle(TopStoriesVerticle.class.getName())
                .subscribe(deployID -> {
                            context.assertEquals(true, deployID != null);
                            async.complete();
                        },
                        error -> {
                            error.printStackTrace();
                            context.fail();
                        });
    }

    @Test
    public void start(TestContext context) {
        Async async = context.async();

        client.get("/fetchStories")
                .rxSend()
                .subscribe(stories -> {
                    System.out.println(new JsonObject(stories.bodyAsString()).encodePrettily());
                    async.complete();
                }, err -> {
                    err.printStackTrace();
                    context.fail();
                });
    }
}