/*    Date         Author                    Project 2         Description                                     */
/* ------------------------------------------------------------------------------------------------------------*/
/*    10/22/2022   Tina Tran(txt200023)      Announcer         Announcer tasks are run and protected           */

import java.util.concurrent.Semaphore;

public class Announcer extends Thread {
  private Customer callCustomer;
  
  /* Constructor for Announcer */
  public Announcer() {
    try {
      DMV.printSem.acquire();
      System.out.println("Announcer created");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Announcer tries to keep agent line filled with 4 people, and checks waiting room for customers */
  public boolean moveCustomerFromWaiting() {
    if(DMV.qAgent.size() < 4) {
      try {
        DMV.mutexAnnouncer.acquire();
        if(DMV.qWaitingRoom.size() > 0) {
          //dequeue customer from waiting room
          callCustomer = DMV.qWaitingRoom.poll();
          DMV.mutexAnnouncer.release();

          return true;
        }
        DMV.mutexAnnouncer.release();
        return false;
      } catch (InterruptedException ex) {
        ex.printStackTrace();
      }
    }
    return false;
  }

  /* Announcer calls the next number */
  public void callNextNumber() {
    try {
      //print announcement
      DMV.printSem.acquire();
      System.out.println("Announcer calls number " + callCustomer.getNumberID());
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Announcer moves the customer to the Agent queue */
  public void moveCustomerToAgentLine() {
    try {
      //enqueue customer to agent line
      DMV.mutexAgent.acquire();
      DMV.qAgent.offer(callCustomer);
      DMV.mutexAgent.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Start Announcer Simulation */
  @Override
  public void run() {
    try {
      //while current thread is not interrupted
      while(!Thread.currentThread().interrupted()) {
        if(moveCustomerFromWaiting()) { //if the current agent line is at most 4 and waiting room has customer(s) then proceed
          DMV.coordAnnouncer.acquire(); //waiting for customer to finish entering waiting room (print)
          callNextNumber(); //print statement for calling the next customer
          callCustomer.coordCustomer.release(); //releases customer to move to agent line
          DMV.coordAnnouncer.acquire(); //waiting for customer to finish moving to the agent line (print)
          moveCustomerToAgentLine(); //adds customer to agent line
          callCustomer.coordCustomer.release(); //releases customer after interaction is complete
        }
      }
    } catch (InterruptedException ex) {
      //System.out.println("Announcer interrupted");
      //ex.printStackTrace();
    }
  }
}