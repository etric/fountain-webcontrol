package com.e3k.fountain.webcontrol.web;

import com.e3k.fountain.webcontrol.io.player.MusicPlayer;
import com.e3k.fountain.webcontrol.io.player.PlaylistUtils;
import spark.Route;
import spark.Spark;

import javax.servlet.MultipartConfigElement;

import static spark.Spark.get;
import static spark.Spark.put;

final class MusicEndpoints {

  static void init() {

    // Page
    final Route pageRoute = (request, response) ->
        WebServer.class.getClassLoader().getResourceAsStream("music.html");

    get("music-unsafe", pageRoute);
    get("api/music/page", pageRoute);

    // MUSIC
    get("/api/music/currentPlayingItem", (request, response) -> {
      response.status(200);
      final int techNum = MusicPlayer.ONE.getCurrentPlayingItem();
      return techNum + 1;
    });
    Spark.get("/api/music/playlist", (request, response) -> {
      response.status(200);
      return MusicPlayer.ONE.getPlaylistItems();
    }, JsonResponseTransformer.ONE);
    put("/api/music/:musicNum", (request, response) -> {
      final int realNum = Integer.parseInt(request.params(":musicNum"));
      final int techNum = realNum - 1;
      if (!PlaylistUtils.isValidMusicNum(techNum)) {
        response.status(400);
        return "Music # must be within range 1.." + PlaylistUtils.PLAYLIST_SIZE;
      }
      if (MusicPlayer.ONE.getCurrentPlayingItem() == techNum) {
        response.status(400);
        return "Music # is currently playing";
      }
      request.attribute("org.eclipse.jetty.multipartConfig",
          new MultipartConfigElement("/temp"));

      MusicUploadHelper.upload(request.raw().getPart("file"), realNum);
      response.status(200);
      return "OK";
    });

    // VOLUME
    get("/api/volume", (request, response) -> {
      String vol = String.valueOf(MusicPlayer.ONE.getVolume());
      response.status(200);
      return vol;
    });
    put("/api/volume/:val", (request, response) -> {
      final int vol = Integer.valueOf(request.params(":val"));
      if (vol < 1 || vol > 100) {
        response.status(400);
        return "Volume must be within range 1..100";
      }
      MusicPlayer.ONE.changeVolume(vol);
      response.status(200);
      return "OK";
    });

    // PAUSE BETWEEN TRACKS
    get("/api/pauseBetweenTracks", (request, response) -> {
      String pauseBetweenTracks = String.valueOf(MusicPlayer.ONE.getPauseBetweenTracks());
      response.status(200);
      return pauseBetweenTracks;
    });
    put("/api/pauseBetweenTracks/:val", (request, response) -> {
      final int pauseBetweenTracks = Integer.valueOf(request.params(":val"));
      if (pauseBetweenTracks < 0 || pauseBetweenTracks > 300) {
        response.status(400);
        return "Pause Between Tracks must be within range 0..300";
      }
      MusicPlayer.ONE.changePauseBetweenTracks(pauseBetweenTracks);
      response.status(200);
      return "OK";
    });
  }
}
