package pt.ist.anacom.presentationserver.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.net.PasswordAuthentication;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;


import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import pt.ist.anacom.shared.exceptions.InvalidOperatorException;

/**
 * Resolve o nome ou o prefixo do operador no URL do servidor.
 */
public class WebServicesLocator {
    /** Lista de referências guardadas. */
    private List<Referencia> list;

    //UDDI
    private String uddiServerIP = "localhost";

    private BusinessQueryManager businessQueryManager = null;
    private BusinessLifeCycleManager businessLifeCycleManager = null;
    private Collection<Organization> orgs = null;
    // Organizaçoes por quais procuramos
    private Collection<String> namePatterns = new ArrayList<String>(
            Arrays.asList("%Vodafone%", "%TMN%", "%Optimus%"));


    /**
     * Instanciar as referências guardadas, acrescentar AQUI, nos XML e na ANT Build os
     * novos operadores.
     */
    public WebServicesLocator() {
        list = new ArrayList<Referencia>();

        //Variaves Staticas usadas antes de se usar o servidor UDDI
        //list.add(new Referencia("TMN", "TMN", "96", "http://localhost:8080/TMN/Operator?wsdl"));
        //list.add(new Referencia("Optimus", "Optimus", "93", "http://localhost:8080/Optimus/Operator?wsdl"));
        //list.add(new Referencia("Vodafone","Vodafone-R1", "91", "http://localhost:8080/Vodafone-R1/Operator?wsdl"));
        //list.add(new Referencia("Vodafone","Vodafone-R2", "91", "http://localhost:8080/Vodafone-R2/Operator?wsdl"));
        //list.add(new Referencia("Vodafone","Vodafone-R3", "91", "http://localhost:8080/Vodafone-R3/Operator?wsdl"));

        System.out.println(" \n\n\n WebServiceLocator: Loading UDDI Server ...\n\n\n");
        uddiSetup();
        uddiPrinter();
        listUpdater();
        System.out.println("\n\nWebServiceLocator: UDDI Server List Updated\n\n");
    }

    /**
     * 
     */
    private void listUpdater(){

        ArrayList<Referencia> l = new ArrayList<Referencia>();

        try{
            if(orgs!=null){
                for (Organization o : orgs) {

                    String opName = o.getName().getValue();
                    String prefix = o.getDescription().getValue().toString();

                    @SuppressWarnings("unchecked")
                    Iterator<Service> ito = o.getServices().iterator();

                    // enquanto haver serviços
                    while(ito.hasNext()){
                        Service sc = ito.next();
                        String svcName = sc.getName().getValue().toString();
                        String url = ((ServiceBinding)sc.getServiceBindings().iterator().next()).getAccessURI();
                        System.out.println("WebServiceLocator: ADD  O:"+opName+" P:"+prefix+" S:"+svcName+" U:"+url);
                        l.add(new Referencia(opName, svcName, prefix, url+"?wsdl"));
                    }
                }
            }else
                System.out.println("WebServiceLocator: Não foram encontrados Serviços\n Verifique Servidor UDDI");


            // actualiza a lista principal
            list = l;

        }catch(Exception e){
            System.out.println("WebServiceLocator: Erro ao actualizar a lista de Servidores ");
        }
    }

    /**
     * Imprime as organizaçoes ja existentes no UDDI server
     */
    private void uddiPrinter(){

        try {
            System.out.println("\n--Uddi: Imprimir Serviços!!!");

            for (Organization o : orgs) {
                System.out.println("\t"+o.getName().getValue());

                @SuppressWarnings("unchecked")
                Iterator<Service> i = o.getServices().iterator();
                while(i.hasNext())
                    System.out.println("\t\t"+i.next().getName().getValue());
            }

            System.out.println("\n--Uddi: Imprimir Serviços!!! Done!!!\n");
        } catch (JAXRException e) {
            System.err.println("........................UDDI: Erro ao contactar o UDDI registry.");
            ////e.printStackTrace();
        }catch (Exception e) {
            System.err.println("........................UDDI: Erro ao contactar o UDDI registry.");
            ////e.printStackTrace();
        }
    }

    /**
     * Faz a ligaçao com o servidor UDDI
     */
    @SuppressWarnings("unchecked")
    private void uddiSetup() {
        try {
            ConnectionFactory connFactory = org.apache.ws.scout.registry.ConnectionFactoryImpl.newInstance();

            ////////////////////////////////////////////////////////
            // Ligação ao UDDI registry
            ////////////////////////////////////////////////////////
            Properties props = new Properties();
            props.setProperty("scout.juddi.client.config.file", "src/main/uddi/uddi.xml");
            props.setProperty("javax.xml.registry.queryManagerURL", "http://"+uddiServerIP+":8081/juddiv3/services/inquiry");
            props.setProperty("javax.xml.registry.lifeCycleManagerURL", "http://"+uddiServerIP+":8081/juddiv3/services/publish");
            props.setProperty("javax.xml.registry.securityManagerURL", "http://"+uddiServerIP+":8081/juddiv3/services/security");
            props.setProperty("scout.proxy.uddiVersion", "3.0");
            props.setProperty("scout.proxy.transportClass", "org.apache.juddi.v3.client.transport.JAXWSTransport");
            connFactory.setProperties(props);

            // Finalmente, estabelece a ligação ao UDDI registry
            Connection connection = connFactory.createConnection();

            // Define credenciais de autenticação a usar para interacção com o UDDI registry
            PasswordAuthentication passwdAuth = new PasswordAuthentication("username", "password".toCharArray());  
            Set<PasswordAuthentication> creds = new HashSet<PasswordAuthentication>();  
            creds.add(passwdAuth);
            connection.setCredentials(creds);

            // Obter objecto RegistryService
            RegistryService rs = connection.getRegistryService();

            // Obter objecto QueryManager da JAXR Business API     

            // (caso se pretenda fazer pesquisas)
            businessQueryManager = rs.getBusinessQueryManager();
            // Obter objecto BusinessLifeCycleManager da JAXR Business API 
            // (caso se pretenda registar/alterar informação no UDDI registry)
            businessLifeCycleManager = rs.getBusinessLifeCycleManager();

            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

            // Efectua a pesquisa

            BulkResponse r = businessQueryManager.findOrganizations(findQualifiers, namePatterns, null, null, null, null);
            orgs = r.getCollection();

        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.1");
            e.printStackTrace();
        }catch (Exception e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.2");
            ////e.printStackTrace();
        }

    }

