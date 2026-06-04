package com.papay.themoviedb.core.data.genre

import com.papay.themoviedb.core.database.genre.GenreEntity
import com.papay.themoviedb.core.model.Genre
import com.papay.themoviedb.core.network.genre.GenreDto
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreMappersTest {

    @Test
    fun `maps genre entity to model`() {
        val entity = GenreEntity(id = 28, name = "Action")

        val result = entity.toModel()

        assertEquals(Genre(id = 28, name = "Action"), result)
    }

    @Test
    fun `maps genre dto to model`() {
        val dto = GenreDto(id = 35, name = "Comedy")

        val result = dto.toModel()

        assertEquals(Genre(id = 35, name = "Comedy"), result)
    }

    @Test
    fun `maps genre dto to entity`() {
        val dto = GenreDto(id = 18, name = "Drama")

        val result = dto.toEntity()

        assertEquals(GenreEntity(id = 18, name = "Drama"), result)
    }

    @Test
    fun `maps genre model to entity`() {
        val genre = Genre(id = 27, name = "Horror")

        val result = genre.toEntity()

        assertEquals(GenreEntity(id = 27, name = "Horror"), result)
    }
}
