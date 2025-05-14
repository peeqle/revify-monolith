package com.revify.monolith.resource.utils;


import com.google.common.net.MediaType;
import com.revify.monolith.resource.config.ConfigFileTemporaryProperties;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tika.detect.AutoDetectReader;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FileUtils {

    private final ConfigFileTemporaryProperties configFileTemporaryProperties;

    private static File tempFileDir;

    @PostConstruct
    public void postConstruct() {
        File file = new File(configFileTemporaryProperties.getDirectory());
        if (!file.exists()) {
            file.mkdir();
        }
        tempFileDir = file;
    }

    public File createTemporaryFile(InputStream inputStream, String filename) throws IOException {
        File tempFile = File.createTempFile(UUID.randomUUID().toString(), "." + FilenameUtils.getExtension(filename), tempFileDir);
        Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        IOUtils.closeQuietly(inputStream);

        return tempFile;
    }

    public static String detectMimeType(File file) throws IOException, SAXException {
        try (InputStream inputStream = TikaInputStream.get(file)) {
            Metadata metadata = new Metadata();
            metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, file.getName());

            MediaType mediaType = MediaType.OCTET_STREAM;
            try (AutoDetectReader reader = new AutoDetectReader(TikaInputStream.get(file))) {
//				mediaType = reader.getDetector().detect(TikaInputStream.get(file), metadata);
            } catch (Exception e) {
                System.err.println("Failed to detect character encoding. Using default encoding.");
            }

            return mediaType.toString();
        }
    }

    /**
     * 7 days lifetime
     *
     * @return file lifetime countdown
     */
    private static Long fileLifetime() {
        return Instant.now().plusSeconds(60 * 60 * 24 * 7).toEpochMilli();
    }
}
