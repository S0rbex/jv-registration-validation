package core.basesyntax.service;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.exception.UserRegisterException;
import core.basesyntax.model.User;

public class RegistrationServiceImpl implements RegistrationService {
    private static final int MIN_LENGTH = 6;
    private static final int MIN_AGE = 18;

    private final StorageDao storageDao;

    public RegistrationServiceImpl(StorageDao storageDao) {
        this.storageDao = storageDao;
    }

    @Override
    public User register(User user) {
        if (user == null) {
            throw new UserRegisterException("User cannot be null");
        }
        String checkLogin = user.getLogin();
        if (storageDao.get(checkLogin) != null) {
            throw new UserRegisterException("User with login "
                    + user.getLogin() + " already exists");
        }
        if (checkLogin.length() < MIN_LENGTH) {
            throw new UserRegisterException("Username too short");
        }
        if (user.getPassword() == null || user.getPassword().length() < MIN_LENGTH) {
            throw new UserRegisterException("Password too short");
        }
        if (user.getAge() == null || user.getAge() < MIN_AGE) {
            throw new UserRegisterException("Age is too young");
        }
        return storageDao.add(user);
    }
}
