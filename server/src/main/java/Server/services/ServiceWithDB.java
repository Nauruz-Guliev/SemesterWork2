package Server.services;

import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NotUniqueException;
import Server.DB.exceptions.NullException;
import Server.services.exceptions.ServiceException;

import java.util.Collection;

public interface ServiceWithDB {

    void add(Object object) throws NotUniqueException, DBException, NullException, ServiceException;
    void add(Collection<Object> objects) throws NotUniqueException, DBException, NullException, ServiceException;

    void change(Object object, String uniqueFieldName) throws NotFoundException, DBException, NullException, NotUniqueException, ServiceException;
    void change(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, DBException, NullException, NotUniqueException, ServiceException;

    void delete(Object object, String uniqueFieldName) throws NotFoundException, DBException, NullException, ServiceException;
    void delete(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, DBException, NullException, ServiceException;

    void getByUniqueField(Object object, String uniqueFieldName) throws NotFoundException, DBException, NullException, ServiceException;
    void getByUniqueField(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, DBException, NullException, ServiceException;


}
