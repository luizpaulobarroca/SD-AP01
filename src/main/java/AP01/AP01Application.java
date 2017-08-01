package AP01;

import AP01.dao.UniversityDAO;
import AP01.model.University;
import AP01.resources.UniversityResource;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.joda.time.DateTimeZone;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import AP01.AP01Configuration;

public class AP01Application extends io.dropwizard.Application<AP01Configuration>  {

    private static final Logger LOGGER = LoggerFactory.getLogger(AP01Application.class);
    private HibernateBundle<AP01Configuration> hibernate = new HibernateBundle<AP01Configuration>(University.class, getEntities()) {
        @Override
        public DataSourceFactory getDataSourceFactory(AP01Configuration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    public static void main(String[] args) throws Exception {
        new AP01Application().run(new String[]{"server", "config.yaml"});
    }

    @SuppressWarnings("rawtypes")
    private Class[] getEntities() { //<-- Isso aqui é para o hibernate procurar todas as classes que tem no pacote modelo, para ele criar as tabelas
        List<ClassLoader> classLoadersList = new LinkedList();
        classLoadersList.add(ClasspathHelper.contextClassLoader());
        classLoadersList.add(ClasspathHelper.staticClassLoader());

        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setScanners(new SubTypesScanner(false), new ResourcesScanner(), new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forClassLoader(classLoadersList.toArray(new ClassLoader[0])))
                .filterInputsBy(new FilterBuilder().include(FilterBuilder.prefix("iFace.model"))));

        Set<Class<? extends Object>> allClasses
                = reflections.getTypesAnnotatedWith(Entity.class);
        allClasses.remove(University.class);
        return allClasses.toArray(new Class[allClasses.size()]);
    }

    @Override
    public void initialize(Bootstrap<AP01Configuration> b) {
        DateTimeZone.setDefault(DateTimeZone.UTC);
        b.addBundle(hibernate);
    }

    @Override
    public void run(AP01Configuration c, Environment e) throws Exception {

        final FilterRegistration.Dynamic cors
                = e.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        LOGGER.info("Method Application#run() called");

        //Isso aqui é para dizer que tem serviços na classe UserResource e ele utilizará o dao UserDAO
        final UniversityDAO userDao = new UniversityDAO(hibernate.getSessionFactory());

        e.jersey().register(new UniversityResource(userDao));


//        e.jersey().register(HttpSessionProvider.class);
//        e.servlets().setSessionHandler(new SessionHandler());

    }
}