    /**
     * Gets the url by operator name.
     * 
     * @param operatorName the operator name
     * @return the url by operator name
     * @throws InvalidOperatorException operador não existente
     */
    public String getUrlByOperatorName(String operatorName) throws InvalidOperatorException {
        for (Referencia ref : list) {
            if (ref.getOperatorName().equals(operatorName)) {
                return ref.getUrl();
            }
        }
        throw new InvalidOperatorException(operatorName, "WebServicesLocator: There is no operator with this name: " + operatorName);
    }

    /**
     * Devolve uma lista de urls dado o nome do operador
     * @param operatorName nome do operador
     * @return uma lista com os links para as replicas deste operador
     * @throws InvalidOperatorException
     */
    @SuppressWarnings("null")
    public ArrayList<String> getUrlListByOperatorName(String operatorName) throws InvalidOperatorException {

        ArrayList<String> l = new ArrayList<String>();

        for (Referencia ref : list) {
            if (ref.getOperatorName().equals(operatorName)) {
                l.add(ref.getUrl());
            }
        }
        if(l.equals(null))
            throw new InvalidOperatorException(operatorName, "WebServicesLocator: There is no operator with this name: " + operatorName);
        else
            return l;
    }


    /**
     * Gets the url by operator prefix.
     * 
     * @param operatorPrefix the operator prefix
     * @return the url by operator prefix
     * @throws InvalidOperatorException the invalid operator exception
     */
    public String getUrlByOperatorPrefix(String operatorPrefix) throws InvalidOperatorException {
        for (Referencia ref : list) {
            if (ref.getOperatorPrefix().equals(operatorPrefix)) {
                return ref.getUrl();
            }
        }
        throw new InvalidOperatorException(operatorPrefix, "Uddi: There is no operator with this prefix: " + operatorPrefix);
    }


    /**
     * Devolve uma lista de ports dado o prefixo da operadora
     * @param operatorPrefix
     * @return uma lista de portas
     * @throws InvalidOperatorException
     */
    @SuppressWarnings({ "null", "unused" })
    public ArrayList<String> getUrlListByOperatorPrefix(String operatorPrefix) throws InvalidOperatorException {

        ArrayList<String> l = new ArrayList<String>();

        for (Referencia ref : list) {
            if (ref.getOperatorPrefix().equals(operatorPrefix)) {
                l.add(ref.getUrl());
            } 
        }
        
        if(l.equals(null)){
            throw new InvalidOperatorException(operatorPrefix, "Uddi: There is no operator with this prefix: " + operatorPrefix);
        }
        
        return l;    
    }

    /**
     * Class interna, apenas faz sentido no contexto do uddi.
     */
    class Referencia {

        /** The operator name. */
        private String operatorName;

        /** The operator name. */
        private String serverName;

        /** The operator prefix. */
        private String operatorPrefix;

        /** The url. */
        private String url;

        /**
         * Instantiates a new referencia.
         * 
         * @param operatorName the operator name
         * @param operatorPrefix the operator prefix
         * @param url the url
         */
        public Referencia(String operatorName, String serverName, String operatorPrefix, String url) {
            this.operatorName = operatorName;
            this.serverName = serverName;
            this.operatorPrefix = operatorPrefix;
            this.url = url;
        }

        /**
         * Gets the operator name.
         * 
         * @return the operator name
         */
        public String getOperatorName() {
            return operatorName;
        }


        public void setServerName(String s) {
            this.serverName = s;
        }

        public String getServerName() {
            return serverName;
        }


        /**
         * Sets the operator name.
         * 
         * @param operatorName the new operator name
         */
        public void setOperatorName(String operatorName) {
            this.operatorName = operatorName;
        }

        /**
         * Gets the operator prefix.
         * 
         * @return the operator prefix
         */
        public String getOperatorPrefix() {
            return operatorPrefix;
        }

        /**
         * Sets the operator prefix.
         * 
         * @param operatorPrefix the new operator prefix
         */
        public void setOperatorPrefix(String operatorPrefix) {
            this.operatorPrefix = operatorPrefix;
        }

        /**
         * Gets the url.
         * 
         * @return the url
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the url.
         * 
         * @param url the new url
         */
        public void setUrl(String url) {
            this.url = url;
        }
    }
}