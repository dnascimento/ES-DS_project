Final release of Software Engineering and Distributed Systems courses' project at IST (2011/2012)

Authors:
- Gonçalo Pestana (me)
- David Dias ()
- Artur Balanuta ()
- Dário Nascimento ()
- Karl Kristen ()

Used Technologies:
- Java
- Ant/XML
- JBoss applicational server
- Webservices (UDDI, WSDL, SOAP,..)
- Google Web Toolkit (UX)
- Asymethric Public-key protocol (implemented by us)
- Scrum 

Software Engineering description:
 Main goals: Model and implement the business logic of a fictional telecom manager and its clients. Implement the model in a WAF structure (considering Presentation, Services, Domain, Persistent Store layers), considering as many design patterns as possible and required. Black and white tests batteries.
 

Distributed Systems description:
 Main goals: Distribute the implemented telecom servers with given non-functional requirements regarding replication and security, namely:
 	i)   Replication of the server using UDDI framework to register and locate services;
 	i)   Tolerance to both silent and byzantine faults;
 	ii)  Asymethric Public-key protocol to ensure secure connection between server and clients (identity and authenticity insured); Cretificate handling, including revogation.
 	iii) Server multithreading (server is able to receive multiple requests from multiple clients)

Final notes:
- Project done in roughly 3 months
- Final grade: 19/20
