import java.text.DecimalFormat;
import java.util.*;

// Color codes for console
class Colors 
{
    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001b[34;1m";
    public static final String PURPLE = "\u001b[35;1m";
    public static final String CYAN = "\u001B[36m";
    public static final String MAGENTA = "\u001B[35m";
    public static final String BLINK = "\u001b[5m";
}

// Base User
abstract class User 
{
    private String name;
    private String phone;

    public User(String name, String phone) 
    {
        this.name = name;
        this.phone = phone;
    }

    public void login() 
    {
        System.out.println(Colors.GREEN + "\nHello " + name + ", you are now logged in." + Colors.RESET);
    }

    public void logout() 
    {
        System.out.println(Colors.RED + "\nGoodbye " + name + ". Logged out successfully." + Colors.RESET);
    }

    public String getName() 
    {	
	 return name; 
    }
    public String getPhone() 
    { 
	return phone; 
    }
}

// Customer
class Customer extends User 
{
    private String address;

    public Customer(String name, String phone) 
    {
        super(name, phone);
    }

    public void setAddress(String addr) 
    {  
	this.address = addr;  
    }
    public String getAddress()  
    {  
	return address; 
    }
}

// Delivery Driver
class DeliveryDriver extends User 
{
    private List<Order> assignedOrders;
    private Order lastDeliveredOrder;

    public DeliveryDriver(String name, String phone) 
    {
        super(name, phone);
        this.assignedOrders = new ArrayList<>();
        this.lastDeliveredOrder = null;
    }

    public void acceptOrder(Order o) 
    {
        o.updateStatus("Delivery on the Way");
        assignedOrders.add(o);
    }

    public void deliver(Order o) 
    {
        try 
 	{
            Thread.sleep(10000); // 10-second delay
            o.updateStatus("Delivered");
            System.out.println(Colors.GREEN + "Order " + o.summary() + " has been delivered." + Colors.RESET);
            lastDeliveredOrder = o;
            assignedOrders.remove(o);
        }
	catch (InterruptedException e) 
	{
            System.out.println(Colors.RED + "Delivery interrupted." + Colors.RESET);
        }
    }

    public String getDetails() 
    { 
	return getName(); 
    }

    public void viewAssignedOrder() 
    {
        if (assignedOrders.isEmpty()) 
	{
            System.out.println(Colors.YELLOW + "No orders assigned yet." + Colors.RESET);
        } 
	else 
	{
            System.out.println("Assigned Orders:");
            for (Order order : assignedOrders) 
	    {
                System.out.println("- " + order.summary());
            }
        }
    }

    public void viewPreviousOrder() 
    {
        if (lastDeliveredOrder == null) 
	{
            System.out.println(Colors.YELLOW + "No previous orders delivered." + Colors.RESET);
        } 
	else 
	{
            System.out.println("Previous Order: " + lastDeliveredOrder.summary());
        }
    }

    public boolean isAvailable() 
    { 
	return assignedOrders.isEmpty(); 
    }
}

// Delivery Manager to handle multiple drivers
class DeliveryManager 
{
    private List<DeliveryDriver> drivers;
    private Random random;

    public DeliveryManager() 
    {
        this.drivers = new ArrayList<>();
        this.random = new Random();
        // Initialize three delivery drivers
        drivers.add(new DeliveryDriver("Amit Sharma", "9876543210"));
        drivers.add(new DeliveryDriver("Priya Singh", "8765432109"));
        drivers.add(new DeliveryDriver("Rahul Verma", "7654321098"));
    }

    public DeliveryDriver assignDriver(Order order) 
    {
        if (drivers.isEmpty()) 
	{
            return null;
        }
        // Randomly select a driver (even if they have orders)
        DeliveryDriver selectedDriver = drivers.get(random.nextInt(drivers.size()));
        selectedDriver.acceptOrder(order);
        new Thread(() -> selectedDriver.deliver(order)).start();
        return selectedDriver;
    }

