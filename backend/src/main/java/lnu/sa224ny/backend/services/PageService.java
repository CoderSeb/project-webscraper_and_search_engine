package lnu.sa224ny.backend.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import lnu.sa224ny.backend.models.Page;
import lnu.sa224ny.backend.models.PageDTO;
import lnu.sa224ny.backend.models.Scores;
import lnu.sa224ny.backend.repositories.PageRepository;
import lnu.sa224ny.backend.utils.FileHandler;
import lnu.sa224ny.backend.webscraper.WebScraper;

@Service
public class PageService {
    private final PageRepository pageRepository;

    private int searchResults;
    private double duration;

    public PageService() {
        new WebScraper("Cryptocurrency").runScraper(500);
        FileHandler fileHandler = new FileHandler();
        this.pageRepository = fileHandler.loadFiles("Cryptocurrency");
        calculatePageRank();
    }

    public int getSearchResults() {
        return this.searchResults;
    }

    public double getDuration() {
        return this.duration;
    }

    public List<PageDTO> search(String query) {
        long startTime = System.nanoTime();
        String[] words = query.split(" ");
        int[] wordIds = new int[words.length];
        for (int i = 0; i < words.length; i++) {
            wordIds[i] = pageRepository.getIdForWord(words[i]);
        }

        List<Page> pageResult = pageRepository.getPages();

        Scores scores = new Scores(pageResult.size());

        List<PageDTO> result = new ArrayList<>();

        calculateScores(wordIds, pageResult, scores);

        for (int i = 0; i < pageResult.size(); i++) {

            if (scores.content[i] != 0.0) {
                Page currentPage = pageResult.get(i);

                PageDTO pageDTO = new PageDTO();
                pageDTO.link = currentPage.getUrl();
                pageDTO.content = Double.isNaN(scores.content[i]) ? 0.0 : scores.content[i];
                pageDTO.location = Double.isNaN(scores.location[i]) ? 0.0 : 0.8 * scores.location[i];
                pageDTO.pageRank = 0.5 * currentPage.getPageRank();
                pageDTO.score = pageDTO.content + pageDTO.location + pageDTO.pageRank;
                result.add(pageDTO);
            }
        }

        searchResults = result.size();

        long endTime = System.nanoTime();

        duration = (double) ((endTime - startTime) / 1000000) / 1000;

        if (result.size() < 5) {
            return result.stream().sorted(Comparator.comparing(PageDTO::getScore).reversed()).toList();
        } else {
            return result.stream().sorted(Comparator.comparing(PageDTO::getScore).reversed()).toList().subList(0, 5);
        }
    }

    private void calculateScores(int[] wordIds, List<Page> pageResult, Scores scores) {
        for (int i = 0; i < pageResult.size(); i++) {
            Page currentPage = pageResult.get(i);
            scores.content[i] = getFrequencyScore(currentPage, wordIds);
            scores.location[i] = getLocationScore(currentPage, wordIds);
        }
        normalize(scores.content, false);
        normalize(scores.location, true);
    }

    private double getFrequencyScore(Page page, int[] wordIds) {
        double score = 0;
        List<Integer> wordList = page.getWords();

        for (Integer integer : wordList) {
            for (int wordId : wordIds) {
                if (integer == wordId) {
                    score++;
                }
            }
        }
        return score;
    }

    private double getLocationScore(Page page, int[] wordIds) {
        double score = 0;
        List<Integer> wordList = page.getWords();
        for (int wordId : wordIds) {
            boolean found = false;

            for (int i = 0; i < wordList.size(); i++) {
                if (wordList.get(i) == wordId) {
                    score += i + 1;
                    found = true;
                    break;
                }
            }
            if (!found) {
                score += 100000;
            }
        }
        return score;
    }

    private void normalize(double[] list, boolean smallIsBetter) {
        if (list.length > 0) {
            if (smallIsBetter) {
                double min = getMin(list);

                for (int i = 0; i < list.length; i++) {
                    list[i] = min / Math.max(list[i], 0.00001);
                }
            } else {
                double max = getMax(list);

                for (int i = 0; i < list.length; i++) {
                    list[i] = list[i] / max;
                }
            }
        }
    }

    private double getMin(double[] list) {
        double min = list[0];
        for (int i = 1; i < list.length; i++) {
            min = Math.min(min, list[i]);
        }
        return min;
    }

    private double getMax(double[] list) {
        double max = list[0];
        for (int i = 1; i < list.length; i++) {
            max = Math.max(max, list[i]);
        }
        return max;
    }

    private void normalizePR(Map<Page, Double> pageRanks) {
        double max = 0;
        for (Page page : pageRanks.keySet()) {
            max = Math.max(max, pageRanks.get(page));
        }
        for (Page page : pageRanks.keySet()) {
            pageRanks.put(page, pageRanks.get(page) / max);
        }
    }

    private void calculatePageRank() {
        System.out.println("Running page rank algorithm...");
        List<Page> allPages = pageRepository.getPages();
        int maxIterations = 20;
        Map<Page, Double> pageRanks = new HashMap<>();
        for (int i = 0; i < maxIterations; i++) {
            for (Page page : allPages) {
                pageRanks.put(page, iteratePageRank(page, allPages));
            }
        }
        normalizePR(pageRanks);

        for (Page page : pageRanks.keySet()) {
            page.setPageRank(pageRanks.get(page));
        }
        pageRepository.setPages(allPages);
    }

    private double iteratePageRank(Page page, List<Page> allPages) {
        double pageRank = 0;

        for (Page currentPage : allPages) {
            if (currentPage.hasLinkTo(page.getUrl())) {
                pageRank += currentPage.getPageRank() / currentPage.getNumberOfLinks();
            }
        }
        return 0.85 * pageRank + 0.15;
    }
}
