package com.campus.dining.service;

import com.campus.dining.common.PageResult;
import com.campus.dining.dto.ReviewDTO;
import com.campus.dining.entity.Review;

public interface ReviewService {

    /**
     * 创建评价
     */
    Review createReview(Long userId, ReviewDTO dto);

    /**
     * 分页查询档口评价
     */
    PageResult<Review> listReviewsByStall(Long stallId, int page, int size);

    /**
     * 分页查询菜品评价
     */
    PageResult<Review> listReviewsByDish(Long dishId, int page, int size);

    /**
     * 分页查询用户评价
     */
    PageResult<Review> listReviewsByUser(Long userId, int page, int size);

    /**
     * 商户回复评价
     */
    void replyReview(Long reviewId, String replyContent);

    /**
     * 管理员隐藏/显示评价
     */
    void updateReviewStatus(Long id, Integer status);

    /**
     * 分页查询所有评价（管理员）
     */
    PageResult<Review> listAllReviews(int page, int size);
}
