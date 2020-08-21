package org.ual.aas.tasklists.views;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.MalformedInputException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection; 
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Statement;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.mapping.Map;
import org.json.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
import org.ual.aas.tasklists.controllers.TaskListController;
import org.ual.aas.tasklists.models.Task;

@WebServlet("/lists/*")
public class TaskListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	TaskListController controller = new TaskListController();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("application/json");
		PrintWriter writer = resp.getWriter();
        
		//dividir o array por "/" e perceber qual o url em questão
        String[] splits = req.getRequestURI().split("/");
        
        
        //obter as listas
        if(splits.length == 3) {
        	       	       	      
        		for (int i=0; i<controller.readTaskLists().size(); i++) {
        			
        			int id = controller.readTaskLists().get(i).getId();
        			String getNome = controller.readTaskLists().get(i).getName();
        			
        			//create Json Object
        			JSONObject json = new JSONObject();

            	    //JSON.
            		json.accumulate("id", id);
            		json.accumulate("name", getNome);
            		
            		//Output do JSON
            		writer.print(json.toString()+"\n");  			
        	        
        		} 	    
        		writer.close();
        }
        
       //obter uma lista especifica
       else if(splits.length == 4 && !splits[3].equals("*")) {
   	   
    	   for (int i=0; i<controller.readTaskList(splits[3]).getTasks().size(); i++) {
    	   
    	   int idLista = controller.readTaskList(splits[3]).getId();
           String nameList = controller.readTaskList(splits[3]).getName();
    	   int idTask = controller.readTaskList(splits[3]).getTasks().get(i).getId();
    	   String descrTask = controller.readTaskList(splits[3]).getTasks().get(i).getDescription();
    	   String statusTask = controller.readTaskList(splits[3]).getTasks().get(i).getStatus();
    	   
    	   
    	   JSONObject json = new JSONObject();
	   	    
    	   //JSON.
	   		json.accumulate("idList", idLista);
	   		json.accumulate("nameList", nameList);
	   		json.accumulate("idTask", idTask);
	   		json.accumulate("descriptionTask", descrTask);
	   		json.accumulate("statusTask", statusTask);
	   		
	
	   		//Output do JSON
	   	    writer.print(json.toString()+"\n");
    	   
    	   }
    	   writer.close();
        }
        
      //obter uma lista e uma tarefa especifica
       else if(splits.length == 5) {
	   	   
    	   int idTask = controller.getTask((splits[3]),(splits[4])).getId();
    	   String descTask = controller.getTask((splits[3]),(splits[4])).getDescription();
    	   String statusTask = controller.getTask((splits[3]),(splits[4])).getStatus();
	   
    	   JSONObject json = new JSONObject();

    	   
    	   	//JSON.
	   		json.accumulate("idTask", idTask);
	   		json.accumulate("descriptionTask", descTask);
	   		json.accumulate("statusTask", statusTask);

	   	    
	   		//Output do JSON
	   	   writer.print(json.toString()+"\n");   
    	   writer.close();
        }
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		TaskListController controller = new TaskListController();
		resp.setContentType("application/json");

		String[] splits = req.getRequestURI().split("/");
		
		//elimina uma certa lista (dependendo do id da lista)
		if(splits.length == 4 && !splits[3].equals("*")) {
			System.out.println(splits[3]);
			controller.deleteTaskList(splits[3]);
		
		//elimina uma task (dependendo do id da task)
		}else if(splits.length==5) {
			controller.deleteTask(splits[3],splits[4]);
	        
		}
	}
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		TaskListController controller = new TaskListController();
		resp.setContentType("application/json");
		
		String[] splits = req.getRequestURI().split("/");
		PrintWriter writer = resp.getWriter();
			
		
		if(splits.length == 3) {// /lists/
			
			//O seguinte codigo obtem JSON do request para String e assim chamamos a função para inserir na BD
			StringBuffer jb = new StringBuffer();
		      String line = null;
		      try {
		        BufferedReader reader = req.getReader();
		        while ((line = reader.readLine()) != null)
		          jb.append(line);
		        JSONObject jsonObject = new JSONObject(jb.toString());
		        String nameList = jsonObject.getString("name");
		        System.out.println(nameList);
		        
		        // criar uma lista
				controller.createTaskList(nameList);
				
				//este ciclo vai devolver o id da lista criada
				for (int i=0; i<controller.readTaskLists().size(); i++) {
					if(nameList.equals(controller.readTaskLists().get(i).getName())) {
						
						int idLista = controller.readTaskLists().get(i).getId();
						
						JSONObject json = new JSONObject();
						
						//transformaçao para Json
						json.accumulate("id", idLista);
						//printar o id da lista criada
						writer.print(json.toString()+"\n");					
					}
				else {
					System.out.println("Erro");
				}
			}
				
				
		      } catch (Exception e) { 
		          e.printStackTrace();        
		      }

		}
		
		else if(splits.length == 4 && !splits[3].equals("*")) {// /lists/list_id/
			
			//O seguinte codigo obtem JSON do request para String e assim chamamos a função para inserir na BD
			StringBuffer jb = new StringBuffer();
		      String line = null;
		      try {
		        BufferedReader reader = req.getReader();
		        while ((line = reader.readLine()) != null)
		          jb.append(line);
		        JSONObject jsonObject = new JSONObject(jb.toString());
		        String descTask = jsonObject.getString("descriptionTask");
		        String statusTask = jsonObject.getString("statusTask");
		        System.out.println(descTask);
		        System.out.println(statusTask);
		        
		        //criar task
				controller.createTask(splits[3],descTask,statusTask);
				
				//obter o id da task (o size será igual ao ultimo id dessa lista)
				int idLastTask = (controller.readTaskList(splits[3]).getTasks().size()-1);
				
				//Agora conseguimos obter o id da ultima task criada naquela lista
				int idFinal = (controller.readTaskList(splits[3]).getTasks().get(idLastTask).getId());

						JSONObject json = new JSONObject();
						
						//transformaçao para Json
						json.accumulate("id", (idFinal));
						//printar o id da task criada
						writer.print(json.toString()+"\n");	
		
					}

		       catch (Exception e) { 
		          e.printStackTrace();        
		      }
	}

			
		else if(splits.length == 5) {
			
			//O seguinte codigo obtem JSON do request para String e assim chamamos a função para inserir na BD
			StringBuffer jb = new StringBuffer();
		      String line = null;
		      try {
		        BufferedReader reader = req.getReader();
		        while ((line = reader.readLine()) != null)
		          jb.append(line);
		        JSONObject jsonObject = new JSONObject(jb.toString());
		        String statusTask = jsonObject.getString("statusTask");
		        
		        //Aqui é feita a alteraçao do status da task
		        controller.changeTaskStatus(splits[3], splits[4], statusTask);
		        
		        //Response
		        JSONObject json = new JSONObject();
		        json.accumulate("id", splits[4]);
		        json.accumulate("status", statusTask);
		    
		        writer.print(json.toString()+"\n");	
		        
		      } catch (Exception e) { 
		          e.printStackTrace();        
		      }
		}
			  
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String[] splits = req.getRequestURI().split("/");
		
		if(splits.length == 4 && !splits[3].equals("*")) {
			
		//O seguinte codigo obtem JSON do request para String
		StringBuffer jb = new StringBuffer();
	      String line = null;
	      try {
	        BufferedReader reader = req.getReader();
	        while ((line = reader.readLine()) != null)
	          jb.append(line);
	        JSONObject jsonObject = new JSONObject(jb.toString());
	        String newName = jsonObject.getString("name");
	        
	        String oldName = controller.readTaskList(splits[3]).getName();
	
	        //Atualização do nome da TaskList
	        controller.open();
			controller.changeTaskListName(oldName, newName);
	      } catch (Exception e) { 
	          e.printStackTrace();        
	      }
		}
		
		else if(splits.length == 5){
			
			StringBuffer jb = new StringBuffer();
		      String line = null;
		      try {
		        BufferedReader reader = req.getReader();
		        while ((line = reader.readLine()) != null)
		          jb.append(line);
		        JSONObject jsonObject = new JSONObject(jb.toString());
		        String newDesc= jsonObject.getString("descriptionTask");
		       
		      //Atualização da descrição da TaskList
		       controller.changeTaskDescription(splits[3],splits[4],newDesc);
		       
				
		      } catch (Exception e) { 
		          e.printStackTrace();        
		      }
			}
		
		}
		
	}
	


