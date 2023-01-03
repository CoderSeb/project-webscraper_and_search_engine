package lnu.sa224ny.backend.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class SearchResult {
    private int numberOfResults;
    private double duration;
    private List<PageDTO> results;
}
