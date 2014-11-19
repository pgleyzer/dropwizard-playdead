package com.commercehub.dropwizard;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by awilson on 11/18/14.
 */

public class PlayDeadServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/plain";
    private static final String OK_RESPONSE_TEXT = "ready";
    private static final String STANDBY_RESPONSE_TEXT = "standby";

    PlayDead playDead;

    public PlayDeadServlet() {
        playDead = PlayDead.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        try {
            if (playDead.isPlayingDead()) {
                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                    response.getOutputStream().print(STANDBY_RESPONSE_TEXT);
            } else {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print(OK_RESPONSE_TEXT);
            }
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        playDead.startPlayingDead();
        response.setStatus(playDead.isPlayingDead() ? HttpServletResponse.SC_OK : HttpServletResponse.SC_UNAUTHORIZED );
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");

        playDead.stopPlayingDead();
        response.setStatus(playDead.isPlayingDead() ?  HttpServletResponse.SC_UNAUTHORIZED : HttpServletResponse.SC_OK );
    }
}
