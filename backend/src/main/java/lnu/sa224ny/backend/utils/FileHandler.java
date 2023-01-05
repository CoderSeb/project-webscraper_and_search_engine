package lnu.sa224ny.backend.utils;

import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import lnu.sa224ny.backend.models.Page;
import lnu.sa224ny.backend.repositories.PageRepository;
import lombok.NoArgsConstructor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

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

    public PageRepository loadFiles(String entrySite) {
        PageRepository pageRepository = new PageRepository();
        loadFilesToPages("src/files/wikipedia/Words/" + entrySite, pageRepository);

        addLinksToPages("src/files/wikipedia/Links/" + entrySite, pageRepository);

        return pageRepository;
    }

    public void saveToFiles(String entrySite, String pageUrl, HtmlPage page) {
        String futureFileName = pageUrl.substring(6);
        try {
            String wordFilePath = "src/files/wikipedia/Words/" + entrySite + "/" + futureFileName;
            String linkFilePath = "src/files/wikipedia/Links/" + entrySite + "/" + futureFileName;
            File newWordFile = new File(wordFilePath);
            File newLinkFile = new File(linkFilePath);

            if (!newWordFile.getParentFile().exists()) {
                newWordFile.getParentFile().mkdirs();
            }

            if (!newLinkFile.getParentFile().exists()) {
                newLinkFile.getParentFile().mkdirs();
            }

            if (!newWordFile.exists()) {
                newWordFile.createNewFile();
                System.out.println("File created: " + newWordFile.getName());
            }

            if (!newLinkFile.exists()) {
                newLinkFile.createNewFile();
                System.out.println("File created: " + newLinkFile.getName());
            }


            DomElement bodyNode = page.getElementById("bodyContent");
            StringBuilder allWords = new StringBuilder();
            for (String word : extractWords(bodyNode)) {
                allWords.append(" ").append(word);
            }

            FileWriter wordFileWriter = new FileWriter(wordFilePath);
            wordFileWriter.write(allWords.toString());
            System.out.println("Written words to file: " + newWordFile.getName());
            wordFileWriter.close();

            FileWriter linkFileWriter = new FileWriter(linkFilePath);
            List<String> allLinks = new ArrayList<>();
            for (DomElement a : bodyNode.getElementsByTagName("a")) {
                String currentHref = ((HtmlAnchor) a).getHrefAttribute();

                if (currentHref.startsWith("/wiki/")
                        && !currentHref.contains(".jpg")
                        && !currentHref.contains(".svg")
                        && !allLinks.contains(currentHref)
                ) {
                    allLinks.add(currentHref);
                }
            }
            linkFileWriter.write(allLinks.toString());
            linkFileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String[] extractWords(DomElement page) {
        if (page == null) {
            return new String[0];
        }
        String allWords = page.asNormalizedText();
        allWords = allWords.replaceAll("[\\n\\r\\^\\$\\.\\|\\?\\*\\+\\{\\}\\[\\]\\(\\)]+", " ").trim();
        String[] allActualWords = Pattern.compile("[a-zA-Z]+")
                .matcher(allWords)
                .results()
                .map(MatchResult::group)
                .map(String::toLowerCase)
                .toArray(String[]::new);
        return allActualWords;
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
