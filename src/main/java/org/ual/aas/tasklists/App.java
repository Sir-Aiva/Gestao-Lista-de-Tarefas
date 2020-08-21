package org.ual.aas.tasklists;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.ual.aas.tasklists.models.Task;
import org.ual.aas.tasklists.models.TaskList;

public class App  {
	public static void printTaskList(TaskList taskList) {
		System.out.println(taskList.getName());
		for(Task task : taskList.getTasks()) {
			System.out.println(task.getDescription());
		}
	}
	
    public static void main( String[] args ) {
        TaskList taskList = new TaskList("Sample Task List");
        taskList.getTasks().add(new Task("1st task", "doing"));
        taskList.getTasks().add(new Task("2nd task", "doing"));
        taskList.getTasks().add(new Task("3rd task", "doing"));
        
        printTaskList(taskList);
        
        
        SessionFactory sessionFactory = null;
    	final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure("resource/hibernate.cfg.xml")
				.build();
        try {
        	sessionFactory  = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
        
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(taskList);
        session.getTransaction().commit();
        session.close();
        
        session = sessionFactory.openSession();
		// Open Hibernate connection
		session.beginTransaction();
		// Build TaskList object
		System.out.println("estou aqui: "+session.get(TaskList.class, 1).getName());
		// Close connection	
		session.close();
		
		session = sessionFactory.openSession();
		// Open Hibernate connection
		session.beginTransaction();
		// Build all TaskList objects
		List<TaskList> ltl = (List<TaskList>)session.createCriteria(TaskList.class).list();
		System.out.println(ltl.size());
		for (int i=0; i<ltl.size(); i++) {
		System.out.println("estou aqui 2haha: "+ltl.get(i).getName());
		}
		// Close connection
		session.close();
		
		


        session = sessionFactory.openSession();
		session.beginTransaction();
		
		List resultSet = session.createQuery("from TaskList").list();
		for(TaskList tl : (List<TaskList>)resultSet) {
			printTaskList(tl);
		}

		
		session.getTransaction().commit();
		session.close();      
        sessionFactory.close();
        
        
    }
}
