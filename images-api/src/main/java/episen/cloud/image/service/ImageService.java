package episen.cloud.image.service;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

@Service
public class ImageService {

    private final String IMAGE_DIR = "src/main/resources/images/";

    public BufferedImage getRandomImage(int width, int height) throws IOException {
        File folder = new File(IMAGE_DIR);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png"));

        if (files == null || files.length == 0) {
            throw new IOException("No images found in directory: " + IMAGE_DIR);
        }

        // Select a random image
        Random random = new Random();
        File randomImage = files[random.nextInt(files.length)];

        // Resize the image
        return Thumbnails.of(randomImage)
                .size(width, height)
                .asBufferedImage();
    }
}