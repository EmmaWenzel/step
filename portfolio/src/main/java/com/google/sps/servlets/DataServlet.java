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
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import java.util.ArrayList;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

      // store user comment 
      String userComment = getComment(request);
      ArrayList<String> commentArray = new ArrayList<String>();
      commentArray.add(userComment);
      System.out.println(commentArray);

      // get entity properties
      long timestamp = System.currentTimeMillis();

      // create entity
      Entity taskEntity = new Entity("Task");
      taskEntity.setProperty("timestamp", timestamp);
      taskEntity.setProperty("stringValue", userComment);

      // store entity
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(taskEntity);

      response.sendRedirect("/index.html");
  }

  private String getComment(HttpServletRequest request){
      String userComment = request.getParameter("user-comment");
      return userComment;
  }

}
