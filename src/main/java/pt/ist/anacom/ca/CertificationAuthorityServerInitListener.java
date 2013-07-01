package pt.ist.anacom.ca;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class CertificationAuthorityServerInitListener
        implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        try {
            // ATENÇÃO: O NOME DA CA TEM REGRAS!!! VER JAVADOC DO MÉTODO
            String caName = arg0.getServletContext().getInitParameter("serverSecurityName");
            String keysDirectory = arg0.getServletContext().getInitParameter("keysDirectory");
            CertificationAuthorityWebService.init(caName, keysDirectory);
        } catch (Exception e) {
            System.out.println("ERROR: CertificationAuthorityServerInitListener: init error");
            e.printStackTrace();
        }
    }



}
