package org.example.finalproject.demos.service.impl;

import org.example.finalproject.demos.mapper.ReviewMapper;
import org.example.finalproject.demos.pojo.Review;
import org.example.finalproject.demos.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评价服务实现类
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewMapper reviewMapper;

    @Override
    public List<Review> getMyReviews(Integer userId) {
        return reviewMapper.selectByUserId(userId);
    }

    @Override
    public List<Review> getAllReviews() {
        return reviewMapper.selectAll();
    }
}
