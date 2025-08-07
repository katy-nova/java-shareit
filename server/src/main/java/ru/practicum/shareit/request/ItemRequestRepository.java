package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findByRequesterOrderByCreatedDesc(User requester, Pageable pageable);

    @EntityGraph(attributePaths = "items")
    List<ItemRequest> findByRequesterNotOrderByCreatedDesc(User requester, Pageable pageable);

    Optional<ItemRequest> findById(Long id);
}
