package pk.merite.koha.webui.bookshelf;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

    List<Bookshelf> findByCallNo(String callNo);
}
