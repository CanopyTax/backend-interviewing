package com.canopy.interviewing;

import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.http.HttpServer;
import io.vertx.rxjava.ext.web.Router;
import io.vertx.rxjava.ext.web.RoutingContext;
import io.vertx.rxjava.ext.web.client.WebClient;

public class TopStoriesVerticle extends AbstractVerticle {
    public static final int TOP_STORIES_PORT = 8080;
    final WebClientOptions options = new WebClientOptions()
            .setDefaultHost("hacker-news.firebaseio.com")
            .setDefaultPort(443)
            .setSsl(true);

    WebClient client;

    @Override
    public void start(Future<Void> startFuture) {
        Router router = Router.router(vertx);
        client = WebClient.create(vertx, options);

        setupRoutes(router);
        startServer(router);

        startFuture.complete();
    }

    private void startServer(Router router) {
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(TOP_STORIES_PORT);
    }

    private void setupRoutes(Router router) {
        router.route(HttpMethod.GET, "/fetchStories").blockingHandler(this::handleFetchStories);
    }

    private void handleFetchStories(RoutingContext context) {
        FetchStories.fetchTopStoriesWithComments(client)
                .subscribe(data -> {
                    context.response().end(new JsonObject().put("topStories", data).toString());
                }, error -> {
                    error.printStackTrace();
                }, () -> System.out.println("\nFinished fetching all the stories with comments!\n"));
    }
}
