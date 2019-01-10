
class Customer 
{
    private int customerID;
    private int transactionTime;
    private int arrivalTime;
    private boolean priorityCustomer;
    
    Customer()
    {
        this(1,1,1,false);
    }

    Customer(int customerid, int transactionduration, int arrivaltime, boolean priorityCust)
    {
        customerID = customerid;
        transactionTime = transactionduration;
        arrivalTime = arrivaltime;
        priorityCustomer = priorityCust;
    }

    int getTransactionTime()
    {
        return transactionTime;
    }

    boolean getIfpriorityCustomer()
    {
        return priorityCustomer;
    }

    int getArrivalTime()
    {
        return arrivalTime;
    }

    int getCustomerID()
    {
        return customerID;
    }

    public String toString()
    {
        return ""+customerID+":"+transactionTime+":"+arrivalTime + "vip: "+ priorityCustomer;
    }


}
