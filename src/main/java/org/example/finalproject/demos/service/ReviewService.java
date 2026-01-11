package org.example.finalproject.demos.service;

import org.example.finalproject.demos.pojo.Review;
import java.util.List;

/**
 * 评价服务接口
 */
public interface ReviewService {
    /**
     * 获取用户的评价列表
     */
    List<Review> getMyReviews(Integer userId);

    /**
     * 获取所有评价（管理员用）
     */
    List<Review> getAllReviews();
}
