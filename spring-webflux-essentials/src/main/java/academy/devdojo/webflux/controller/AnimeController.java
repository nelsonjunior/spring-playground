package academy.devdojo.webflux.controller;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.repository.AnimeRepository;
import academy.devdojo.webflux.service.AnimeService;
import ch.qos.logback.classic.boolex.JaninoEventEvaluator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jdk.jshell.Snippet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.text.MessageFormat;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("animes")
@Slf4j
@SecurityScheme(
        name = "Basic Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "basic"
)
public class AnimeController {

    private final AnimeService animeService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all animes",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Flux<Anime> listAll(){
        log.info("list all anime!");
        return animeService.findAll();
    }

    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get anime by id",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Mono<Anime> listById(@PathVariable int id){
        log.info(MessageFormat.format("list anime by id {0}!", id));
        return animeService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Mono<Anime> save(@Valid @RequestBody Anime anime){
        log.info("Save anime!");
        return animeService.save(anime);
    }

    @PostMapping("batch")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save animes with batch",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Flux<Anime> saveBatch(@RequestBody List<Anime> animes){
        log.info("Save anime!");
        return animeService.saveAll(animes);
    }

    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Update anime",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Mono<Void> update(@PathVariable int id, @Valid @RequestBody Anime anime){
        log.info("Update anime!");
        return animeService.update(anime.withId(id));
    }

    @DeleteMapping(path = "{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete anime by id",
            security = @SecurityRequirement(name = "Basic Authentication"),
            tags = {"animes"})
    public Mono<Void> delete(@PathVariable int id){
        log.info("Delete anime!");
        return animeService.delete(id);
    }
}
