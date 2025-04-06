package matching.teamify.dto.page;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {

    private List<T> content;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private boolean first;
    private boolean last;

    public PageResponse(List<T> content, long totalElements, int pageNumber, int pageSize) {
        this.content = content;
        this.totalElements = totalElements;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalPages = (pageSize == 0) ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        this.first = pageNumber == 0;
        this.last = pageNumber == totalPages -1 || totalPages == 0;
    }

}
