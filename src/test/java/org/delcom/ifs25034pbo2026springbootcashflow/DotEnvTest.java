package org.delcom.ifs25034pbo2026springbootcashflow;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;

class DotEnvTest {
    @AfterEach
    void cleanUp() {
        System.clearProperty("CF_TEST_NEW");
        System.clearProperty("CF_TEST_SET");
        System.clearProperty("CF_TEST_SPACED");
    }

    @Test
    void loadsKeysAndSkipsInvalidLines(@TempDir Path dir) throws Exception {
        Path f = dir.resolve(".env");
        Files.writeString(f, String.join("\n",
                "# komentar", "", "tanpa-sama-dengan", "=kosong",
                "CF_TEST_NEW=nilai", "  CF_TEST_SPACED = a b  ",
                "CF_TEST_SET=dari-file", "PATH=tidak-boleh-menimpa"));
        System.setProperty("CF_TEST_SET", "asli");
        DotEnv.load(f);
        assertEquals("nilai", System.getProperty("CF_TEST_NEW"));
        assertEquals("a b", System.getProperty("CF_TEST_SPACED"));
        assertEquals("asli", System.getProperty("CF_TEST_SET"));
        assertNull(System.getProperty("PATH"));
    }

    @Test
    void missingFileIsIgnored(@TempDir Path dir) throws Exception {
        DotEnv.load(dir.resolve("tidak-ada.env"));
    }

    @Test
    void loadDefaultDoesNotFail() throws Exception {
        DotEnv.load();
    }

    @Test
    void mainStartsSpring() throws Exception {
        try (MockedStatic<SpringApplication> spring = Mockito.mockStatic(SpringApplication.class)) {
            App.main(new String[0]);
            spring.verify(() -> SpringApplication.run(App.class, new String[0]));
        }
    }
}
