package ch.keepcalm.demo.service.order

import io.jaegertracing.Configuration
import io.jaegertracing.internal.samplers.ConstSampler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux

@SpringBootApplication
class OrderServiceApplication

fun main(args: Array<String>) {
    runApplication<OrderServiceApplication>(*args)
}


@RestController
@RequestMapping("/api/v1/orders")
class OrderServiceResource(private val catalogServiceClient: CatalogServiceClient) {


    @GetMapping(path = ["/random"])
    fun getAnimalNames(@RequestHeader headers: HttpHeaders): Flux<String> {
        return catalogServiceClient.getRandomAnimalNames()
    }

}

@Service
class CatalogServiceClient(private val webClientBuilder: WebClient.Builder) {

    fun getRandomAnimalNames(): Flux<String> { // DEV - http://localhost:8081
        return this.webClientBuilder
                .baseUrl("http://cataloge-service:8080/api/v1/animals/random").build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToFlux(String::class.java)
                .log()
    }
}

@Component
class TracerConfiguration {

    @Bean
    fun jaegerTracer() = Configuration("order-service")
            .withSampler(Configuration.SamplerConfiguration
                    .fromEnv()
                    .withType(ConstSampler.TYPE)
                    .withParam(1))
            .withReporter(Configuration.ReporterConfiguration
                    .fromEnv()
                    .withLogSpans(true))
}

@Component
class OrderServiceConfiguration {

    @Bean
    fun webClientBuilder() = WebClient.builder()

}