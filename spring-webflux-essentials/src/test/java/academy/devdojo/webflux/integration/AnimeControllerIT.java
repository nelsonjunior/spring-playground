package academy.devdojo.webflux.integration;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.repository.AnimeRepository;
import academy.devdojo.webflux.util.AnimeCreator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
/*
Opção para realizar teste apenas sem o contexto completo do spring
@WebFluxTest
@Import({AnimeService.class, CustomAttributes.class})
*/
public class AnimeControllerIT {

    private final static String REGULAR_USER = "user";

    private final static String ADMIN_USER = "nelson";

    @MockBean
    private AnimeRepository animeRepositoryMock;

    @Autowired
    private WebTestClient client;

    private final Anime anime = AnimeCreator.createValidAnime();

    @BeforeAll
    public static void blockHoundSetup() {
        BlockHound.install(builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
    }

    @Test
    public void blockHoundWorks() {
        try {
            FutureTask<?> task = new FutureTask<>(() -> {
                Thread.sleep(0);
                return "";
            });
            Schedulers.parallel().schedule(task);

            task.get(10, TimeUnit.SECONDS);
            Assertions.fail("should fail");
        } catch (Exception e) {
            Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
        }
    }

    @BeforeEach
    public void setUp() {

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(Flux.just(anime));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
                .thenReturn(Mono.just(anime));

        BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(anime, anime));

        BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
                .thenReturn(Mono.empty());

    }

    @Test
    @DisplayName("findAll returns a flux of anime when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void findAll_ReturnFluxOfAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo(anime.getId())
            .jsonPath("$.[0].name").isEqualTo(anime.getName());
    }

    @Test
    @DisplayName("findAll returns forbidden when user is successfully authenticated and does not have role ADMIN")
    @WithUserDetails(REGULAR_USER)
    public void findAll_ReturnForbidden_WhenUserDoesNotHaveHoleAdmin() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().isForbidden();
    }

    @Test
    @DisplayName("findAll returns unauthorized when user is not authenticated")
    public void findAll_ReturnUnauthorized_WhenUserIsNotAuthenticated() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("findAll returns a flux of anime when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void findAll_Flavor2_ReturnFluxOfAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes")
            .exchange()
            .expectStatus().is2xxSuccessful()
            .expectBodyList(Anime.class)
            .hasSize(1)
            .contains(anime);
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists when is successfully authenticated and has role USER")
    @WithUserDetails(REGULAR_USER)
    public void findById_ReturnMonoAnime_WhenSuccessful() {
        client
            .get()
            .uri("/animes/{id}", 3)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Anime.class).isEqualTo(anime);
    }

    @Test
    @DisplayName("findById returns Mono with anime when it exists and user is successfully authenticated and has role USER")
    @WithUserDetails(REGULAR_USER)
    public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        client
            .get()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");

    }

    @Test
    @DisplayName("delete removes the anime when successful when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void delete_RemovesAnime_WhenSuccessful() {

        client
            .delete()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNoContent()
            .expectBody()
            .isEmpty();
    }

    @Test
    @DisplayName("delete returns Mono error when does not exists when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void delete_ReturnMonoErro_WhenEmptyMonoIsReturned() {

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        client
            .delete()
            .uri("/animes/{id}", 1)
            .exchange()
            .expectStatus().isNotFound()
            .expectBody()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("save creates an anime when successful when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void save_CreatesAnime_WhenSuccessful() {

        client
            .post()
            .uri("/animes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(AnimeCreator.createAnimeToBeSaved())
            .exchange()
            .expectStatus().isCreated()
            .expectBody(Anime.class).isEqualTo(anime);

    }

    @Test
    @DisplayName("save returns mono error with bad request when name is empty when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void save_ReturnError_WhenNameIsEmpty() {

        client
            .post()
            .uri("/animes")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(AnimeCreator.createValidAnime().withName(""))
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody()
            .jsonPath("$.status").isEqualTo(400)
            .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("saveBatch creates a list of anime when successful when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void saveBatch_CreatesListOfAnimes_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

        client
            .post()
            .uri("/animes/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
            .exchange()
            .expectStatus().isCreated()
            .expectBodyList(Anime.class)
            .hasSize(2)
            .contains(anime);
    }

    @Test
    @DisplayName("saveBatch returns Mono error when of the objects in the list contains null or empty name when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void saveBatch_ReturnsMonoErro_WhenContainsInvalidName() {

        Anime animeEmptyName = AnimeCreator.createValidAnime().withName("");

        BDDMockito.when(animeRepositoryMock.saveAll(ArgumentMatchers.anyIterable()))
                .thenReturn(Flux.just(animeEmptyName));

        client
                .post()
                .uri("/animes/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(List.of(animeEmptyName)))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("update save updated anime and returns empty mono when successful when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void update_SaveUpdatedAnime_WhenSuccessful() {

        BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createUpdatedAnime()))
                .thenReturn(Mono.just(anime));

        client
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(AnimeCreator.createUpdatedAnime()))
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();

    }

    @Test
    @DisplayName("update returns mono error with bad request when name is empty when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void update_ReturnError_WhenNameIsEmpty() {

        client
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(AnimeCreator.createUpdatedAnime().withName(""))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }

    @Test
    @DisplayName("update returns Mono error when does not exists when is successfully authenticated and has role ADMIN")
    @WithUserDetails(ADMIN_USER)
    public void update_ReturnMonoErro_WhenEmptyMonoIsReturned() {

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
                .thenReturn(Mono.empty());

        client
                .put()
                .uri("/animes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(AnimeCreator.createUpdatedAnime()))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException Happened");
    }
}