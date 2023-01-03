package lnu.sa224ny.backend.utils;

import lnu.sa224ny.backend.models.Page;
import lnu.sa224ny.backend.repositories.PageRepository;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@NoArgsConstructor
public class FileHandler {
    public void loadFilesToPages(String path, PageRepository pageRepository) {
        File directory = new File(path);
        System.out.println("Reading files in path " + path);

        if (directory.exists() && directory.isDirectory()) {
            Path directoryPath = Paths.get(path);

            try {
                Files.list(directoryPath).forEach(file -> {
                    Page newPage = new Page();
                    newPage.setUrl(String.valueOf(file.getFileName()));
                    try {
                        List<String> words = readFile(path + "/" + newPage.getUrl());
                        words.forEach(word -> pageRepository.addWordIdToPage(newPage, word));
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                    pageRepository.addPage(newPage);
                });
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    public PageRepository loadFiles() {
        PageRepository pageRepository = new PageRepository();
        loadFilesToPages("src/files/wikipedia/Words/Games", pageRepository);
        loadFilesToPages("src/files/wikipedia/Words/Programming", pageRepository);
        addLinksToPages("src/files/wikipedia/Links/Games", pageRepository);
        addLinksToPages("src/files/wikipedia/Links/Programming", pageRepository);

        return pageRepository;
    }

    private void addLinksToPages(String path, PageRepository pageRepository) {
        File directory = new File(path);

        System.out.println("Adding page links from path " + path);

        if (directory.exists() && directory.isDirectory()) {
            Path directoryPath = Paths.get(path);

            try {
                Files.list(directoryPath).forEach(file -> {
                    String pageName = String.valueOf(file.getFileName());
                    Page foundPage = pageRepository.findByUrl(pageName);
                    if (foundPage != null) {
                        try {
                            List<String> links = readLinkFile(path + "/" + pageName);
                            links.forEach(foundPage::addLink);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }

    public List<String> readFile(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        scanner.useDelimiter(" ");
        List<String> records = new ArrayList<>();

        while (scanner.hasNext()) {
            records.add(scanner.next());
        }

        scanner.close();

        return records;
    }

    public List<String> readLinkFile(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(path));
        scanner.useDelimiter("\n");
        List<String> records = new ArrayList<>();

        while (scanner.hasNext()) {
            records.add(scanner.nextLine());
        }

        scanner.close();

        return records;
    }
}
