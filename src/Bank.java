
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;


class CompareEmployee implements Comparator<Employee>
{
    @Override
    public int compare(Employee o1, Employee o2)
    {
        return o1.getEndBusyIntervalTime() - o2.getEndBusyIntervalTime();
    }
}

class Bank
{
    
    private PriorityQueue<Employee> busyEmployeeQ;
    private List<Customer> customerQ; 
    private Queue<Employee> freeEmployeeQ;
    private int tookPreviousVipCustomerInstead = 0;
    private int customerQLimit;
    private Object lock= new Object();
    
    public Bank()
    {
        this(1,1,1);
    }

    public Bank(int numEmployees, int customerQlimit, int startEmployeeID)
    {
       
        customerQ = new ArrayList<Customer>(customerQlimit);
        //FIFO queue
        freeEmployeeQ = new ArrayDeque<Employee>(numEmployees);
        busyEmployeeQ = new PriorityQueue<Employee>( numEmployees, new CompareEmployee());
        customerQLimit = customerQlimit;

        // Employee objeleri yaratilip FreeEmployeeQ ya koyuluyor
        for (int i = 0; i < numEmployees; i++) {
            insertFreeEmployeeQ( new Employee(startEmployeeID++) );
        }
    }

    public Employee removeFreeEmployeeQ()
    {
        return freeEmployeeQ.poll();
    }

    public Employee removeBusyEmployeeQ()
    {
        return busyEmployeeQ.poll();
    }

    public Customer decideNextCustomerToBeServed() throws InterruptedException 
    {
    	
    	synchronized (customerQ) {
	        // Look for the current customer if it is a vip poll her
	    	Customer c0 = customerQ.get(0);
	    	if (c0.getIfpriorityCustomer()){
	    		System.out.println("\tSu anki musteri oncelikli bir musteri.");
	    		customerQ.remove(0);	    		
	    		return c0;
	    	}else if (customerQ.size()==1){	
	    		System.out.println("\tSu anki musteri kuyruktaki tek musteri.");
	    		customerQ.remove(0);
	    		return c0;
	    	}else{
	    		//If not vip look for the previous
	    		Customer c1 = customerQ.get(1);
	    		if (c1.getIfpriorityCustomer() && tookPreviousVipCustomerInstead<2){
	    			//remove this from list and return it
	    			tookPreviousVipCustomerInstead ++;
	    			System.out.println("\tSu anki musteri oncelikli degil. Bir sonraki musteri oncelikli oldugu icin ona hizmet verilecek.");
	    			c1 = customerQ.remove(1);
	    			return c1;
	    		}else if (customerQ.size()>2 && customerQ.get(2).getIfpriorityCustomer() && tookPreviousVipCustomerInstead<2){
	    			//If not vip look for the second previous
		    		Customer c2 = customerQ.get(2);
	    			//remove this from list and return it
	    			tookPreviousVipCustomerInstead++;
	    			System.out.println("\tSu anki ve bir sonraki musteriler oncelikli degil. Bir sonraki musteri oncelikli oldugu icin ona hizmet verilecek.");
	    			c2 = customerQ.remove(2);
	    			return c2;	    		
	    		}else if(tookPreviousVipCustomerInstead == 2){
	    			System.out.println("\tBu musteriden once 2 oncelikli musteri one gecirildi. Simdi bu musteriye hizmet verilecek...");
	    			customerQ.remove(0);
	    			tookPreviousVipCustomerInstead=0;
	    			return c0;
	    		}else{
	    			System.out.println("\tBu musteri ve sonraki 2 musteri oncelikli degil, bu musteriye hizmet verilecek...");
	    			customerQ.remove(0);
	    			tookPreviousVipCustomerInstead=0;
	    			return c0;
	    		}
	    	
	    	}
	    	
	    	
    	}
    }
    
    public void insertFreeEmployeeQ(Employee Employee)
    {
        freeEmployeeQ.add(Employee);
    }

    public void insertBusyEmployeeQ(Employee Employee)
    {
        busyEmployeeQ.add(Employee);
    }

    public void insertCustomerQ(Customer customer) 
    {
    	synchronized (customerQ) {
    		customerQ.add(customer);
    	}
    }


    public boolean emptyFreeEmployeeQ()
    {
        return freeEmployeeQ.isEmpty();
    }

    public boolean emptyBusyEmployeeQ()
    {
        return busyEmployeeQ.isEmpty();
    }

    public boolean emptyCustomerQ()
    {
        return customerQ.isEmpty();
    }

    public int numFreeEmployees()
    {
        return freeEmployeeQ.size();
    }

    public int numBusyEmployees()
    {
        return busyEmployeeQ.size();
    }

    public int numWaitingCustomers()
    {
        return customerQ.size();
    }

    public Employee getFrontBusyEmployeeQ()
    {
        return busyEmployeeQ.peek();
    }

    public boolean isCustomerQTooLong()
    {
        return customerQ.size() == customerQLimit;
    }

    public void printStatistics()
    {
        System.out.println("\t# Bekleyen musteriler  : "+numWaitingCustomers());
        System.out.println("\t# Mesgul calisanlar    : "+numBusyEmployees());
        System.out.println("\t# Bos calisanlar       : "+numFreeEmployees());
    }


}
