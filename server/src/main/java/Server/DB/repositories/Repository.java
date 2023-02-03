package Server.DB.repositories;


import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NotUniqueException;
import Server.DB.exceptions.NullException;
import Server.services.exceptions.ServiceException;

import java.lang.reflect.Field;

public interface Repository {

    void insert(Object object) throws DBException, NullException, ServiceException, NotUniqueException;

    void update(Object object, Field uniqueField) throws NotFoundException, DBException, NullException, ServiceException, NotUniqueException;

    void delete(Object object, Field uniqueField) throws NotFoundException, DBException, NullException, ServiceException;

    void selectByUniqueField(Object object, Field uniqueField) throws NotFoundException, DBException, NullException, ServiceException;

    //----------------------------------------------------------

}
