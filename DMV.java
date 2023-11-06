/*    Date         Author                    Project 2         Description                                     */
/* ------------------------------------------------------------------------------------------------------------*/
/*    10/22/2022   Tina Tran(txt200023)      DMV               DMV creates threads and global variables and    */
/*                                                             runs the entire simulation                      */

import java.util.*;
import java.util.concurrent.Semaphore;

public class DMV {
  /* Global Variables */
  //max customers and agents
  public static final int totalCustomers = 20;
  public static final int totalAgents = 2;

  /* Semaphores */
  //create all static semaphores
  public static Semaphore mutexInfoDesk = new Semaphore(1);
  public static Semaphore coordInfoDesk = new Semaphore(0);
  public static Semaphore mutexAnnouncer = new Semaphore(1);
  public static Semaphore coordAnnouncer = new Semaphore(0);
  public static Semaphore mutexAgent = new Semaphore(1);
  public static Semaphore printSem = new Semaphore(1);

  /* Queues */
  //create queues for information desk, announcer, and agents
  public static Queue<Customer> qInfoDesk = new LinkedList<>();
  public static Queue<Customer> qWaitingRoom = new LinkedList<>();
  public static Queue<Customer> qAgent = new LinkedList<>();

  /* Thread Arrays*/
  //create thread arrays for customer and agent
  public static Thread[] customer = new Thread[totalCustomers];
  public static Thread[] agent = new Thread[totalAgents];

  /* Start Entire Simulation (create all threads and join all customers) */
  public static void startSimulation() {
      /* Create all the threads */
      //create thread for the information desk
      Thread infoDesk = new Thread(new InfoDesk());
      //create thread for the announcer
      Thread announcer = new Thread(new Announcer());
      //create thread for each agent
      for (int i = 0; i < totalAgents; i++) {
        agent[i] = new Thread(new Agent(i));
      }
      //create thread for each customer
      for (int i = 0; i < totalCustomers; i++) {
          customer[i] = new Thread(new Customer(i));
      }

      /* Run the simulation */
      infoDesk.start();
      announcer.start();
      for (int i = 0; i < totalAgents; i++) {
        agent[i].start();
      }
      for (int i = 0; i < totalCustomers; i++) {
        customer[i].start();
      }

      /* Join all customers */
      for (int i = 0; i < totalCustomers; i++) {
          try {
              customer[i].join();
              printSem.acquire();
              System.out.println("Customer " + i + " was joined");
              printSem.release();
          } catch (InterruptedException ex) {
              ex.printStackTrace();
          }
      }
      
      /* When customer exits, end the simulation */
      //kill all threads
      infoDesk.interrupt();
      announcer.interrupt();
      for (int i = 0; i < totalAgents; i++) {
        agent[i].interrupt();
      }
      //exit simulation
      System.out.println("Done");
      System.exit(0);
  }

}