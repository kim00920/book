package book.Book.user.service.impl;

import book.Book.mapper.RatingMapper;
import book.Book.user.dto.RatingResponse;
import book.Book.user.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingServiceImpl implements RatingService {

    private final RatingMapper ratingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RatingResponse> countGroupedRating(Long bookId) {
        return ratingMapper.countGroupedRating(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double AvgRating(Long bookId) {
        return ratingMapper.avgRating(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public RatingResponse findUserRating(Long userId, Long bookId) {
        return ratingMapper.findUserRating(userId, bookId);
    }
}
