package com.papay.themoviedb.core.data.movie

import com.papay.themoviedb.core.database.movie.MovieEntity
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.model.Movie
import com.papay.themoviedb.core.model.MovieDetail
import com.papay.themoviedb.core.model.MoviePage
import com.papay.themoviedb.core.model.MovieReview
import com.papay.themoviedb.core.model.MovieReviewPage
import com.papay.themoviedb.core.model.MovieVideo
import com.papay.themoviedb.core.model.SpokenLanguage
import com.papay.themoviedb.core.network.genre.GenreDto
import com.papay.themoviedb.core.network.movie.AuthorDetailsDto
import com.papay.themoviedb.core.network.movie.MovieDetailDto
import com.papay.themoviedb.core.network.movie.MovieDto
import com.papay.themoviedb.core.network.movie.MovieResponseDto
import com.papay.themoviedb.core.network.movie.MovieReviewDto
import com.papay.themoviedb.core.network.movie.MovieReviewResponseDto
import com.papay.themoviedb.core.network.movie.MovieVideoDto
import com.papay.themoviedb.core.network.movie.SpokenLanguageDto
import org.junit.Assert.assertEquals
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `maps movie entity to model`() {
        val entity = MovieEntity(
            genreId = 28,
            id = 1,
            title = "Inception",
            posterPath = "/poster.jpg",
            releaseDate = "2010-07-16"
        )

        val result = entity.toModel()

        assertEquals(Movie(id = 1, title = "Inception", posterPath = "/poster.jpg", releaseDate = "2010-07-16"), result)
    }

    @Test
    fun `maps movie dto to model`() {
        val dto = MovieDto(
            id = 2,
            title = "Interstellar",
            posterPath = "/interstellar.jpg",
            releaseDate = "2014-11-07"
        )

        val result = dto.toModel()

        assertEquals(
            Movie(id = 2, title = "Interstellar", posterPath = "/interstellar.jpg", releaseDate = "2014-11-07"),
            result
        )
    }

    @Test
    fun `maps movie response dto to model`() {
        val dto = MovieResponseDto(
            page = 2,
            totalPages = 10,
            results = listOf(MovieDto(id = 3, title = "Tenet", posterPath = null, releaseDate = null))
        )

        val result = dto.toModel()

        assertEquals(
            MoviePage(
                page = 2,
                totalPages = 10,
                movies = listOf(Movie(id = 3, title = "Tenet", posterPath = null, releaseDate = null))
            ),
            result
        )
    }

    @Test
    fun `maps movie detail dto to model with defaults`() {
        val dto = MovieDetailDto(
            id = 4,
            title = "Dune",
            overview = "A noble family becomes embroiled in a war.",
            releaseDate = "2021-10-22",
            genres = listOf(GenreDto(id = 878, name = "Science Fiction")),
            spokenLanguages = listOf(
                SpokenLanguageDto(englishName = "English", name = "English"),
                SpokenLanguageDto(englishName = null, name = "Deutsch"),
                SpokenLanguageDto(englishName = null, name = null)
            ),
            voteAverage = null,
            voteCount = null
        )

        val result = dto.toModel()

        assertEquals(
            MovieDetail(
                id = 4,
                title = "Dune",
                overview = "A noble family becomes embroiled in a war.",
                releaseDate = "2021-10-22",
                genres = listOf(Genre(id = 878, name = "Science Fiction")),
                spokenLanguages = listOf(
                    SpokenLanguage(name = "English"),
                    SpokenLanguage(name = "Deutsch"),
                    SpokenLanguage(name = "")
                ),
                voteAverage = 0.0,
                voteCount = 0
            ),
            result
        )
    }

    @Test
    fun `maps movie review response dto to model`() {
        val dto = MovieReviewResponseDto(
            page = 1,
            totalPages = 3,
            totalResults = 24,
            results = listOf(
                MovieReviewDto(
                    id = "review-1",
                    author = "Papay",
                    content = "Great movie.",
                    createdAt = "2026-06-04T09:00:00Z",
                    authorDetails = AuthorDetailsDto(rating = 8.5)
                )
            )
        )

        val result = dto.toModel()

        assertEquals(
            MovieReviewPage(
                reviews = listOf(
                    MovieReview(
                        id = "review-1",
                        author = "Papay",
                        content = "Great movie.",
                        createdAt = "2026-06-04T09:00:00Z",
                        rating = 8.5
                    )
                ),
                page = 1,
                totalPages = 3,
                totalResults = 24
            ),
            result
        )
    }

    @Test
    fun `maps movie video dto to model`() {
        val dto = MovieVideoDto(
            key = "abc123",
            name = "Official Trailer",
            site = "YouTube",
            type = "Trailer"
        )

        val result = dto.toModel()

        assertEquals(
            MovieVideo(key = "abc123", name = "Official Trailer", site = "YouTube", type = "Trailer"),
            result
        )
    }

    @Test
    fun `maps movie model to entity with genre id`() {
        val movie = Movie(
            id = 5,
            title = "The Dark Knight",
            posterPath = "/dark-knight.jpg",
            releaseDate = "2008-07-18"
        )

        val result = movie.toEntity(genreId = 28)

        assertEquals(
            MovieEntity(
                genreId = 28,
                id = 5,
                title = "The Dark Knight",
                posterPath = "/dark-knight.jpg",
                releaseDate = "2008-07-18"
            ),
            result
        )
    }
}
