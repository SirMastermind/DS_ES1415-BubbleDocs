package pt.ulisboa.tecnico.sdis.store.ws.impl;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class StoreWebServiceListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("Deregistering SQL-Drivers ...");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            try {
                DriverManager.deregisterDriver(driver);
                System.out.println(driver.getClass().getName());
            } catch (SQLException e) {
                System.err.println("Error deregistering driver "
                        + driver.getClass().getName());
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

    }

}
