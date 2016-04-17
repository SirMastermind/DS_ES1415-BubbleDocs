package pt.ulisboa.tecnico.sdis.uddi;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Key;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

/**
 * This class defines simple methods to bind UDDI organizations to URL
 * addresses: list, lookup, unbind, bind, rebind. It is inspired by the
 * java.rmi.Naming class.
 *
 * To achieve greater control of the underlying registry, the JAX-R API should
 * be used instead.
 */
public class UDDINaming {

    /** JAX-R query object */
    private BusinessQueryManager bqm;
    /** JAX-R update object */
    private BusinessLifeCycleManager blcm;

    /** JAX-R connection factory */
    private ConnectionFactory connFactory;
    /** JAX-R connection */
    private Connection conn;

    /** UDDI user name */
    private String username = "username";
    /** UDDI user password */
    private char[] password = "password".toCharArray();

    /**
     * option to establish connection automatically - Should the lookup method
     * connect automatically? true - yes, false - no
     */
    private boolean autoConnectFlag;

    /** option to print JNDI and JAX-R debug messages */
    private boolean debugFlag = false;

    //
    // Constructors
    //

    /** Create an UDDI client configured to access the specified URL */
    public UDDINaming(String url) throws JAXRException {
        this(url, true);
    }

    /**
     * Create an UDDI client configured to access the specified URL and with the
     * specified auto-connect option
     */
    public UDDINaming(String url, boolean autoConnect) throws JAXRException {
        if (!url.startsWith("http"))
            throw new IllegalArgumentException(
                    "Please provide UDDI server URL in http://host:port format!");
        this.autoConnectFlag = autoConnect;

        try {
            InitialContext context = new InitialContext();
            connFactory = (ConnectionFactory) context
                    .lookup("java:jboss/jaxr/ConnectionFactory");
        } catch (NamingException e) {
            // Could not find using JNDI
            if (debugFlag) {
                System.out.println("Caught " + e);
                e.printStackTrace(System.out);
            }

            // try factory method from scout
            System.setProperty("javax.xml.registry.ConnectionFactoryClass",
                    "org.apache.ws.scout.registry.ConnectionFactoryImpl");
            connFactory = ConnectionFactory.newInstance();
        }

        // define system properties used to perform replacements in uddi.xml
        if (System.getProperty("javax.xml.registry.queryManagerURL") == null)
            System.setProperty("javax.xml.registry.queryManagerURL", url
                    + "/juddiv3/services/inquiry");

        if (System.getProperty("javax.xml.registry.lifeCycleManagerURL") == null)
            System.setProperty("javax.xml.registry.lifeCycleManagerURL", url
                    + "/juddiv3/services/publish");

        if (System.getProperty("javax.xml.registry.securityManagerURL") == null)
            System.setProperty("javax.xml.registry.securityManagerURL", url
                    + "/juddiv3/services/security");

        Properties props = new Properties();
        props.setProperty("scout.juddi.client.config.file", "uddi.xml");
        props.setProperty("javax.xml.registry.queryManagerURL",
                System.getProperty("javax.xml.registry.queryManagerURL"));
        props.setProperty("scout.proxy.uddiVersion", "3.0");
        props.setProperty("scout.proxy.transportClass",
                "org.apache.juddi.v3.client.transport.JAXWSTransport");
        connFactory.setProperties(props);
    }

    //
    // Accessors
    //

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public boolean isPrintDebug() {
        return debugFlag;
    }

    /** print information messages? */
    public void setPrintDebug(boolean infoFlag) {
        this.debugFlag = infoFlag;
    }

