package com.canopy.interviewing;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.rxjava.ext.web.client.WebClient;
import rx.Observable;

public class Start {
    private static final int NUM_STORIES = 10;
    private static final int NUM_COMMENTS = 3;

    static Observable<JsonArray> fetchTopStoriesWithComments(WebClient client) {
        return client
                .get("/v0/topstories.json")
                .rxSend()
                .toObservable()
                .map(response -> response.bodyAsString())
                .flatMap(arrayString -> Observable.from(new JsonArray(arrayString)))
                .take(NUM_STORIES)
                .flatMap(storyId ->
                        client.get(String.format("/v0/item/%s.json", storyId))
                                .rxSend()
                                .toObservable()
                                .map(response -> response.bodyAsString())
                                .flatMap(objectString -> {
                                    JsonObject story = new JsonObject(objectString);
                                    String title = story.getString("title");
                                    String url = story.getString("url");
                                    JsonArray commentIds = story.getJsonArray("kids"); // comments on the story are the "kids"

                                    JsonArray comments = new JsonArray();
                                    JsonObject storyObj = new JsonObject()
                                            .put("title", title)
                                            .put("url", url)
                                            .put("comments", comments);

                                    if (commentIds != null)
                                        return Observable.from(commentIds)
                                                .take(NUM_COMMENTS)
                                                .flatMap(commentId ->
                                                        client.get(String.format("/v0/item/%s.json", commentId))
                                                                .rxSend()
                                                                .toObservable()
                                                                .map(response -> response.bodyAsString())
                                                                .map(str -> new JsonObject(str))
                                                                .map(comment -> comment.getString("text"))
                                                )
                                                .reduce(comments, (agg, comment) -> agg.add(comment))
                                                .map(unused -> storyObj);
                                    else
                                        return Observable.just(storyObj);
                                })
                ).reduce(new JsonArray(), (stories, story) -> stories.add(story));
    }
}
