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
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Gets bird votes from the form and returns bird vote data as a JSON object*/
@WebServlet("/favorite-bird")
public class favBirdDataServlet extends HttpServlet {

  /**
  * Fetches data with a query, sorts data into a hash map, 
  * and translates to JSON to load to the graph
  */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      
    //create and prepare a query
    Query query = new Query("Bird");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    // store each vote in a hash map
    Map<String, Integer> birdVotes = new HashMap<>();
    for (Entity entity : results.asIterable()) {
        String bird = (String) entity.getProperty("birdName");
        int currentVotes = birdVotes.containsKey(bird) ? birdVotes.get(bird) : 0;
        birdVotes.put(bird, currentVotes + 1);
    }

    // translate to json
    response.setContentType("application/json");
    Gson gson = new Gson();
    String json = gson.toJson(birdVotes);
    response.getWriter().println(json);
  }

  /** Stores bird votes in Datastore using entities */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    Entity birdEntity = new Entity("Bird");
    birdEntity.setProperty("birdName", request.getParameter("bird"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(birdEntity);

    response.sendRedirect("/More.html");
  }
}