    /**
     * Main method expects two arguments: - UDDI server URL - Organization name
     *
     * Main performs a lookup on UDDI server using the organization name. If a
     * registration is found, the service URL is printed to standard output. If
     * not, nothing is printed.
     *
     * Standard error is used to print error messages.
     */
    public static void main(String[] args) {
        // Check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s uddiURL orgName%n",
                    UDDINaming.class.getName());
            return;
        }

        String uddiURL = args[0];
        String orgName = args[1];

        UDDINaming instance;
        try {
            instance = new UDDINaming(uddiURL);
            String url = instance.lookup(orgName);

            if (url != null)
                System.out.println(url);

        } catch (JAXRException e) {
            System.err.println("Caught JAX-R exception!");
            e.printStackTrace(System.err);
        }
    }

    //
    // Connection management
    //

    /** Connect to UDDI server */
    public void connect() throws JAXRException {

        conn = connFactory.createConnection();

        // Define credentials
        PasswordAuthentication passwdAuth = new PasswordAuthentication(
                username, password);
        Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();
        creds.add(passwdAuth);
        conn.setCredentials(creds);

        // Get RegistryService object
        RegistryService rs = conn.getRegistryService();

        // Get QueryManager object (for inquiries)
        bqm = rs.getBusinessQueryManager();

        // get BusinessLifeCycleManager object (for updates)
        blcm = rs.getBusinessLifeCycleManager();
    }

    /** Disconnect from UDDI server */
    public void disconnect() throws JAXRException {
        try {
            if (conn != null)
                conn.close();
        } finally {
            conn = null;
            bqm = null;
            blcm = null;
        }
    }

    /** Disconnect from UDDI server, ignoring JAX-R exceptions */
    public void disconnectQuietly() {
        try {
            disconnect();

        } catch (JAXRException e) {
            // ignore
        }
    }

    /** helper method to automatically connect to registry */
    private void autoConnect() throws JAXRException {
        if (conn == null)
            if (autoConnectFlag)
                connect();
            else
                throw new IllegalStateException(
                        "Not connected! Cannot perform operation!");
    }

    /** helper method to automatically disconnect from registry */
    private void autoDisconnect() throws JAXRException {
        if (autoConnectFlag)
            disconnectQuietly();
    }

    //
    // UDDINaming interface
    // Outer methods manage connection and call internal operations
    //

    /** Returns a collection of URL bound to the name */
    public Collection<String> list(String orgName) throws JAXRException {
        autoConnect();
        try {
            return queryAll(orgName);
        } finally {
            autoDisconnect();
        }
    }

    /** Returns the first URL associated with the specified name */
    public String lookup(String orgName) throws JAXRException {
        autoConnect();
        try {
            return query(orgName);
        } finally {
            autoDisconnect();
        }
    }

    /** Destroys the binding for the specified name */
    public void unbind(String orgName) throws JAXRException {
        autoConnect();
        try {
            deleteAll(orgName);

        } finally {
            autoDisconnect();
        }
    }

    /** Binds the specified name to a URL */
    public void bind(String orgName, String url) throws JAXRException {
        autoConnect();
        try {
            publish(orgName, url);

        } finally {
            autoDisconnect();
        }
    }

    /** Rebinds the specified name to a new URL */
    public void rebind(String orgName, String url) throws JAXRException {
        autoConnect();
        try {
            deleteAll(orgName);
            publish(orgName, url);

        } finally {
            autoDisconnect();
        }
    }

    //
    // private implementation
    //

    private Collection<String> queryAll(String orgName) throws JAXRException {
        List<String> result = new ArrayList<String>();

        // search by name
        Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

        // query organizations
        Collection<String> namePatterns = new ArrayList<String>();
        namePatterns.add(orgName);

        // perform search
        BulkResponse r = bqm.findOrganizations(findQualifiers, namePatterns,
                null, null, null, null);
        @SuppressWarnings("unchecked")
        Collection<Organization> orgs = r.getCollection();
        if (debugFlag)
            System.out.printf("Found %d organizations%n", orgs.size());

        for (Organization o : orgs) {
            @SuppressWarnings("unchecked")
            Collection<Service> services = o.getServices();
            if (debugFlag)
                System.out.printf("Found %d services%n", services.size());

            for (Service s : services) {
                @SuppressWarnings("unchecked")
                Collection<ServiceBinding> serviceBindinds = (Collection<ServiceBinding>) s
                        .getServiceBindings();
                if (debugFlag)
                    System.out.printf("Found %d service bindings%n",
                            serviceBindinds.size());

                for (ServiceBinding sb : serviceBindinds) {
                    result.add(sb.getAccessURI());
                }
            }
        }

        // service binding not found
        if (debugFlag)
            System.out.printf("Returning list with size %d%n", result.size());
        return result;
    }

    private String query(String orgName) throws JAXRException {
        Collection<String> listResult = queryAll(orgName);
        int listResultSize = listResult.size();

        if (listResultSize == 0) {
            // service binding not found
            if (debugFlag)
                System.out.println("Service binding not found; Returning null");
            return null;
        } else {
            if (listResultSize > 1)
                if (debugFlag)
                    System.out.printf(
                            "Returning first service binding of %d found%n",
                            listResultSize);
            return listResult.iterator().next();
        }
    }

    private boolean deleteAll(String orgName) throws JAXRException {

        Collection<String> findQualifiers = new ArrayList<String>();
        findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

        Collection<String> namePatterns = new ArrayList<String>();
        namePatterns.add(orgName);

        // Search existing
        BulkResponse response = bqm.findOrganizations(findQualifiers,
                namePatterns, null, null, null, null);
        @SuppressWarnings("unchecked")
        Collection<Organization> orgs = response.getCollection();
        Collection<Key> orgsToDelete = new ArrayList<Key>();

        for (Organization org : orgs)
            if (org.getName().getValue().equals(orgName))
                orgsToDelete.add(org.getKey());

        // delete previous registrations
        if (orgsToDelete.isEmpty()) {
            return true;
        } else {
            if (debugFlag)
                System.out.printf("%d organizations to delete%n",
                        orgsToDelete.size());

            BulkResponse deleteResponse = blcm
                    .deleteOrganizations(orgsToDelete);
            boolean result = (deleteResponse.getStatus() == JAXRResponse.STATUS_SUCCESS);

            if (debugFlag) {
                if (result) {
                    System.out
                            .println("UDDI deregistration completed successfully.");
                } else {
                    System.out.println("UDDI error during deregistration.");
                }
            }

            return result;
        }
    }

    private boolean publish(String orgName, String url) throws JAXRException {
        // derive other names from organization name
        String serviceName = orgName + " service";
        String bindingDesc = serviceName + " binding";

        if (debugFlag) {
            System.out.printf("Derived service name %s%n", serviceName);
            System.out.printf("Derived binding description %s%n", bindingDesc);
        }

        return publish(orgName, serviceName, bindingDesc, url);
    }

    private boolean publish(String orgName, String serviceName,
            String bindingDescription, String bindingURL) throws JAXRException {

        // Create organization
        Organization org = blcm.createOrganization(orgName);

        // Create service
        Service service = blcm.createService(serviceName);
        service.setDescription(blcm.createInternationalString(serviceName));
        // Add service to organization
        org.addService(service);
        // Create serviceBinding
        ServiceBinding serviceBinding = blcm.createServiceBinding();
        serviceBinding.setDescription(blcm
                .createInternationalString(bindingDescription));
        serviceBinding.setValidateURI(false);
        // Define the Web Service endpoint address here
        serviceBinding.setAccessURI(bindingURL);
        if (serviceBinding != null) {
            // Add serviceBinding to service
            service.addServiceBinding(serviceBinding);
        }

        // register new organization/service/serviceBinding
        Collection<Organization> orgs = new ArrayList<Organization>();
        orgs.add(org);
        BulkResponse response = blcm.saveOrganizations(orgs);

        boolean result = (response.getStatus() == JAXRResponse.STATUS_SUCCESS);

        if (debugFlag) {
            if (result) {
                System.out.println("UDDI registration completed successfully.");
            } else {
                System.out.println("UDDI error during registration.");
            }
        }

        return result;
    }

}
