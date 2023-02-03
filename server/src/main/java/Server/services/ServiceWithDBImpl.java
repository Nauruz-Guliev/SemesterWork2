package Server.services;


import Server.DB.exceptions.DBException;
import Server.DB.exceptions.NotFoundException;
import Server.DB.exceptions.NotUniqueException;
import Server.DB.exceptions.NullException;
import Server.DB.repositories.Repository;
import Server.DB.repositories.RepositoryImpl;
import Server.services.exceptions.ServiceException;

import java.lang.reflect.Field;
import java.util.Collection;

public class ServiceWithDBImpl implements ServiceWithDB {

    protected RepositoryImpl repository;

    public ServiceWithDBImpl(Repository repository) {
        this.repository = (RepositoryImpl) repository;
    }

    //нет проверок внешнего ключа

    @Override
    public void add(Object object) throws NotUniqueException, NullException, ServiceException, DBException {
        repository.insert(object);
    }
    @Override
    public void add(Collection<Object> objects) throws NotUniqueException, NullException, ServiceException, DBException {
        for (Object object : objects) {
            add(object);
        }
    }

    @Override
    public void change(Object object, String uniqueFieldName) throws NotUniqueException, NullException, ServiceException, NotFoundException, DBException {
        repository.update(object, getField(object, uniqueFieldName));
    }
    @Override
    public void change(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, DBException, NullException, NotUniqueException, ServiceException {
        for (Object object : objects) {
            change(object, uniqueFieldName);
        }
    }

    @Override
    public void delete(Object object, String uniqueFieldName) throws NotFoundException, ServiceException, DBException, NullException {
        repository.delete(object, getField(object, uniqueFieldName));
    }
    @Override
    public void delete(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, ServiceException, DBException, NullException {
        for (Object object : objects) {
            delete(object, uniqueFieldName);
        }
    }


    @Override
    public void getByUniqueField(Object object, String uniqueFieldName) throws NotFoundException, ServiceException, DBException, NullException {
        repository.selectByUniqueField(object, getField(object, uniqueFieldName));
    }
    @Override
    public void getByUniqueField(Collection<Object> objects, String uniqueFieldName) throws NotFoundException, DBException, ServiceException, NullException {
        for (Object object : objects) {
            getByUniqueField(object, uniqueFieldName);
        }
    }

    //------------------------------------------------------------

    /** Получить Field по его имени */
    protected static Field getField(Object object, String uniqueFieldName) throws ServiceException {
        Field uniqueField;
        try {
            uniqueField = object.getClass().getDeclaredField(uniqueFieldName);
        } catch (NoSuchFieldException e) {
            throw new ServiceException("Не найдено поле " + uniqueFieldName + " у класса " + object.getClass());
        } catch (NullPointerException e) {
            throw new ServiceException("Объект пустой");
        }
        return uniqueField;
    }

}
