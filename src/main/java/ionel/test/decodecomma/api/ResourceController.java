package ionel.test.decodecomma.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class ResourceController {
    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    @GetMapping
    public ResponseEntity<List<String>> getSomeResource(@RequestParam("res") List<String> resources) {

        LOG.info("Requested resources: {}", resources);
        return ResponseEntity.ok(resources);
    }
}
