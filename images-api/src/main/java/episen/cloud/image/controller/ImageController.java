package episen.cloud.image.controller;

import episen.cloud.image.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

@CrossOrigin(origins = "http://localhost:8888")

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Image API", description = "Endpoints for retrieving random images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping(value = "/random-image", produces = MediaType.IMAGE_JPEG_VALUE)
    @Operation(summary = "Get a random image", description = "Returns a randomly selected image resized to the specified width and height.")
    public byte[] getRandomImage(
            @Parameter(description = "Width of the image in pixels", example = "300")
            @RequestParam int width,

            @Parameter(description = "Height of the image in pixels", example = "300")
            @RequestParam int height
    ) throws Exception {
        BufferedImage image = imageService.getRandomImage(width, height);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", baos);
        return baos.toByteArray();
    }
}
