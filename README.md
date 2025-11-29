This project is a console-based Java application that simulates an on-demand fuel delivery system, similar to modern real-time service platforms. It allows customers to order fuel online, choose fuel types, make secure payments, and get fuel delivered to their doorstep by registered delivery drivers. The system supports customer login with OTP verification, wallet and UPI-based payments, automatic delivery driver assignment, real-time order status updates, and detailed billing. The application is designed using core Java concepts such as OOPS, abstraction, inheritance, polymorphism, interfaces, multithreading, exception handling, collections, and user input handling through Scanner.

1. Overall Idea of the Project
This project simulates a real time fuel delivery application called FUELgo:
Customers can:
Login with OTP
Choose fuel type, bunk, liters, and delivery address
See a full bill
Pay using different payment methods
Track the order status
Delivery drivers can:
Register or login with OTP
Accept and deliver assigned orders
View assigned and previous orders

Technically, it uses:
OOP principles
Abstraction, inheritance, polymorphism, interface
Composition (classes using other classes)
Threads (for delivery simulation)
Validation, OTP flow, menus, loops

  //main method
run() Method
Starts the app.
Uses Scanner for input.
Prints greeting messages with colors.
Shows Main Menu:
Customer Login
Delivery Person Register/Login
Exit
Based on selection, calls:
authenticateCustomer(sc) and then handleCustomerMenu(...)
handleDeliveryPersonMenu(sc) and then handleDriverMenu(...)
Keeps running until user chooses Exit.
