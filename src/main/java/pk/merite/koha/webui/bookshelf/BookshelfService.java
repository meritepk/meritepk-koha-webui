package pk.merite.koha.webui.bookshelf;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BookshelfService {

    private BookshelfRepository repository;

    public BookshelfService(BookshelfRepository repository) {
        this.repository = repository;
    }

    public List<Bookshelf> findByCallNo(String callNo) {
        return repository.findByCallNo(callNo);
    }

}
