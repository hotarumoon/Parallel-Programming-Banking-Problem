import java.util.Random;
import java.util.Scanner;

class App {

	// input parameters
	private int numEmployees;
	private int customerQLimit;
	private int simulationTime;
	private int chancesOfArrival, maxTransactionTime;
	public int currentTime = 0;
	// statistical data
	private int numGoaway, numServed, totalWaitingTime;

	// internal data
	private int customerIDCounter;
	private Random dataRandom; // get customer data using random function

	// most recent customer arrival info, see getCustomerData()
	private boolean anyNewArrival;
	private int transactionTime;

	// initialize data fields
	private App() {
		numGoaway = 0;
		numServed = 0;
		totalWaitingTime = 0;
		customerIDCounter = 0;
	}

	private void setupParameters() {
		// read input parameters

		Scanner input = new Scanner(System.in);
		System.out.println("\n\t=== Simulasyon Parameterelerini giriniz ===\n");

		do {
			System.out.print("Simulasyon suresi giriniz (max 10000): ");
			simulationTime = input.nextInt();
		} while (simulationTime > 10000 || simulationTime < 0);
		do {
			System.out.print("Musterinin maximum transaction suresini giriniz (max is 500): ");
			maxTransactionTime = input.nextInt();
		} while (maxTransactionTime > 500 || maxTransactionTime < 0);
		do {
			System.out.print("Bankaya yeni musteri gelme olasiligini giriniz (0% < & <= 100%)  ");
			chancesOfArrival = input.nextInt();
		} while (chancesOfArrival > 100 || chancesOfArrival <= 0);
		do {
			System.out.print("Musteri temsilcisi sayisini giriniz (max 10): ");
			numEmployees = input.nextInt();
		} while (numEmployees > 10 || numEmployees < 0);
		do {
			System.out.print("Maksimum bekleyebilecek musteri sayisini giriniz (max 50): ");
			customerQLimit = input.nextInt();
		} while (customerQLimit > 50 || customerQLimit < 0);

		System.out.println("Random data olusturuluyor.");

		input.close();

		dataRandom = new Random();
	}

	private void getCustomerData() {

		anyNewArrival = ((dataRandom.nextInt(100) + 1) <= chancesOfArrival);
		transactionTime = dataRandom.nextInt(maxTransactionTime) + 1;

	}