    public void registerDriver(String name, String phone) 
    {
        drivers.add(new DeliveryDriver(name, phone));
    }

    public DeliveryDriver loginDriver(String phone, Scanner sc) 
    {
        for (DeliveryDriver driver : drivers) 
	{
            if (driver.getPhone().equals(phone) && OTPService.verifyOTP(phone, sc)) 
	    {
                return driver;
            }
        }
        return null;
    }

    public boolean isPhoneNumberRegistered(String phone) 
    {
        for (DeliveryDriver driver : drivers) 
	{
            if (driver.getPhone().equals(phone)) 
	    {
                return true;
            }
        }
        return false;
    }
}

// Fuel classes
abstract class Fuel 
{
    protected String type;
    protected double pricePerLiter;

    public Fuel(String t, double p) 
    {
        this.type = t;
        this.pricePerLiter = p;
    }

    public abstract double calculateCost(double l);
    public String getType() 
    { 
	return type; 
    }
}

class Petrol95 extends Fuel 
{
    public Petrol95(double p) 
    { 
	super("Petrol95", p); 
    }
    public double calculateCost(double l) 
    { 
	return l * pricePerLiter; 
    }
}

class Diesel extends Fuel 
{
    public Diesel(double p)  
    { 
	super("Diesel", p); 
    }
    public double calculateCost(double l) 
    { 
	return l * pricePerLiter; 
    }
}

// Order class
class Order 
{
    private static DecimalFormat df = new DecimalFormat("RS#,###.00");

    private Customer customer;
    private Fuel fuel;
    private double liters, distance, fuelCost, deliveryCharge, gst, total;
    private String bunkName, status, estTime, deliveryAddr;
    private String orderId;

    public Order(String id, Customer cust, Fuel fuel, double liters, String bunk, double dist, String daddr) 
    {
        this.orderId = id;
        this.customer = cust;
        this.fuel = fuel;
        this.liters = liters;
        this.bunkName = bunk;
        this.distance = dist;
        this.deliveryAddr = daddr;

        this.status = "Pending";
        this.fuelCost = fuel.calculateCost(liters);
        this.deliveryCharge = 30 + (dist * 2);
        this.gst = 0.15 * (fuelCost + deliveryCharge);
        this.total = fuelCost + deliveryCharge + gst;
        this.estTime = (int) (dist * 2 + 10) + " mins";
    }

    public double getTotal() 
    { 
	return total; 
    }
    public void updateStatus(String s) 
    { 
	this.status = s; 
    }

    public void showBill() 
    {
        System.out.println(Colors.YELLOW + "\n========= BILL =========" + Colors.RESET);
        System.out.println("Fuel Type     : " + fuel.getType());
        System.out.println("Liters        : " + liters);
        System.out.println("Bunk          : " + bunkName);
        System.out.println("Delivery Addr : " + deliveryAddr);
        System.out.println("Distance      : " + distance + " km");
        System.out.println("Est. Delivery : " + estTime);
        System.out.println("------------------------");
        System.out.println("Fuel Cost     : " + df.format(fuelCost));
        System.out.println("Delivery Fee  : " + df.format(deliveryCharge));
        System.out.println("GST (15%)     : " + df.format(gst));
        System.out.println("========================");
        System.out.println(Colors.CYAN + "TOTAL         : " + df.format(total) + Colors.RESET);
        System.out.println("Status        : " + status);
        System.out.println("========================\n");
    }

