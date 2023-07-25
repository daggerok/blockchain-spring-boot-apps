package io.github.daggerok.blockchain

import org.apache.logging.log4j.kotlin.logger
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.data.relational.core.mapping.Table
import reactor.kotlin.test.test
import java.time.Instant

@Table("blockchain")
data class Block(
    val previousHash: String = "",
    val timestamp: Instant? = null,
    val hash: String = "",
    @Id val id: Long? = null
)

interface BlockRepository : R2dbcRepository<Block, Long>

@SpringBootTest
class BlockTests(@Autowired val blockRepository: BlockRepository) {

    @BeforeEach
    fun `before each`() {
        blockRepository.deleteAll().subscribe()
    }

    @Test
    fun `should test block with r2dbc and liquibase`() {
        // given
        val previousHash = "Hello, World!"
            .chars().mapToObj { String.format("%02x", it) }
            .toArray().joinToString(separator = "")
        // and
        blockRepository.save(Block(previousHash = previousHash))
            .test()
            .expectNextCount(1)
            .verifyComplete()

        // when
        blockRepository.findAll()
            .test()
            // then
            .consumeNextWith(logger()::info)
            .verifyComplete()
    }
}
