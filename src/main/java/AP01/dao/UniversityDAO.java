package AP01.dao;

import AP01.model.University;
import org.hibernate.SessionFactory;

public class UniversityDAO extends MeuDAO<University> {
    public UniversityDAO(SessionFactory factory) {
        super(factory);
    }
}