    public String summary() 
    {
        return orderId + " | " + fuel.getType() + " | " + liters + "L | " + deliveryAddr + " | " + status + " | Total: " + df.format(total);
    }
}

// Payment
interface Payment 
{ 
	void pay(double amount); 
}

class PhonePe implements Payment 
{
    private String upiId;
    public PhonePe(String upi) 
    { 
	this.upiId = upi; 
    }
    public void pay(double amt) 
    { 
	System.out.println("Payment of " + amt + " done via PhonePe UPI: " + upiId); 
    }
}

class Paytm implements Payment 
{
    private String upiId;
    public Paytm(String upi) 
    { 
	this.upiId = upi; 
    }
    public void pay(double amt) 
    { 
	System.out.println("Payment of " + amt + " done via Paytm UPI: " + upiId); 
    }
}

class BankTransfer implements Payment 
{
    private String accNo, ifsc;
    public BankTransfer(String acc, String ifsc) 
    { 
	this.accNo = acc; this.ifsc = ifsc; 
    }
    public void pay(double amt) 
    { 
	System.out.println("Payment of " + amt + " done via Bank Transfer (A/C: " + accNo + ", IFSC: " + ifsc + ")"); 
    }
}

class FuelGoWallet implements Payment 
{
    private double balance = 0;
    public void addMoney(double amt) 
    { 
	balance += amt; System.out.println("Wallet balance updated: " + balance); 
    }
    public void pay(double amt) 
    {
        if (balance >= amt) 
        {
		 balance -= amt; System.out.println("Payment of " + amt + " done using FUELgo Wallet. Remaining: " + balance); 
    	}
        else System.out.println(Colors.RED + "Insufficient wallet balance!" + Colors.RESET);
    }
    public double getBalance() 
    { 
	return balance; 
    }
}

// OTP Service with retry and resend
class OTPService 
{
    public static boolean verifyOTP(String phone, Scanner sc) 
    {
        Random rand = new Random();
        int attempts = 0;
        while (attempts < 3) 
	{
            int otp = 1000 + rand.nextInt(9000);
            System.out.println("Sending OTP to " + phone + " : " + Colors.RED+ otp +Colors.RESET );
            System.out.print("Enter OTP (or type 0 to Exit): ");
            String otpInput = sc.nextLine();
            if (otpInput.equals("0")) return false;
            if (otpInput.matches("\\d{1,6}")) 
	    {
                int entered = Integer.parseInt(otpInput);
                if (entered == otp) return true;
            }
            attempts++;
            System.out.println(Colors.RED + "Incorrect OTP. Attempts left: " + (3 - attempts) + Colors.RESET);
            if (attempts < 3) 
	    {
                System.out.println("1. Resend OTP\n2. Exit");
                String choiceInput = sc.nextLine();
                if (!choiceInput.matches("[1-2]")) 
		{
                    System.out.println(Colors.RED + "Invalid choice. Please enter 1 or 2." + Colors.RESET);
                    continue;
                }
                if (choiceInput.equals("2")) return false;
            }
        }
        System.out.println(Colors.RED + "Too many failed OTP attempts. Exiting..." + Colors.RESET);
        return false;
    }
}

// Main App
class FUELgoApp 
{
    private DeliveryManager deliveryManager;
    private Order lastOrder;
    private FuelGoWallet wallet;

    public FUELgoApp() 
    {
        this.deliveryManager = new DeliveryManager();
        this.lastOrder = null;
        this.wallet = new FuelGoWallet();
    }

