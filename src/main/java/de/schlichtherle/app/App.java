package de.schlichtherle.app;

import de.schlichtherle.app.rest.CreateLicenseResource;
import de.schlichtherle.app.rest.SidResource;
import de.schlichtherle.app.rest.TrueLicenseResource;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(
        basePackages = {
                "de.schlichtherle"
        }
)
public class App {
    public static void main(String[] args) {
        new SpringApplicationBuilder().bannerMode(Banner.Mode.OFF)
                .properties()
                .sources(App.class)
                .sources(SidResource.class)
                .sources(CreateLicenseResource.class)
                .sources(TrueLicenseResource.class)
//                .web(WebApplicationType.REACTIVE)
                .run(args);
    }
}
