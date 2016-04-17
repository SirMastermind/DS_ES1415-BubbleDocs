package pt.ulisboa.tecnico.sdis.uddi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test suite
 */
public class UDDINamingIT {

    // static members

    // one-time initialization and clean-up

    @BeforeClass
    public static void oneTimeSetUp() {

    }

    @AfterClass
    public static void oneTimeTearDown() {

    }


    // members

    String uddiURL = "http://localhost:8081";
    String name = "MyWebServiceName";
    String url = "http://host:port/my-ws/endpoint";

    private UDDINaming uddiNaming;


    // initialization and clean-up for each test

    @Before
    public void setUp() throws Exception {
        uddiNaming = new UDDINaming(uddiURL);
    }

    @After
    public void tearDown() {
        uddiNaming = null;
    }


    // tests

    @Test
    public void test() throws Exception {
        // publish to UDDI
        uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);

        // query UDDI
        String endpointAddress = uddiNaming.lookup(name);

        assertNotNull(endpointAddress);
        assertEquals(/* expected */ url, /* actual */ endpointAddress);
    }

    @Test
    public void testWildcard() throws Exception {
        // publish to UDDI
        uddiNaming = new UDDINaming(uddiURL);
        uddiNaming.rebind(name, url);

        // query UDDI using a wildcard character '%'
        String nameWithWildcard = name.substring(0, 5) + "%";
        String endpointAddress = uddiNaming.lookup(nameWithWildcard);

        assertNotNull(endpointAddress);
        assertEquals(/* expected */ url, /* actual */ endpointAddress);
    }

}