    public void run() 
    {
        Scanner sc = new Scanner(System.in);
        System.out.println(Colors.GREEN + "\n==============================" + Colors.RESET);
        System.out.println(Colors.YELLOW + "       WELCOME TO FUELGO " + Colors.RESET);
        System.out.println(Colors.GREEN + "\n==============================" + Colors.RESET);
        System.out.println(Colors.MAGENTA + "Your fuel, delivered at your doorstep!" + Colors.RESET);
        System.out.println(Colors.MAGENTA + "Skip the queue, save time, and keep riding with FUELgo." + Colors.RESET);

        boolean appRunning = true;
        while (appRunning) 
 	{
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. Customer Login");
            System.out.println("2. Delivery Person Register/Login");
            System.out.println(Colors.BLINK + "3. Exit..." + Colors.RESET);
            String choiceInput = sc.nextLine();
            if (!choiceInput.matches("[1-3]")) 
	    {
                System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
                continue;
            }
            int choice = Integer.parseInt(choiceInput);

            switch (choice) 
	    {
                case 1:
                    Customer customer = authenticateCustomer(sc);
                    if (customer != null) 
		    {
                        handleCustomerMenu(customer, sc);
                    }
                    break;

                case 2:
                    DeliveryDriver driver = handleDeliveryPersonMenu(sc);
                    if (driver != null) 
    		    {
                        handleDriverMenu(driver, sc);
                    }
                    break;

                case 3:
                    appRunning = false;
                    break;
            }
        }

        System.out.println(Colors.GREEN + "\nThank you for using FUELgo.");
        System.out.println("Stay fueled, stay safe, and see you next time." + Colors.RESET);
        sc.close();
    }

    private Customer authenticateCustomer(Scanner sc) 
    {
        System.out.print("\nEnter Name (or type exit): ");
        String name = sc.nextLine();
        if (name.equalsIgnoreCase("exit")) return null;
        if (name.matches("-?\\d+")) 
        {
            System.out.println(Colors.RED + "Name cannot be an integer." + Colors.RESET);
            return null;
        }
        System.out.print("Enter Phone Number (10 digits): ");
        String phoneInput = sc.nextLine();
        if (!phoneInput.matches("\\d{10}")) 
        {
            System.out.println(Colors.RED + "Invalid phone number." + Colors.RESET);
            return null;
        }
        if (!OTPService.verifyOTP(phoneInput, sc)) return null;
        Customer cust = new Customer(name, phoneInput);
        cust.login();
        return cust;
    }

    private void handleCustomerMenu(Customer cust, Scanner sc) 
    {
        int orderCount = 1;
        boolean running = true;
        while (running) 
	{
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. Place Order");
            System.out.println("2. View Previous Order");
            System.out.println("3. Logout");
            String chInput = sc.nextLine();
            if (!chInput.matches("[1-3]")) 
	    {
                System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
                continue;
            }
            int ch = Integer.parseInt(chInput);

            switch (ch) 
	    {
                case 1:
                    Order order = createOrder(cust, sc, orderCount++);
                    if (order != null) 
		    {
                        	lastOrder = order;
                       		lastOrder.showBill();
                        if (processPayment(order, cust, sc)) 
			{
                            	DeliveryDriver driver = deliveryManager.assignDriver(order);
                            if (driver != null) 
			    {
                                System.out.println("Driver Assigned: " + driver.getDetails() + " | Contact: " + driver.getPhone());
                                order.showBill();
                                try 
				{
                                    while (!order.summary().contains("Delivered")) 
				    {
                                        Thread.sleep(1000);
                                    }
                                    order.showBill();
                                } 
				catch (InterruptedException e) 
				{
                                    System.out.println(Colors.RED + "Error waiting for delivery." + Colors.RESET);
                                }
                            } 
			    else 
			    {
                                System.out.println(Colors.YELLOW + "No available drivers at the moment. Order pending." + Colors.RESET);
                            }
                        }
                    }
                    break;

                case 2:
                    if (lastOrder == null)
                        System.out.println(Colors.YELLOW + "No previous orders." + Colors.RESET);
                    else
                        System.out.println(lastOrder.summary());
                    break;

                case 3:
                    running = false;
                    cust.logout();
                    break;
            }
        }
    }

