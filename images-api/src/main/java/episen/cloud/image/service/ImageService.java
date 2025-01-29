package episen.cloud.image.service;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ImageService {

    private final ResourceLoader resourceLoader;
    private final List<String> imageFiles = List.of("images/images.jpg", "images/téléchargement.jpg");

    public ImageService(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public BufferedImage getRandomImage(int width, int height) throws IOException {
        // Pick a random image from the list
        String randomImagePath = imageFiles.get(new Random().nextInt(imageFiles.size()));

        Resource resource = resourceLoader.getResource("classpath:" + randomImagePath);
        if (!resource.exists()) {
            throw new IOException("Image not found: " + randomImagePath);
        }

        try (InputStream inputStream = resource.getInputStream()) {
            return ImageIO.read(inputStream);
        }
    }
}
