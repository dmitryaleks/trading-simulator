package com.rest.util;

import com.rest.model.Instrument;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.List;

public class InstrumentManager {

    static public class InstrumentLookupException extends Exception {

        public InstrumentLookupException(final String msg) {
            super(msg);
        }

    }

    public static Instrument getInstrument(final String instrCode) throws InstrumentLookupException {

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        final String getInstrHQL = String.format("SELECT I FROM Instrument I WHERE I.name = '%s'", instrCode);
        Query query = session.createQuery(getInstrHQL);
        List<Instrument> instruments = query.list();
        session.close();

        if(instruments.size() == 0) {
            throw new InstrumentLookupException(String.format("Instrument %s not found", instrCode));
        }

        if(instruments.size() > 1) {
            throw new InstrumentLookupException(String.format("Ambiguous instrument code: %s", instrCode));
        }

        com.rest.model.Instrument inst = instruments.get(0);
        return inst;
    }

    public static List<Instrument> getAllInstruments() throws InstrumentLookupException {

        SessionFactory sessionFactory = new Configuration().configure()
                .buildSessionFactory();
        Session session = sessionFactory.openSession();
        final String getInstrHQL = "SELECT I FROM Instrument I";
        Query query = session.createQuery(getInstrHQL);
        List<Instrument> instruments = query.list();
        session.close();
        if(instruments.size() == 0) {
            throw new InstrumentLookupException(String.format("No instruments found"));
        }
        return instruments;
    }
}
