package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    @Override
    public User save(User user) {
        if (emails.contains(user.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + user.getEmail());
        }

        if (user.getId() == null) {
            user.setId(idCounter.getAndIncrement());
        }

        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteById(Long id) {
        User user = users.remove(id);
        if (user != null) {
            emails.remove(user.getEmail());
        }
    }

    @Override
    public boolean emailExists(String email) {
        return emails.contains(email);
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}