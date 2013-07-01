package pt.ist.anacom;

import pt.ist.anacom.domain.NetworkManager;
import pt.ist.anacom.service.CreateOperatorService;
import pt.ist.anacom.shared.dto.operator.OperatorDetailedDTO;
import pt.ist.fenixframework.Config;
import pt.ist.fenixframework.Config.RepositoryType;
import pt.ist.fenixframework.FenixFramework;

public class SetupDomain {
    public static void main(String[] args) {
        FenixFramework.initialize(new Config() {
            {
                dbAlias = "/tmp/db/local";
                domainModelPath = "src/main/dml/anacom.dml";
                repositoryType = RepositoryType.BERKELEYDB;
                rootClass = NetworkManager.class;
            }
        });
        populateDomain();
    }

    /**
     * Populate do dominio. Utiliza os serviços e DTO's
     */
    public static void populateDomain() {
        final OperatorDetailedDTO dto1 = new OperatorDetailedDTO("Red", "91", 10, 5, 9, 1.2, 0.1);
        final OperatorDetailedDTO dto2 = new OperatorDetailedDTO("Orange", "93", 10, 2, 4, 1.2, 0.0);
        //final OperatorDetailedDTO dto3 = new OperatorDetailedDTO("Vodafone", "91", 5, 5, 5, 1.2, 0.5);

        final CreateOperatorService serv1 = new CreateOperatorService(dto1);
        final CreateOperatorService serv2 = new CreateOperatorService(dto2);
        //final CreateOperatorService serv3 = new CreateOperatorService(dto3);

        serv1.execute();
        serv2.execute();
        //serv3.execute();
    }
}
