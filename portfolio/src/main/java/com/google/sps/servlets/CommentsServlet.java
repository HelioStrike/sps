package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.Gson;
import com.google.sps.Constants;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that handles comments data */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Sort comments by newest
    Query query = new Query(Constants._COMMENT).addSort(Constants.TIMESTAMP, SortDirection.DESCENDING);

    //Get comments from datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    //Read comments from result and put into a list
    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String author = (String) entity.getProperty(Constants.AUTHOR);
      String commentBody = (String) entity.getProperty(Constants.COMMENT);
      long timestamp = (long) entity.getProperty(Constants.TIMESTAMP);

      Comment comment = new Comment(id, author, commentBody, timestamp);
      comments.add(comment);
    }

    //Return data as a JSON object
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    //Comment object parameters
    String author = request.getParameter(Constants.AUTHOR);
    String comment = request.getParameter(Constants.COMMENT);
    long timestamp = System.currentTimeMillis();

    //Validate parameters, and send error if invalid
    String errorMessage = "";
    if(author.trim().length() == 0) {
      errorMessage += Constants.AUTHOR_EMPTY_ERROR;
    }
    if(comment.length() < Constants.COMMENT_MIN_LENGTH || comment.length() > Constants.COMMENT_MAX_LENGTH) {
      errorMessage += Constants.COMMENT_LENGTH_ERROR;
    }
    if(errorMessage.length() > 0) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
      return;
    }

    //Create comment entity
    Entity commentEntity = new Entity(Constants._COMMENT);
    commentEntity.setProperty(Constants.AUTHOR, author);
    commentEntity.setProperty(Constants.COMMENT, comment);
    commentEntity.setProperty(Constants.TIMESTAMP, timestamp);

    //Store comment in datastore
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    //Redirect user back to homepage
    response.sendRedirect("/");
  }
}
