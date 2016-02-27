package ru.ifmo.practice.seabattle.db;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class HibernateUtil {
    private SessionFactory sessionFactory = null;

    private static class HibernateUtilHolder {
        private static HibernateUtil instance;

        static {
            try {
                instance = new HibernateUtil();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public static HibernateUtil getInstance() {
        return HibernateUtilHolder.instance;
    }

    private HibernateUtil() {
        setUp();
    }

    //Поднимает фабрику, которая создает сессии для доступа к БД
    private void setUp() {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            e.printStackTrace();
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void shutdown() {
        getSessionFactory().close();
    }
}
