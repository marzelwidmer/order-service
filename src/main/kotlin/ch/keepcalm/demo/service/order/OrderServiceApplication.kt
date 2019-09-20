package ch.keepcalm.demo.service.order

import io.jaegertracing.internal.samplers.ConstSampler
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.boot.runApplication
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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
import springfox.documentation.builders.ApiInfoBuilder
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.util.*

@EnableDiscoveryClient
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

    companion object {
        val CATALOG_SERVICE_URL = "http://catalog-service/api/v1/animals/random"
    }

    fun getRandomAnimalNames(): Flux<String> {
        return this.webClientBuilder
                .baseUrl(CATALOG_SERVICE_URL).build()
                .get()
                .accept(MediaType.APPLICATION_JSON)
                .retrieve().bodyToFlux(String::class.java)
                .log()
    }
}

@Component
class TracerConfiguration {

    @Bean
    fun jaegerTracer(): io.jaegertracing.Configuration = io.jaegertracing.Configuration("order-service")
            .withSampler(io.jaegertracing.Configuration.SamplerConfiguration
                    .fromEnv()
                    .withType(ConstSampler.TYPE)
                    .withParam(1))
            .withReporter(io.jaegertracing.Configuration.ReporterConfiguration
                    .fromEnv()
                    .withLogSpans(true))
}

@Component
class OrderServiceConfiguration {

    @Bean
    @LoadBalanced
    fun webClientBuilder() = WebClient.builder()

    @Bean
    @LoadBalanced
    fun restTemplate() = RestTemplateBuilder().build()


}

@Configuration
@EnableSwagger2
class SwaggerConfig(var build: Optional<BuildProperties>, var git: Optional<GitProperties>) {

    @Bean
    fun api(): Docket {
        return Docket(DocumentationType.SWAGGER_2)
                .apiInfo(
                        ApiInfoBuilder()
                                .title("Spring Boot REST API")
                                .description("Order Service REST API")
                                .contact(Contact("Marcel Widmer", "https://github.com/marzelwidmer", "marzelwidmer@gmail.com"))
                                .license("Apache 2.0")
                                .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
                                .version(
                                        when {
                                            (build.isPresent && git.isPresent) -> "${build.get().version}-${git.get().shortCommitId}-${git.get().branch}"
                                            else -> "1.0"
                                        }
                                )
                                .build()
                )
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths { it.equals("/api/v1/orders/random") }
                .build()
                .useDefaultResponseMessages(false)
                .forCodeGeneration(true)
    }
}