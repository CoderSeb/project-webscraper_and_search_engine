package lnu.sa224ny.backend.controllers;


import lnu.sa224ny.backend.models.SearchBody;
import lnu.sa224ny.backend.models.SearchResult;
import lnu.sa224ny.backend.services.PageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class SearchController {

    private PageService pageService;

    @PostMapping("/api/search")
    public SearchResult search(@RequestBody SearchBody body) {
        String result = body.query.replaceAll("\"", "");
        SearchResult searchResult = new SearchResult();
        searchResult.setResults(pageService.search(result));
        searchResult.setNumberOfResults(pageService.getSearchResults());
        searchResult.setDuration(pageService.getDuration());
        return searchResult;
    }
}
