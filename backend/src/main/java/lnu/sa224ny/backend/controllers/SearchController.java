package lnu.sa224ny.backend.controllers;


import lnu.sa224ny.backend.models.SearchQuery;
import lnu.sa224ny.backend.models.SearchResult;
import lnu.sa224ny.backend.services.PageService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;



@CrossOrigin(origins = "*")
@RestController
@AllArgsConstructor
public class SearchController {

    private PageService pageService;

    @PostMapping("/api/search")
    public SearchResult search(@RequestBody SearchQuery searchQuery) {
        String result = searchQuery.query.replaceAll("\"", "");
        SearchResult searchResult = new SearchResult();
        searchResult.setResults(pageService.search(result, searchQuery.searchLevel));
        searchResult.setNumberOfResults(pageService.getSearchResults());
        searchResult.setDuration(pageService.getDuration());
        return searchResult;
    }
}
