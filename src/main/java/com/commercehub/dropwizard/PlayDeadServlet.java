package com.commercehub.dropwizard;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;

public class PlayDeadServlet extends HttpServlet {

    private static final String CONTENT_TYPE = "text/plain";
    private static final String OK_RESPONSE_TEXT = "ready";
    private static final String STANDBY_RESPONSE_TEXT = "standby";

    PlayDead playDead;

    public PlayDeadServlet(PlayDead playDead) {
        this.playDead = playDead;
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
            handleExceptionResponse(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        if (hasAccess(request)) {
            try {
                playDead.startPlayingDead();
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                handleExceptionResponse(response, e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
        if (hasAccess(request)) {
            try {
                playDead.stopPlayingDead();
                response.setStatus(HttpServletResponse.SC_OK);
            } catch (IOException e) {
                handleExceptionResponse(response, e);
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void handleExceptionResponse(HttpServletResponse response, Exception exception) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        if(playDead.getConfig().showMessageOnError()) {
            try {
                exception.printStackTrace(new PrintStream(response.getOutputStream()));
            } catch (IOException ignore) { }
        }
    }

    private boolean hasAccess(HttpServletRequest request) {
        return playDead.getConfig().getAccessKey() == null ||
                (request.getQueryString() != null && request.getQueryString().equals(playDead.getConfig().getAccessKey()));
    }
}
