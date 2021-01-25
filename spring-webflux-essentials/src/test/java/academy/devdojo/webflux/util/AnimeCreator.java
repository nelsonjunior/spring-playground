package academy.devdojo.webflux.util;

import academy.devdojo.webflux.domain.Anime;

public class AnimeCreator {

    public static Anime createAnimeBeToSaved(){
        return Anime.builder()
                .name("Dragon Ball")
                .build();
    }

    public static Anime createValidAnime(){
        return Anime.builder()
                .id(1)
                .name("Dragon Ball")
                .build();
    }

    public static Anime createUpdatedAnime(){
        return Anime.builder()
                .id(1)
                .name("Dragon Ball Super")
                .build();
    }

}
