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

@Table("hello")
data class Hello(
    val world: String = "",
    val timestamp: Instant? = null,
    @Id val id: Long? = null
)

interface HelloRepository : R2dbcRepository<Hello, Long>

@SpringBootTest
class HelloWorldTests(@Autowired val helloRepository: HelloRepository) {

    @BeforeEach
    fun `before each`() {
        helloRepository.deleteAll().subscribe()
    }

    @Test
    fun `should test context with r2dbc and liquibase`() {
        // given
        helloRepository.save(Hello(world = "Hello, World!"))
            .test()
            .expectNextCount(1)
            .verifyComplete()

        // when
        helloRepository.findAll()
            .test()
            // then
            .consumeNextWith(logger()::info)
            .verifyComplete()
    }
}