	private void printStatistics(Bank bank)
    {
        // print out simulation results

        System.out.println("\n===============================================================\n");
        System.out.println("\t=== Simulasyon raporu ===\n\n");
        System.out.println("\t# Toplam gelen musteri sayisi                : " + customerIDCounter);
        System.out.println("\t# Islem yapamadan ayrilan musteri sayisi     : " + numGoaway);
        System.out.println("\t# Islem yapabilen musteri sayisi             : " + numServed);


        System.out.println("\n\n---------------------------------------------------------------");
        System.out.println("\t=== Banka calisani bilgileri. ===\n");
        bank.printStatistics();

        System.out.println("\n\n\tToplam bekleme suresi   : " + totalWaitingTime);
        double averageWaitingTime = ( bank.emptyCustomerQ() )
                ? 0.0 : (double)totalWaitingTime / bank.numWaitingCustomers();
        System.out.printf("\tOrtalama bekleme suresi : %.2f\n", averageWaitingTime);
        System.out.println("\n\n---------------------------------------------------------------");
        System.out.println("\t=== Mesgul banka calisani bilgileri. ===\n");
        if (!bank.emptyBusyEmployeeQ()) {
            while (bank.numBusyEmployees() > 0) {
                Employee Employee = bank.removeBusyEmployeeQ();
                Employee.setEndIntervalTime(simulationTime, 1);
                Employee.printStatistics();
            }
        } else {
            System.out.println("\t\tMesgul banka calisani yok.\n");
        }
        System.out.println("\n\n---------------------------------------------------------------");
        System.out.println("\t=== Bos banka calisani bilgileri. ===\n");
        if (!bank.emptyFreeEmployeeQ()) {
            while (bank.numFreeEmployees() > 0) {
                Employee Employee = bank.removeFreeEmployeeQ();
                Employee.setEndIntervalTime(simulationTime, 0);
                Employee.printStatistics();
            }
        } else {
            System.out.println("\t\tBos banka calisani yok.\n");
        }
        System.out.println();
    }

	
	public static void main(String[] args) throws InterruptedException {
		
		App simulation = new App();
		simulation.setupParameters();
		
		System.out.println("\n\t=== Simulasyon Basliyor ===\n");
		Bank bank = new Bank(simulation.numEmployees, simulation.customerQLimit, 1);

		//simulation loop
        for (int currentTime = 0; currentTime < simulation.simulationTime; currentTime++) {

            System.out.println("---------------------------------------------------------------");
            System.out.println("Su an zaman  : " + (currentTime+1));
            System.out.println("Kuyruk : " + bank.numWaitingCustomers() + "/" + simulation.customerQLimit);
            simulation.totalWaitingTime = (bank.numWaitingCustomers() > 0) ? simulation.totalWaitingTime+1 : 0;
            simulation.currentTime = currentTime;
            // Step 1: bankaya giren yeni bir musteri var mi?
            simulation.getCustomerData();
            
            if (simulation.anyNewArrival) {

                // Step 1.1: setup musteri
            	simulation.customerIDCounter++;
                Random random= new Random();
                System.out.println("\tMusteri #" + simulation.customerIDCounter + " " + simulation.transactionTime + " sure kalmak uzere bankaya geldi. ");

                // Step 1.2: Musteri sirasi dolu mu kontrol et dolu ise yeni gelen musteri ayrilir
                if (bank.isCustomerQTooLong()) {
                    System.out.println("\tMusteri kuyrugu dolu. Musteri #" + simulation.customerIDCounter + " ayrildi...");
                    simulation.numGoaway++;
                } else {
                	//Yer varsa yeni musterinin bilgilerini random ata
                    System.out.println("\tMusteri #" + simulation.customerIDCounter + " kuyrukta bekliyor.");
                    Thread t1 = new Thread(new Runnable(){
            			public void run() {
            				
            				try {
            					bank.insertCustomerQ( new Customer(simulation.customerIDCounter, simulation.transactionTime,
										simulation.currentTime,random.nextBoolean()));
								Thread.sleep(simulation.transactionTime);
							} catch (InterruptedException e) {								
								e.printStackTrace();
							}
            			}
            			
            		});
                    t1.start();
                    
                    t1.join();
                    }

            } else {
                System.out.println("\tYeni musteri gelmedi!");
            }
            
            // Step 2: Busy Employee leri serbest birak ve free EmployeeQ ya ekle
            while (bank.numBusyEmployees() > 0 && bank.getFrontBusyEmployeeQ().getEndBusyIntervalTime() == currentTime) {
                Employee Employee = bank.removeBusyEmployeeQ();
                Employee.busyToFree();
                bank.insertFreeEmployeeQ(Employee);

                System.out.println("\tMusteri #" + Employee.getCustomer().getCustomerID() + " islemini bitirdi.");
                System.out.println("\tCalisan #" + Employee.getEmployeeID() + " artik bos.");
            }

            // Step 3: bos olan employee lerin sirada bekleyen musterilere hizmet etmesini sagla 
            Thread t2 = new Thread(new Runnable(){
    			public void run() {
    				while (bank.numFreeEmployees() > 0 && bank.numWaitingCustomers() > 0) {
						 Customer customer;
						try {
							customer = bank.decideNextCustomerToBeServed();
							 Employee Employee = bank.removeFreeEmployeeQ();
						     Employee.freeToBusy(customer, simulation.currentTime);
						     bank.insertBusyEmployeeQ(Employee);
						     simulation.numServed++;

						     System.out.println("\tMusteri #" + customer.getCustomerID() + " #"
						             + Employee.getEmployeeID() + "numarali calisan ile " + customer.getTransactionTime() 
						             + " sure islem yapacak.");
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					 }
    			
    			}
               
            });

    		t2.start();
    		
    		t2.join();	

        }    	
		
        simulation.printStatistics(bank);
	}

}
