package util;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class StringGenerator {
    private final ArrayList<String> possibleStrings;
    private final Random random = new Random();

    public StringGenerator(String fileName) throws IOException {
        BufferedReader file = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getResourceAsStream(fileName))));
        possibleStrings = file.lines().collect(Collectors.toCollection(ArrayList::new));
    }

    public String random() {
        return possibleStrings.get(random.nextInt(possibleStrings.size()));
    }

    public ArrayList<String> random(int amount) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            result.add(random());
        }
        return result;
    }
}
