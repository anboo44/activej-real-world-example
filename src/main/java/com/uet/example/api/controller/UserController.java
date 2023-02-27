package com.uet.example.api.controller;

import com.uet.example.api.record.dto.UserDTO;
import com.uet.example.api.record.vo.UserVO;
import com.uet.example.domain.user.UserId;
import com.uet.example.repository.UserRepository;
import com.uet.example.util.exception.EntityNotFoundException;
import com.uet.example.util.exception.WrongReqException;
import io.activej.http.HttpCookie;
import io.activej.http.HttpRequest;
import io.activej.http.HttpResponse;
import io.activej.http.session.SessionStore;
import io.activej.inject.annotation.Inject;
import io.activej.promise.Promise;
import lombok.SneakyThrows;

import java.util.UUID;

import static com.uet.example.util.StrConst.SESSION_ID;
import static java.lang.Integer.parseInt;

@Inject
public class UserController extends BaseController {

    private @Inject UserRepository       userRepository;
    private @Inject SessionStore<String> sessionStore;

    public Promise<HttpResponse> login(HttpRequest request) {
        String sessionId = UUID.randomUUID().toString();

        return sessionStore.save(sessionId, "My object saved in session")
                           .map($ -> HttpResponse.ofCode(201)
                                                 .withCookie(HttpCookie.of(SESSION_ID, sessionId)));
    }

    @SneakyThrows
    public Promise<HttpResponse> list(HttpRequest request) {
        return userRepository.getAll().map(
            records -> records.stream().map(UserDTO::apply).toList()
        ).map(this::success);
    }

    public Promise<HttpResponse> register(HttpRequest request) {
        return request.loadBody().map($ -> {
            UserVO vo = parseJson(request, UserVO.class);

            var errLst = vo.validate();
            if (!errLst.isEmpty()) { throw new WrongReqException(errLst); }

            return vo.toEntity();
        }).then(
            entity -> userRepository.create(entity).map($ -> HttpResponse.ofCode(201))
        );
    }

    public Promise<HttpResponse> getById(HttpRequest request) {
        UserId userId = new UserId(
            parseInt(request.getPathParameter("userId"))
        );

        return userRepository.findById(userId)
                             .map(userOpt -> {
                                 if (userOpt.isEmpty()) {
                                     throw new EntityNotFoundException("Not found user with id: " + userId.getValue());
                                 }

                                 return success(UserDTO.apply(userOpt.get()));
                             });
    }
}
