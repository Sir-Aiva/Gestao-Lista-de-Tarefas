package org.ual.aas.tasklists.controllers;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.ual.aas.tasklists.models.Task;
import org.ual.aas.tasklists.models.TaskList;

public class TaskListController {
	private SessionFactory sessionFactory;
	
	public TaskListController() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure("resource/hibernate.cfg.xml")
				.build();
        try {
        	this.sessionFactory  = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
	}
	
	public List<TaskList> readTaskLists() {
		// TODO
		Session session = sessionFactory.openSession();
		// Open Hibernate connection
		session.beginTransaction();
		// Build all TaskList objects
		List<TaskList> ltl = session.createCriteria(TaskList.class).list();
		// Close connection
		session.close();
		return ltl;
	}
	
	public TaskList readTaskList(String taskListId) {
		// TODO
		Session session = sessionFactory.openSession();
		// Open Hibernate connection
		session.beginTransaction();
		// Build TaskList object
		TaskList tList = session.get(TaskList.class, Integer.parseInt(taskListId));
		// Close connection	
		session.close();
		
		return tList;
	}
	
	public void writeTaskList(TaskList taskList) {
		// TODO: change object id
        Session session = sessionFactory.openSession();
		// Open Hibernate session
        session.beginTransaction();
        // Save TaskList object
        session.save(taskList);
        
        session.getTransaction().commit();
        // Close session
        session.close();
	}
	
	public void close() {
		sessionFactory.close();
	}
	
	public void open() {
		 sessionFactory.openSession();	
	}
	
	public List<TaskList> getTaskLists() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasTasksLists() {
        Session session = sessionFactory.openSession();
        if(readTaskLists().size() == 0) {
            return false;
        }
        return true;
	}

	public boolean hasTaskList(String name) {
		for(TaskList taskList: readTaskLists()){
			if(taskList.getName().equals(name)) {
			return true;
			}
		}
		return false;
	}

	public String createTaskList(String name) {
		TaskList taskList = new TaskList(name);
		this.writeTaskList(taskList);
		return ""+taskList.getId();
	}

	public TaskList getTaskList(String name) {
		for (TaskList taskList : readTaskLists()) {
			if(taskList.getName().equals(name)) {
				return taskList;
			}
		}		
		return null;
	}
	
	public void changeTaskListName(String name, String newName) {//--------------------CORRETO
        Session session = sessionFactory.openSession();
		TaskList taskList = getTaskList(name);
		session.beginTransaction();
		taskList.setName(newName);
        session.update(taskList);
        session.getTransaction().commit();
        session.close();
		// TODO Auto-generated method stub
	}
	
	
	public void deleteTask(String taskListId, String taskId ) {//--------------------CORRETO
        Session session = sessionFactory.openSession();
        //int tLId = Integer.parseInt(taskListId);
        //int tId = Integer.parseInt(taskId);
        TaskList taskList = readTaskList(taskListId);
        int index = Integer.parseInt(taskId);
        for(int i=0; i<taskList.getTasks().size();  i++) {
        	if(taskList.getTasks().get(i).getId()==Integer.parseInt(taskId)) {
        		index = i;
        		session.beginTransaction();
                //session.delete(taskList.getTasks().get(index)); 
                taskList.getTasks().remove(i);
                session.update(taskList);
                session.getTransaction().commit();
                close();
        	}
        	else {
        		System.out.println("ERRO");
        	}  	
        }

        close();
        // TODO Auto-generated method stub
    }
	
	public boolean hasTask(String taskListId, String taskId) {
        int tLId = Integer.parseInt(taskListId);
        int tId = Integer.parseInt(taskId);
        for(Task task : readTaskLists().get(tLId).getTasks()) {
            if(task.getId()==tId) {
                return true;
            }
        }
        return false;
    }

	public void changeTaskDescription(String TaskListId, String taskId, String taskDescription) {
        Session session = sessionFactory.openSession();
        Task task = getTask(TaskListId, taskId);  
        session.beginTransaction(); 
        task.setDescription(taskDescription);
        session.update(task);
        session.getTransaction().commit();
        //close();
        // TODO Auto-generated method stub
    }

	public void changeTaskStatus(String taskListId, String taskId, String status) {//--------------CORRETO
        Session session = sessionFactory.openSession();
        Task task = getTask(taskListId, taskId);
        session.beginTransaction();
        task.setStatus(status);
        session.update(task);
        session.getTransaction().commit();
        close();
		// TODO Auto-generated method stub

	}

	public String createTask(String taskListId, String description, String status) {
        Session session = sessionFactory.openSession();
        //int tLId = Integer.parseInt(taskListId);
        Task task = new Task(description,status);
		TaskList taskList = readTaskList(taskListId);
		
		session.beginTransaction();
		taskList.getTasks().add(task);
		session.update(taskList);
		session.getTransaction().commit();
 
        return ""+task.getId();
    }
	
	
	public Task getTask(String taskListId, String taskId) {
        //int tLId = Integer.parseInt(taskListId);
        //int tId = Integer.parseInt(taskId);
        for (Task task : readTaskList(taskListId).getTasks()) {
            if(task.getId()==(Integer.parseInt(taskId))) {
            	System.out.println("aqui: "+task.getDescription());
                return task;
            }
        }
        System.out.println("nao deu: ");
        return null;
    }

	public void deleteTaskList(String Id) {
        Session session = sessionFactory.openSession();
        //int tLId = Integer.parseInt(Id);
        TaskList taskList = readTaskList(Id);
        System.out.println("tasklist: "+Id);
        System.out.println("tasklist: "+taskList.getId());
        session.beginTransaction();
        session.delete(taskList);
        //session.update(taskList);
        session.getTransaction().commit();
        close();
        // TODO Auto-generated method stub

    }

}