    private Order createOrder(Customer cust, Scanner sc, int orderCount) 
    {
        System.out.println("\nNearest Petrol Bunks:");
        System.out.println("1. Bharat Petroleum (2.5 km)");
        System.out.println("2. HP Petroleum     (4.2 km)");
        System.out.println("3. Previous Menu");
        String bunkChoiceInput = sc.nextLine();
        if (!bunkChoiceInput.matches("[1-3]")) 
        {
            System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
            return null;
        }
        int bunkChoice = Integer.parseInt(bunkChoiceInput);
        if (bunkChoice == 3) return null;
        String bunk = (bunkChoice == 1) ? "Bharat Petroleum" : "HP Petroleum";
        double dist = (bunkChoice == 1) ? 2.5 : 4.2;

        System.out.println("\nChoose Fuel:");
        System.out.println("1. Petrol95 (110/l)");
        System.out.println("2. Diesel   (95/l)");
        System.out.println("3. Previous Menu");
        String fuelChoiceInput = sc.nextLine();
        if (!fuelChoiceInput.matches("[1-3]")) 
	{
            System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
            return null;
        }
        int fuelChoice = Integer.parseInt(fuelChoiceInput);
        if (fuelChoice == 3) return null;
        Fuel fuel = (fuelChoice == 1) ? new Petrol95(110) : new Diesel(95);

        System.out.print("Enter Liters (or 0 for Previous Menu): ");
        String litersInput = sc.nextLine();
        if (!litersInput.matches("-?\\d*\\.?\\d+")) 
	{
            System.out.println(Colors.RED + "Invalid liters. Please enter a number." + Colors.RESET);
            return null;
        }
        double liters = Double.parseDouble(litersInput);
        if (liters < 0 || liters > 1000) 
	{
            System.out.println(Colors.RED + "Liters must be between 0 and 1000." + Colors.RESET);
            return null;
        }
        if (liters == 0) return null;

        System.out.print("Enter Delivery Address (or type back): ");
        String addr = sc.nextLine();
        if (addr.equalsIgnoreCase("back")) return null;
        cust.setAddress(addr);

        return new Order("O" + orderCount, cust, fuel, liters, bunk, dist, addr);
    }

