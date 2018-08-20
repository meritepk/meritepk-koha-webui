package pk.merite.koha.webui.bookshelf;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pk.merite.koha.webui.utils.ApiResponse;

@RestController
public class BookshelfController {

    private BookshelfService service;

    public BookshelfController(BookshelfService service) {
        this.service = service;
    }

    @GetMapping(path = { "/webservices/bookshelves" })
    public ResponseEntity<ApiResponse<List<Bookshelf>>> search(@RequestParam String callNo) {
        List<Bookshelf> result = service.findByCallNo(callNo);
        if (result == null || result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new ApiResponse<List<Bookshelf>>(result));
    }
}
