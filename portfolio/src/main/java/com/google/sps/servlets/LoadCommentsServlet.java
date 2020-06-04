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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import com.google.appengine.api.datastore.FetchOptions;

/** Servlet responsible for loading comments. */
@WebServlet("/load-comments")
public class LoadCommentsServlet extends HttpServlet {

  /**
  * Fetches data with a query and gets the most recent submition
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    // create and prepare a query 
    Query query = new Query("CommentNum").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    // get the most recent number submitted
    int maxComments = 1;
    List<Entity> resultsList = results.asList(FetchOptions.Builder.withLimit(maxComments));
    Entity entity = resultsList.get(0);
    long numComments = (long) entity.getProperty("number");

    response.getWriter().println(numComments);     
  }

  @Override 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    // get entity properties
    int commentNum = getNumComments(request);
    long timestamp = System.currentTimeMillis();

    // set entity properties
    Entity commentNumEntity = new Entity("CommentNum");
    commentNumEntity.setProperty("number", commentNum);
    commentNumEntity.setProperty("timestamp", timestamp);

    // store entity
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentNumEntity);

    response.sendRedirect("/index.html");      
  }

  /** Gets number of comments to print */
  private int getNumComments(HttpServletRequest request) {

    String numCommentsString = request.getParameter("comment-number");

    // ensures the number of comments is not null
    if(numCommentsString == null) {
        return 0;
    } else {
        return Integer.parseInt(numCommentsString);
    }

  }

}