    private boolean processPayment(Order order, Customer cust, Scanner sc) 
    {
        boolean payMenu = true;
        while (payMenu) 
	{
            System.out.println("\nSelect Payment Method:");
            System.out.println("1. " + Colors.PURPLE + "PhonePe" + Colors.RESET);
            System.out.println("2. " + Colors.BLUE + "Pay" + Colors.RESET + "tm");
            System.out.println("3. FUELgo Wallet");
            System.out.println("4. Bank Transfer");
            System.out.println("5. Add Money to Wallet");
            System.out.println("6. Previous Menu");
            String payChoiceInput = sc.nextLine();
            if (!payChoiceInput.matches("[1-6]")) 
	    {
                System.out.println(Colors.RED + "Invalid choice. Please enter 1 to 6." + Colors.RESET);
                continue;
            }
            int payChoice = Integer.parseInt(payChoiceInput);

            Payment payment = null;
            switch (payChoice) 
	    {
                case 1:
                    System.out.print("Enter PhonePe UPI ID: ");
                    String u1 = sc.nextLine();
                    if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                    payment = new PhonePe(u1);
                    break;

                case 2:
                    System.out.print("Enter Paytm UPI ID: ");
                    String u2 = sc.nextLine();
                    if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                    payment = new Paytm(u2);
                    break;

                case 3:
                    if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                    payment = wallet;
                    if (wallet.getBalance() < order.getTotal()) 
		    {
                        System.out.println(Colors.RED + "Insufficient wallet balance! Current balance: " + wallet.getBalance() + Colors.RESET);
                        System.out.println("1. Add to Wallet");
                        System.out.println("2. Previous Menu");
                        String walletChoiceInput = sc.nextLine();
                        if (!walletChoiceInput.matches("[1-2]")) 
			{
                            System.out.println(Colors.RED + "Invalid choice. Please enter 1 or 2." + Colors.RESET);
                            break;
                        }
                        int walletChoice = Integer.parseInt(walletChoiceInput);
                        if (walletChoice == 1) 
			{
                            System.out.println("\nSelect Payment Method to Add Money:");
                            System.out.println("1. " + Colors.PURPLE + "PhonePe" + Colors.RESET);
                            System.out.println("2. " + Colors.BLUE + "Pay" + Colors.RESET + "tm");
                            String addMoneyChoiceInput = sc.nextLine();
                            if (!addMoneyChoiceInput.matches("[1-2]")) 
			    {
                                System.out.println(Colors.RED + "Invalid choice. Please enter 1 or 2." + Colors.RESET);
                                break;
                            }
                            int addMoneyChoice = Integer.parseInt(addMoneyChoiceInput);
                            System.out.print("Enter Amount to Add: ");
                            String addInput = sc.nextLine();
                            if (!addInput.matches("-?\\d*\\.?\\d+")) 
			    {
                                System.out.println(Colors.RED + "Invalid amount. Please enter a number." + Colors.RESET);
                                break;
                            }
                            double add = Double.parseDouble(addInput);
                            if (add <= 0 || add > 100000) 
 		 	    {
                                System.out.println(Colors.RED + "Amount must be between 0 and 100,000." + Colors.RESET);
                                break;
                            }
                            if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                            Payment addPayment = null;
                            if (addMoneyChoice == 1) 
			    {
                                System.out.print("Enter PhonePe UPI ID: ");
                                String upiId = sc.nextLine();
                                addPayment = new PhonePe(upiId);
                            } 
			    else 
			    {
                                System.out.print("Enter Paytm UPI ID: ");
                                String upiId = sc.nextLine();
                                addPayment = new Paytm(upiId);
                            }
                            addPayment.pay(add);
                            wallet.addMoney(add);
                            if (wallet.getBalance() >= order.getTotal()) 
			    {
                                payment = wallet;
                            } 
			    else 
			    {
                                System.out.println(Colors.RED + "Still insufficient wallet balance after adding funds!" + Colors.RESET);
                                break;
                            }
                        } 
			else 
			{
                            break; // Return to payment menu
                        }
                    }
                    break;

                case 4:
                    System.out.print("Enter Bank Account Number: ");
                    String acc = sc.nextLine();
                    System.out.print("Enter IFSC Code: ");
                    String ifsc = sc.nextLine();
                    if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                    payment = new BankTransfer(acc, ifsc);
                    break;

                case 5:
                    System.out.println("\nSelect Payment Method to Add Money:");
                    System.out.println("1. " + Colors.PURPLE + "PhonePe" + Colors.RESET);
                    System.out.println("2. " + Colors.BLUE + "Pay" + Colors.RESET + "tm");
                    String addMoneyChoiceInput = sc.nextLine();
                    if (!addMoneyChoiceInput.matches("[1-2]")) 
		    {
                        System.out.println(Colors.RED + "Invalid choice. Please enter 1 or 2." + Colors.RESET);
                        break;
                    }
                    int addMoneyChoice = Integer.parseInt(addMoneyChoiceInput);
                    System.out.print("Enter Amount to Add: ");
                    String addInput = sc.nextLine();
                    if (!addInput.matches("-?\\d*\\.?\\d+")) 
    	 	    {
                        System.out.println(Colors.RED + "Invalid amount. Please enter a number." + Colors.RESET);
                        break;
                    }
                    double add = Double.parseDouble(addInput);
                    if (add <= 0 || add > 100000) 
   		    {
                        System.out.println(Colors.RED + "Amount must be between 0 and 100,000." + Colors.RESET);
                        break;
                    }
                    if (!OTPService.verifyOTP(cust.getPhone(), sc)) break;
                    Payment addPayment = null;
                    if (addMoneyChoice == 1) 
	 	    {
                        System.out.print("Enter PhonePe UPI ID: ");
                        String upiId = sc.nextLine();
                        addPayment = new PhonePe(upiId);
                    } 
		    else 
		    {
                        System.out.print("Enter Paytm UPI ID: ");
                        String upiId = sc.nextLine();
                        addPayment = new Paytm(upiId);
                    }
                    addPayment.pay(add);
                    wallet.addMoney(add);
                    break;

                case 6:
                    	payMenu = false;
                    	return false;
            }

            if (payment != null) 
	    {
                payment.pay(order.getTotal());
                if (payment instanceof FuelGoWallet && wallet.getBalance() < order.getTotal()) 
		{
                    break;
                }
                System.out.println(Colors.GREEN + "Payment successfully done. Your order is on the way." + Colors.RESET);
                payMenu = false;
                return true;
            }
        }
        return false;
    }

