// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;

/** Servlet that adds and removes comments from the page */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  
  /** A comment submitted by a user */
  private final class Comment {
    private final long id;
    private final long timestamp;
    private final String userComment;
    private final String userName;

    private Comment(long id, String userComment, long timestamp, String userName) {
        this.id = id;
        this.userComment = userComment;
        this.timestamp = timestamp;
        this.userName = userName;
    }
  }
  
  /**
  * Fetches data with a query, sorts data into Comment objects, 
  * and translates to JSON to load comments to the page
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // create and prepare a query
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // stores each comment in a comment object
    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      long timestamp = (long) entity.getProperty("timestamp");
      String userComment = (String) entity.getProperty("userComment");
      String userName = (String) entity.getProperty("userName");

      Comment comment = new Comment(id, userComment, timestamp, userName);
      comments.add(comment);
    }

    // translate to JSON for loadComments function
    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  /** Stores user comments in Datastore using entities */
  /**TODO: constrain user imput*/
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

      // create entity
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("timestamp", System.currentTimeMillis());
      commentEntity.setProperty("userComment", request.getParameter("user-comment"));
      commentEntity.setProperty("userName", request.getParameter("user-name"));

      // store entity
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      response.sendRedirect("/index.html");
  }

}
