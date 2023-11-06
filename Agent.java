/*    Date         Author                    Project 2         Description                                     */
/* ------------------------------------------------------------------------------------------------------------*/
/*    10/22/2022   Tina Tran(txt200023)      Agent             Agent tasks are run and protected               */

import java.util.concurrent.Semaphore;

public class Agent extends Thread {
  private final int agentID;
  private Customer serveCustomer;
  public Semaphore coordAgent; //individual agent coordinator semaphore (for each)
  
  /* Constructor for Agent and set Semaphore */
  public Agent(int agentID) {
    this.agentID = agentID;
    coordAgent = new Semaphore(0);
    try {
      DMV.printSem.acquire();
      System.out.println("Agent " + agentID + " created");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Getter for the Agent ID */
  public int getAgentID() {
    return agentID;
  }

  /* Boolean for next customer in line, check if agent queue has a customer */
  public boolean nextInLine() {
    try {
      DMV.mutexAgent.acquire();
      if(DMV.qAgent.size() > 0){
        //dequeue customer from agent line
        serveCustomer = DMV.qAgent.poll();
        DMV.mutexAgent.release();

        //print agent serving customer
        DMV.printSem.acquire();
        System.out.println("Agent " + agentID + " is serving customer " + serveCustomer.getCustomerID());
        DMV.printSem.release();
        serveCustomer.setAgentID(this);

        return true;
      }
      DMV.mutexAgent.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
    return false;
  }

  /* Ask customer to take photo and eye exam */
  public void asksCustomer(){
    try {
      DMV.printSem.acquire();
      System.out.println("Agent " + agentID + " asks customer " + serveCustomer.getCustomerID() + " to take photo and eye exam");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Provides customer with temporary license */
  public void giveLicense(){
    try {
      DMV.printSem.acquire();
      System.out.println("Agent " + agentID + " gives license to customer "+ serveCustomer.getCustomerID());
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Start Agent Simulation */
  @Override
  public void run() {
    try {
      //while current thread is not interrupted
      while(!Thread.currentThread().interrupted()) {
        if(nextInLine()) { //if the agent line has customers then start serving the customers
          serveCustomer.coordCustomer.release(); //release customer to be served by an available agent
          coordAgent.acquire(); //agent waits for customer to arrive at agent desk (print)
          asksCustomer(); //print statement for asking customer to take the exams
          serveCustomer.coordCustomer.release(); //release customer to take exams
          coordAgent.acquire(); //agent waits for customer to complete eye exam and photo (print)
          giveLicense(); //print statement for agent giving customer the license
          serveCustomer.coordCustomer.release(); //release customer to take their license
          coordAgent.acquire(); //waits for customer to finish receiving their license and depart (print)
        }
      }
    } catch (InterruptedException ex) {
      //System.out.println("Agent " + agentID + " interrupted");
      //ex.printStackTrace();
    }
  }
}