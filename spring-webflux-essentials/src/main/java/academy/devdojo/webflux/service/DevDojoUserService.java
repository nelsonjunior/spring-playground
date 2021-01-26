package academy.devdojo.webflux.service;

import academy.devdojo.webflux.repository.DevDojoUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DevDojoUserService implements ReactiveUserDetailsService {

    private final DevDojoUserRepository devDojoUserRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return devDojoUserRepository.findByUsername(username)
                .cast(UserDetails.class);
    }
}
