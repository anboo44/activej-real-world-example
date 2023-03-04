package com.uet.example.repository;

import com.uet.example.domain.user.UserEntity;
import com.uet.example.domain.user.UserId;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;

import java.util.List;
import java.util.Optional;

// Shorthand for Optional class
class Opt {
    public static <T> Optional<T> nullable(T data) {
        return Optional.ofNullable(data);
    }
}

@Inject
public class UserRepository extends BaseRepository {

    //----------/ AREA: Public methods /------------//
    public Promise<List<UserEntity>> getAll() {
        return Promise.ofBlocking(executor, () -> selectAll(UserEntity.class));
    }

    public Promise<Void> create(UserEntity user) {
        return Promise.ofBlocking(executor, () -> save(user));
    }

    public Promise<Optional<UserEntity>> findById(UserId userId) {
        return Promise.ofBlocking(executor, () -> Opt.nullable(selectById(userId, UserEntity.class)));
    }
}
