/*    Date         Author                    Project 2         Description                                     */
/* ------------------------------------------------------------------------------------------------------------*/
/*    10/22/2022   Tina Tran(txt200023)      InfoDesk          Information Desk tasks are run and protected    */

import java.util.concurrent.Semaphore;

public class InfoDesk extends Thread {
  private static int assignID = 1; //initialize number assigned to customer starting with 1
  public static Customer helpCustomer;

  /* Constructor for Information Desk */
  public InfoDesk() {
    try {
      DMV.printSem.acquire();
      System.out.println("Information desk created");
      DMV.printSem.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* InfoDesk assigns a unique number sequentially starting at 1 to each customer */
  public void assignNumber() {
    helpCustomer.setNumberID(assignID++); //increment the number for to give a new number for each customer
    try {
      //enqueue customer to waiting room
      DMV.mutexAnnouncer.acquire();
      DMV.qWaitingRoom.offer(helpCustomer);
      DMV.mutexAnnouncer.release();
    } catch (InterruptedException ex) {
      ex.printStackTrace();
    }
  }

  /* Start InfoDesk Simulation */
  @Override
  public void run() {
    try {
      //while current thread is not interrupted
      while(!Thread.currentThread().interrupted()) {
        DMV.mutexInfoDesk.acquire(); //waiting for customer to arrive
        if (DMV.qInfoDesk.size() > 0) { //check if info desk line has customers
          helpCustomer = DMV.qInfoDesk.poll(); //dequeue customers from info desk line
          DMV.mutexInfoDesk.release(); //relinquish access to info desk queue
          assignNumber(); //assign a number to the customer
          helpCustomer.coordCustomer.release(); //release customer from waiting on info desk to finish number assignment
          DMV.coordInfoDesk.acquire(); //waiting for customer to finish receiving number

          continue;
        }
        DMV.mutexInfoDesk.release(); //release customer after arrival is finished
      }
    } catch (InterruptedException ex) {
      //System.out.println("Info desk interrupted");
      //ex.printStackTrace();
    }
  }
}