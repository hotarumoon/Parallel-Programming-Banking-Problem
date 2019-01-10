
class Employee
{
    private int startTime;
    private int endTime;
    private int employeeID;
    private Customer currentCustomer;
    private int totalFreeTime;
    private int totalBusyTime;
    private int totalCustomers;

    Employee()
    {
        this(1);
    }

    Employee(int employeeId)
    {
        employeeID = employeeId;
    }

    int getEmployeeID()
    {
        return employeeID;
    }

    Customer getCustomer()
    {
        return currentCustomer;
    }

    int getEndBusyIntervalTime()
    {
        return endTime;
    }


    void freeToBusy (Customer currentCustomer, int currentTime)
    {
        totalFreeTime += (currentTime - startTime);
        startTime = currentTime;
        endTime = startTime + currentCustomer.getTransactionTime();
        this.currentCustomer = currentCustomer;
        totalCustomers++;
    }

    Customer busyToFree ()
    {
        totalBusyTime += (endTime - startTime);
        startTime = endTime;
        return currentCustomer;
    }

    // intervalType: 0 for FREE interval, 1 for BUSY interval
    void setEndIntervalTime (int endsimulationtime, int intervalType)
    {
        endTime = endsimulationtime;

        if (intervalType == 0) {
            totalFreeTime += endTime - startTime;
        } else {
            totalBusyTime += endTime - startTime;
        }
    }

   
    void printStatistics ()
    {
        System.out.println("\tCalisan ID                : "+employeeID);
        System.out.println("\tToplam bos zaman          : "+totalFreeTime);
        System.out.println("\tToplam dolu zaman         : "+totalBusyTime);
        System.out.println("\tToplam #musteri           : "+totalCustomers);

        if (totalCustomers > 0) {
            System.out.format("\tOrtalama transaction suresi : %.2f\n",
                    (totalBusyTime*1.0)/totalCustomers);
        }
        System.out.println();
    }

    @Override
    public String toString()
    {
        return "Calisan:"+employeeID+":"+startTime+"-"+endTime+":Musteri:"+currentCustomer;
    }


}