    private DeliveryDriver handleDeliveryPersonMenu(Scanner sc) 
    {
        System.out.println("\n1. Register as Delivery Person");
        System.out.println("2. Login as Delivery Person");
        System.out.println("3. Previous Menu");
        String choiceInput = sc.nextLine();
        if (!choiceInput.matches("[1-3]")) 
	{
            System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
            return null;
        }
        int choice = Integer.parseInt(choiceInput);

        switch (choice) 
	{
            case 1:
                System.out.print("\nEnter Name: ");
                String dName = sc.nextLine();
                if (dName.matches("-?\\d+")) 
		{
                    System.out.println(Colors.RED + "Name cannot be an integer." + Colors.RESET);
                    return null;
                }
                System.out.print("Enter Phone Number (10 digits): ");
                String dPhoneInput = sc.nextLine();
                if (!dPhoneInput.matches("\\d{10}")) 
		{
                    System.out.println(Colors.RED + "Enter valid phone number." + Colors.RESET);
                    return null;
                }
                if (deliveryManager.isPhoneNumberRegistered(dPhoneInput)) 
		{
                    System.out.println(Colors.RED + "Phone number already registered. Please use a different number or login." + Colors.RESET);
                    return null;
                }
                if (!OTPService.verifyOTP(dPhoneInput, sc)) return null;
                deliveryManager.registerDriver(dName, dPhoneInput);
                System.out.println(Colors.GREEN + "Driver registered successfully." + Colors.RESET);
                DeliveryDriver newDriver = new DeliveryDriver(dName, dPhoneInput);
                newDriver.login();
                newDriver.viewAssignedOrder();
                return newDriver;

            case 2:
                System.out.print("\nEnter Phone Number (10 digits): ");
                String phoneInput = sc.nextLine();
                if (!phoneInput.matches("\\d{10}")) 
		{
                    System.out.println(Colors.RED + "Enter valid phone number." + Colors.RESET);
                    return null;
                }
                DeliveryDriver driver = deliveryManager.loginDriver(phoneInput, sc);
                if (driver != null) 
		{
                    driver.login();
                    driver.viewAssignedOrder();
                    return driver;
                } 
		else 
		{
                    System.out.println(Colors.RED + "Driver login failed." + Colors.RESET);
                    return null;
                }

            case 3:
                return null;
        }
        return null;
    }

    private void handleDriverMenu(DeliveryDriver driver, Scanner sc) 
    {
        boolean running = true;
        while (running) 
	{
            System.out.println("\n--- Delivery Person Menu ---");
            System.out.println("1. View Assigned Orders");
            System.out.println("2. View Previous Order");
            System.out.println("3. Logout");
            String choiceInput = sc.nextLine();
            if (!choiceInput.matches("[1-3]")) 
	    {
                System.out.println(Colors.RED + "Invalid choice. Please enter 1, 2, or 3." + Colors.RESET);
                continue;
            }
            int choice = Integer.parseInt(choiceInput);

            switch (choice) 
	    {
                case 1:
                    driver.viewAssignedOrder();
                    break;
                case 2:
                    driver.viewPreviousOrder();
                    break;
                case 3:
                    driver.logout();
                    running = false;
                    break;
            }
        }
    }

    public static void main(String[] args) 
    {
        FUELgoApp app = new FUELgoApp();
        app.run();
    }
}