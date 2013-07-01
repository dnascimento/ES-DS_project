package pt.ist.anacom.applicationserver;

import pt.ist.anacom.domain.NetworkManager;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.Config.RepositoryType;
import pt.ist.fenixframework.FenixFramework;


/** Arranque da base de dados, ver PresentationServer&AnacomServiceImpl class. */
public class DatabaseBootstrap {

    /** Not initialized yet? */
    private static boolean notInitialized = true;


    /** Inicializar a fenixframework. */
    public static void init() {
        if (notInitialized) {
            FenixFramework.initialize(new Config() {
                {
                    dbAlias = "/tmp/db/local";
                    domainModelPath = "src/main/dml/anacom.dml";
                    repositoryType = RepositoryType.BERKELEYDB;
                    rootClass = NetworkManager.class;
                }
            });
        }
        notInitialized = false;
    }

    /**
     * "Instalar" os dados na FF (populate) este método só é executado em ES e serve para
     * inicializar o Operator.
     */
    public static void setup() {
        try {
            pt.ist.anacom.SetupDomain.populateDomain();
        } catch (pt.ist.anacom.shared.exceptions.AnaComException e) {
            System.out.println("Error while populating phonebook application: " + e);
        }
    }
}
