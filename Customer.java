/*    Date         Author                    Project 2         Description                                     */
/* ------------------------------------------------------------------------------------------------------------*/
/*    10/22/2022   Tina Tran(txt200023)      Customer          Customer tasks are run and protected            */

import java.util.concurrent.Semaphore;

public class Customer extends Thread {
  private final int customerID;
  private int numberID;
  private Agent agentID;
  public Semaphore coordCustomer; //individual customer coordinator semaphore (for each)
  
  /* Constructor for Customer and set Semaphore */
  public Customer (int customerID){
    this.customerID = customerID;
    coordCustomer = new Semaphore(0);
  }

  /* Setter for number ID and agent ID */
  public void setNumberID(int num) {
    this.numberID = num;
  }
  public void setAgentID(Agent agent) {
    this.agentID = agent;
  }
  
  /* Getters for customer id and number id */
  public int getCustomerID() {
    return customerID;
  }
  public int getNumberID() {
    return numberID;
  }
  
  /* Customer enters the DMV */
  private void entersDMV() {
    try{
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " created, enters DMV");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Customer waits in line at the information desk to get a number qInfoDesk at line: 106*/

  /* Customer waits in waiting area until number is called */
  public void entersWaitingRoom() {
    try {
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " gets number " + numberID + ", enters waiting room");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }


  /* Customer waits in line for agent */
  public void moveToAgent() {
    try {
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " moves to agent line");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Customer gets served by agent */
  public void getsServed(){
    try {
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " is being served by agent " + agentID.getAgentID());
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }
  
  /* Customer works with agent to complete driver's license application and exits */
  public void completesExams(){
    try {
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " completes photo and eye exam for agent " + agentID.getAgentID());
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }
  public void getLicense(){
    try {
      DMV.printSem.acquire();
      System.out.println("Customer " + customerID + " gets license and departs");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Start Customer Simulation */  
  @Override
  public void run() {
    try {
      entersDMV(); //customer is created and enters the DMV

      /* Information Desk interacts with customer */
      DMV.mutexInfoDesk.acquire(); //customer arrives at info desk line
      DMV.qInfoDesk.offer(this); //customer puts itself in info desk line
      DMV.mutexInfoDesk.release(); //relinquish control of info desk queue
      coordCustomer.acquire(); //customer waits for info desk to finish assigning number (print)
      entersWaitingRoom(); //print customer enters waiting room
      DMV.coordInfoDesk.release(); //customer lets info desk know that they have finished receiving the number

      /* Announcer interacts with customer */
      DMV.coordAnnouncer.release(); //customer is ready release announcer to call next number
      coordCustomer.acquire(); //customer waits for announcer to call number (print)
      moveToAgent(); //print customer moving to agent line
      DMV.coordAnnouncer.release(); //release announcer to move the customer to agent queue
      coordCustomer.acquire(); //waiting for the announcer to end interaction

      /* Agent interacts with customer */
      coordCustomer.acquire(); //customer waits for agent to serve them
      getsServed(); //print statement for customer being served by the agent
      agentID.coordAgent.release(); //releases customer to agent to start exams
      coordCustomer.acquire(); //customer waits for agent to ask about eye exam and photo (print)
      completesExams(); //print statement for customer completing the exams
      agentID.coordAgent.release(); //release customer to agent to pick up license
      coordCustomer.acquire(); //customer waits for agent finish giving temporary license (print)
      getLicense(); //print statement for customer receiving license and departing
      agentID.coordAgent.release(); //release agent to serve next customer
    } catch (InterruptedException ex) {
      System.out.println("Customer ERROR - interrupted");
      ex.printStackTrace();
    }
  }
}


