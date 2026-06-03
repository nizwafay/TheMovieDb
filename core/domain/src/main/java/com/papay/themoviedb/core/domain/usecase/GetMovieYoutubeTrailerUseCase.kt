package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.MovieVideo

class GetMovieYoutubeTrailerUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieVideo? {
        return movieRepository.getMovieVideos(movieId = movieId)
            .firstOrNull { video ->
                video.site.equals(YouTubeSite, ignoreCase = true) &&
                    video.type.equals(TrailerType, ignoreCase = true)
            }
    }

    private companion object {
        const val YouTubeSite = "YouTube"
        const val TrailerType = "Trailer"
    }
}
