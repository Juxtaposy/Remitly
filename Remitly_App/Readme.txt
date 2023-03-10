This is Currency Exchanger Java application for Remitly Intership task.

Author(s): Mateusz Gawroński and online tutorials

Built with Java 17 SE using Eclipse IDE.

HOW TO RUN:
After downloading the source code, either open project using IDE or run via command line:

1. Navigate to ...\Remitly_App\src
2. Compile using: javac app\remitly\CurrencyExchanger.java
3. Run using: java app.remitly.CurrencyExchanger

APPLICATION:
User can type within both visible texts fields to calculate between exchange rates for GBP and PLN.
Current exchange rate is displayed on top of application window. It is fetched from official NBP API 
found at "api.nbp.pl website.

KNOWN ISSUES:
- If user types delimiter "." first, it won't be treated as delimiter but will be displayed in the window.

- If multiple delimiters are present, they are invisible to the calculator but have to be removed by the user 
in order to increase currency value beyond 1.0. 

MORE INFO:
Application HAS NOT been tested on other Java version so use at  your own risk.
Manual tests were conducted, no Unit Tests are present. 