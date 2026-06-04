package com.papay.themoviedb.core.domain.usecase

import com.papay.themoviedb.core.domain.repository.MovieRepository
import com.papay.themoviedb.core.model.MovieVideo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GetMovieYoutubeTrailerUseCaseTest {
    private val movieRepository = mockk<MovieRepository>()
    private val getMovieYoutubeTrailer = GetMovieYoutubeTrailerUseCase(movieRepository = movieRepository)

    @Test
    fun `invoke returns first YouTube trailer from repository videos`() = runTest {
        coEvery { movieRepository.getMovieVideos(MovieId) } returns listOf(
            Featurette,
            VimeoTrailer,
            YoutubeTrailer,
            SecondYoutubeTrailer
        )

        val result = getMovieYoutubeTrailer(movieId = MovieId)

        assertEquals(YoutubeTrailer, result)
        coVerify(exactly = 1) { movieRepository.getMovieVideos(MovieId) }
    }

    @Test
    fun `invoke matches site and type case insensitively`() = runTest {
        val lowercaseYoutubeTrailer = MovieVideo(
            key = "lowercase-trailer",
            name = "Lowercase Trailer",
            site = "youtube",
            type = "trailer"
        )
        coEvery { movieRepository.getMovieVideos(MovieId) } returns listOf(lowercaseYoutubeTrailer)

        val result = getMovieYoutubeTrailer(movieId = MovieId)

        assertEquals(lowercaseYoutubeTrailer, result)
        coVerify(exactly = 1) { movieRepository.getMovieVideos(MovieId) }
    }

    @Test
    fun `invoke returns null when no YouTube trailer exists`() = runTest {
        coEvery { movieRepository.getMovieVideos(MovieId) } returns listOf(
            Featurette,
            VimeoTrailer
        )

        val result = getMovieYoutubeTrailer(movieId = MovieId)

        assertNull(result)
        coVerify(exactly = 1) { movieRepository.getMovieVideos(MovieId) }
    }

    @Test
    fun `invoke returns null when repository videos are empty`() = runTest {
        coEvery { movieRepository.getMovieVideos(MovieId) } returns emptyList()

        val result = getMovieYoutubeTrailer(movieId = MovieId)

        assertNull(result)
        coVerify(exactly = 1) { movieRepository.getMovieVideos(MovieId) }
    }

    private companion object {
        const val MovieId = 550

        val Featurette = MovieVideo(
            key = "featurette",
            name = "Featurette",
            site = "YouTube",
            type = "Featurette"
        )

        val VimeoTrailer = MovieVideo(
            key = "vimeo-trailer",
            name = "Vimeo Trailer",
            site = "Vimeo",
            type = "Trailer"
        )

        val YoutubeTrailer = MovieVideo(
            key = "youtube-trailer",
            name = "YouTube Trailer",
            site = "YouTube",
            type = "Trailer"
        )

        val SecondYoutubeTrailer = MovieVideo(
            key = "second-youtube-trailer",
            name = "Second YouTube Trailer",
            site = "YouTube",
            type = "Trailer"
        )
    }
}
