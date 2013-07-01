package pt.ist.anacom.applicationserver;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.registry.BulkResponse;
import javax.xml.registry.BusinessLifeCycleManager;
import javax.xml.registry.BusinessQueryManager;
import javax.xml.registry.Connection;
import javax.xml.registry.ConnectionFactory;
import javax.xml.registry.FindQualifier;
import javax.xml.registry.JAXRException;
import javax.xml.registry.JAXRResponse;
import javax.xml.registry.RegistryService;
import javax.xml.registry.infomodel.Organization;
import javax.xml.registry.infomodel.Service;
import javax.xml.registry.infomodel.ServiceBinding;

import pt.ist.fenixframework.pstm.TransactionChangeLogs;
import pt.ist.fenixframework.pstm.repository.Repository;


/**
 * Realiza a inicialização da FF. Os paths insicados na configuração devem ser absolutos e
 * não relativos porque o sistema é implementado em servidores com ambientes distintos.
 * Este EventListener é invocado quando ocorrem eventos como o deploy e undeploy da
 * applicacao. É usado para fazer o arranque e paragem da FF
 */
public class ApplicationServerInitListener
        implements ServletContextListener {

    private String operatorName = null;
    private String operatorPrefix = null;
    private String serverName = null;
    private String smsTariff = null;
    private String voiceTariff = null;
    private String videoTariff = null;
    private String extraTariff = null;
    private String bonusTariff = null;
    private String uddiServerIP = "localhost";
    private String ServerIP = "localhost";


    // Handlers
    public static String serverSecurityName;

    // UDDI
    private String organizationName = null;
    private String serviceName = null;
    private String bindingDescription = "Informação para acesso ao serviço";
    private String bindingURL = "http://" + ServerIP + ":8080/" + serverName + "/Operator";

    private BusinessQueryManager businessQueryManager = null;
    private BusinessLifeCycleManager businessLifeCycleManager = null;
    private Organization orgX = null;
    private Collection<Organization> orgs = null;




    /**
     * contextInitialized - Cria contexto de cada servidor
     */
    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        System.out.println("\n.........Entrou no context listener.........\n");


        try {
            // leitura das caracteristicas do operador definidos no web.xml
            operatorName = arg0.getServletContext().getInitParameter("operatorName");
            serverName = arg0.getServletContext().getInitParameter("serverName");
            operatorPrefix = arg0.getServletContext().getInitParameter("operatorPrefix");
            smsTariff = arg0.getServletContext().getInitParameter("smsTariff");
            voiceTariff = arg0.getServletContext().getInitParameter("voiceTariff");
            videoTariff = arg0.getServletContext().getInitParameter("videoTariff");
            extraTariff = arg0.getServletContext().getInitParameter("extraTariff");
            bonusTariff = arg0.getServletContext().getInitParameter("bonusTariff");

            ApplicationServerInitListener.serverSecurityName = arg0.getServletContext().getInitParameter("serverSecurityName");

            ApplicationServerWebService.init(operatorName, serverName, operatorPrefix, smsTariff, voiceTariff, videoTariff, extraTariff, bonusTariff);

        } catch (Exception e) {
            System.out.println("ApplicationServerInitListner: Error starting up the web service: \n");
            e.printStackTrace();
        }

        if (arg0.getServletContext().getInitParameter("operatorName").toString().equals("CA"))
            return;

        uddiSetup();
        uddiPrinter();
        uddiRegister();
    }



    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

        System.out.println("\n.........Entrou no context contextDestroyed .........\n");

        // remover serviço do UDDI Server
        uddiSetup();
        uddiPrinter();
        uddiUnregister();

        // TODO Resolver o loop do FF
        TransactionChangeLogs.finalizeTransactionSystem();

        Repository rep = Repository.getRepository();
        rep.closeRepository();
    }

    /**
     * Faz a ligaçao com o servidor UDDI
     */
    @SuppressWarnings("unchecked")
    private void uddiSetup() {
        try {
            ConnectionFactory connFactory = org.apache.ws.scout.registry.ConnectionFactoryImpl.newInstance();

            // //////////////////////////////////////////////////////
            // Ligação ao UDDI registry
            // //////////////////////////////////////////////////////
            Properties props = new Properties();
            props.setProperty("scout.juddi.client.config.file", "uddi.xml");
            props.setProperty("javax.xml.registry.queryManagerURL", "http://" + uddiServerIP + ":8081/juddiv3/services/inquiry");
            props.setProperty("javax.xml.registry.lifeCycleManagerURL", "http://" + uddiServerIP + ":8081/juddiv3/services/publish");
            props.setProperty("javax.xml.registry.securityManagerURL", "http://" + uddiServerIP + ":8081/juddiv3/services/security");
            props.setProperty("scout.proxy.uddiVersion", "3.0");
            props.setProperty("scout.proxy.transportClass", "org.apache.juddi.v3.client.transport.JAXWSTransport");
            connFactory.setProperties(props);

            // Finalmente, estabelece a ligação ao UDDI registry
            Connection connection = connFactory.createConnection();

            // Define credenciais de autenticação a usar para interacção com o UDDI
            // registry
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

            // //////////////////////////////////////////////////////
            // Pesquisa de organização já registada
            // //////////////////////////////////////////////////////
            Collection<String> findQualifiers = new ArrayList<String>();
            findQualifiers.add(FindQualifier.SORT_BY_NAME_DESC);

            // Pretendemos criar nova organização apenas caso ainda não exista
            // Logo, vamos primeiro pesquisar por organização com nome "MinhaOrganizacao"

            // ////////////////////////////////////////////////////
            // Ligação ao UDDI registry
            // ////////////////////////////////////////////////////

            // defenir nomes , serviço e organizaçao
            organizationName = operatorName;
            serviceName = serverName;
            bindingURL = "http://" + ServerIP + ":8080/" + serverName + "/Operator";

            Collection<String> namePatterns = new ArrayList<String>();
            namePatterns.add("%" + organizationName + "%");
            // Efectua a pesquisa
            BulkResponse r = businessQueryManager.findOrganizations(findQualifiers, namePatterns, null, null, null, null);
            orgs = r.getCollection();

        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        } catch (Exception e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        }

    }

    /**
     * Retira o serviço do Servidor UDDI
     */
    private void uddiUnregister() {
        try {

            for (Organization o : orgs) {
                // Vai buscar a organizaçao
                if (o.getName().getValue().equals(organizationName)) {
                    // guarda a organizaçao para futuras referencias
                    orgX = o;

                    @SuppressWarnings("unchecked")
                    Iterator<Service> ito = o.getServices().iterator();
                    // enquanto haver serviços
                    while (ito.hasNext()) {
                        Service sc = ito.next();

                        // Se o nome fo igual modifica o o service binding
                        if (sc.getName().getValue().equals(serverName)) {
                            System.out.println("UDDI: " + serverName + " Registado! Vamos retiralo do servidor");
                            o.removeService(sc);
                        }
                    }
                    break;
                }
            }

            Collection<Organization> orgs2 = new ArrayList<Organization>();
            orgs2.add(orgX);

            // Finalmente, regista a nova organization/service/serviceBinding
            // (ou as novas alterações) no UDDI registry
            BulkResponse br = businessLifeCycleManager.saveOrganizations(orgs2);

            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
                System.out.println("UDDI: UnRegisto completado com sucesso.");
            else {
                System.out.println("UDDI: Erro durante o Unregisto no UDDI.\n Tentando outravez");
                uddiUnregister();
            }

        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        } catch (Exception e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        }
    }

    /**
     * Adiciona o serviço ao servidor UDDI
     */
    private void uddiRegister() {

        try {

            for (Organization o : orgs) {
                // Vai buscar a organizaçao
                if (o.getName().getValue().equals(organizationName)) {

                    // guarda a organizaçao para futuras referencias
                    orgX = o;
                    boolean r = false;
                    @SuppressWarnings("unchecked")
                    Iterator<Service> ito = o.getServices().iterator();
                    // enquanto haver serviços
                    while (ito.hasNext()) {
                        Service sc = ito.next();

                        // Se o nome fo igual modifica o o service binding
                        if (sc.getName().getValue().equals(serverName)) {
                            System.out.println("UDDI: " + serverName + " Já esta registado! Vamos Actualizar as Variaveis");
                            o.removeService(sc);
                            addThisServiceToUDDI(o);
                            r = true;
                            break;
                        }
                    }

                    // se o seerviço nao existir mas a empresa sim vamos adicionar o novo
                    // serviço
                    if (!r) {
                        System.out.println("UDDI: " + serverName + " Não esta registado! Vamos Adiciona-lo");
                        addThisServiceToUDDI(o);
                    }
                    break;
                }
            }


            // se a prganizaçao nao existir, criamos e popolamos
            if (orgX == null) {
                System.out.println("UDDI:  Organização" + organizationName + " Não Existe Vamos cria-la e adicionar-lhe o serviço");
                // Não se encontrou a organização já registada, logo vamos criá-la
                orgX = businessLifeCycleManager.createOrganization(organizationName);
                orgX.setDescription(businessLifeCycleManager.createInternationalString(operatorPrefix));
                addThisServiceToUDDI(orgX);
            }


            Collection<Organization> orgs2 = new ArrayList<Organization>();
            orgs2.add(orgX);

            // Finalmente, regista a nova organization/service/serviceBinding
            // (ou as novas alterações) no UDDI registry
            BulkResponse br = businessLifeCycleManager.saveOrganizations(orgs2);

            if (br.getStatus() == JAXRResponse.STATUS_SUCCESS)
                System.out.println("UDDI: Registo completado com sucesso.");
            else {
                System.out.println("UDDI: Erro durante o registo no UDDI.\n Tentando outra vez !!!");
                uddiSetup();
                uddiRegister();
            }


        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        } catch (Exception e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        }
    }

    /**
     * Adiciona este serviço a organição passada por parametro
     * 
     * @param o organizaçao na qual vai ser registado o serviço
     */
    private void addThisServiceToUDDI(Organization o) {
        try {

            // Cria serviço
            Service service = businessLifeCycleManager.createService(serviceName);
            service.setDescription(businessLifeCycleManager.createInternationalString(serviceName));

            // Cria serviceBinding
            ServiceBinding serviceBinding = businessLifeCycleManager.createServiceBinding();
            serviceBinding.setDescription(businessLifeCycleManager.createInternationalString(bindingDescription));
            serviceBinding.setValidateURI(false);

            // Aqui define-se o URL do endpoint do Web Service
            serviceBinding.setAccessURI(bindingURL);

            // Adiciona o serviceBinding ao serviço
            service.addServiceBinding(serviceBinding);

            o.addService(service);

        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao adicionar serviço a uddi.");
        }

    }


    /**
     * Imprime as organizaçoes ja existentes no UDDI server
     */
    private void uddiPrinter() {

        try {
            System.out.println("\n--Uddi: Imprimir Serviços!!!");

            for (Organization o : orgs) {
                System.out.println("\t" + o.getName().getValue());

                @SuppressWarnings("unchecked")
                Iterator<Service> i = o.getServices().iterator();
                while (i.hasNext())
                    System.out.println("\t\t" + i.next().getName().getValue());
            }

            System.out.println("\n--Uddi: Imprimir Serviços!!! Done!!!\n");
        } catch (JAXRException e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        } catch (Exception e) {
            System.err.println("UDDI: Erro ao contactar o UDDI registry.");
            // //e.printStackTrace();
        }
    }



    public static String getServerSecurityName() {
        return serverSecurityName;
    }



    public static void setServerSecurityName(String serverSecurityName) {
        ApplicationServerInitListener.serverSecurityName = serverSecurityName;
    }



}
