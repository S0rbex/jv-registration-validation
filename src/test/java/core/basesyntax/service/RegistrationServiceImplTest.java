package core.basesyntax.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import core.basesyntax.dao.StorageDao;
import core.basesyntax.exception.UserRegisterException;
import core.basesyntax.model.User;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegistrationServiceImplTest {

    private static class FakeStorageDao implements StorageDao {
        private final Map<String, User> storage = new HashMap<>();

        @Override
        public User add(User user) {
            storage.put(user.getLogin(), user);
            return user;
        }

        @Override
        public User get(String login) {
            return storage.get(login);
        }

        public int size() {
            return storage.size();
        }
    }

    private RegistrationService registrationService;
    private FakeStorageDao fakeDao;
    private User validUser;

    @BeforeEach
    void setUp() {
        fakeDao = new FakeStorageDao();
        registrationService = new RegistrationServiceImpl(fakeDao);

        validUser = new User();
        validUser.setLogin("validLogin");
        validUser.setPassword("validPassword");
        validUser.setAge(20);
    }

    @Test
    void register_Success_ShouldReturnAndStoreUser() {
        User registeredUser = registrationService.register(validUser);

        assertNotNull(registeredUser);
        assertEquals("validLogin", registeredUser.getLogin());

        assertEquals(1, fakeDao.size());
        assertEquals(validUser, fakeDao.get("validLogin"));
    }

    @Test
    void register_UserIsNull_ShouldThrowException() {
        UserRegisterException exception = assertThrows(
                UserRegisterException.class,
                () -> registrationService.register(null)
        );

        assertEquals("User cannot be null", exception.getMessage());
        assertEquals(0, fakeDao.size()); // Переконуємось, що нічого не додали
    }

    @Test
    void register_LoginAlreadyExists_ShouldThrowException() {
        fakeDao.add(validUser);

        User duplicateUser = new User();
        duplicateUser.setLogin("validLogin");
        duplicateUser.setPassword("anotherPass");
        duplicateUser.setAge(30);

        UserRegisterException exception = assertThrows(
                UserRegisterException.class,
                () -> registrationService.register(duplicateUser)
        );

        assertEquals("User with login " + validUser.getLogin() + " already exists",
                exception.getMessage());

        assertEquals(1, fakeDao.size());
    }

    @Test
    void register_LoginIsTooShort_ShouldThrowException() {
        validUser.setLogin("short");

        UserRegisterException exception = assertThrows(
                UserRegisterException.class,
                () -> registrationService.register(validUser)
        );

        assertEquals("Username too short", exception.getMessage());
        assertEquals(0, fakeDao.size());
    }

    @Test
    void register_PasswordIsTooShort_ShouldThrowException() {
        validUser.setPassword("12345");

        UserRegisterException exception = assertThrows(
                UserRegisterException.class,
                () -> registrationService.register(validUser)
        );

        assertEquals("Password too short", exception.getMessage());
        assertEquals(0, fakeDao.size());
    }

    @Test
    void register_AgeIsTooYoung_ShouldThrowException() {
        validUser.setAge(17);

        UserRegisterException exception = assertThrows(
                UserRegisterException.class,
                () -> registrationService.register(validUser)
        );

        assertEquals("Age is too young", exception.getMessage());
        assertEquals(0, fakeDao.size());
    }
}
