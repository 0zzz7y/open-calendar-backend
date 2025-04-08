package com.tomaszwnuk.dailyassistant.note

import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_NUMBER
import com.tomaszwnuk.dailyassistant.TestConstants.PAGEABLE_PAGE_SIZE
import com.tomaszwnuk.dailyassistant.category.Category
import com.tomaszwnuk.dailyassistant.category.CategoryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.util.*
import kotlin.test.assertEquals

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NoteServiceTest {

    @Mock
    private lateinit var _noteRepository: NoteRepository

    @Mock
    private lateinit var _categoryRepository: CategoryRepository

    @InjectMocks
    private lateinit var _noteService: NoteService

    private lateinit var _sampleCategory: Category

    private lateinit var _sampleNote: Note

    private lateinit var _sampleDto: NoteDto

    private lateinit var _pageable: Pageable

    @BeforeEach
    fun setup() {
        _sampleCategory = Category(name = "Shopping List")
        _sampleNote = Note(
            id = UUID.randomUUID(),
            name = "Groceries",
            description = "Buy milk, eggs, and bread",
            category = _sampleCategory
        )
        _sampleDto = _sampleNote.toDto()
        _pageable = PageRequest.of(PAGEABLE_PAGE_NUMBER, PAGEABLE_PAGE_SIZE)
    }

    @Test
    fun `should return created note`() {
        whenever(_categoryRepository.findById(_sampleDto.categoryId!!)).thenReturn(Optional.of(_sampleCategory))
        doReturn(_sampleNote).whenever(_noteRepository).save(any())
        val result: Note = _noteService.create(_sampleDto)

        assertEquals(_sampleNote.id, result.id)
        verify(_noteRepository).save(any())
    }


    @Test

    @Test

    @Test

    @Test









}