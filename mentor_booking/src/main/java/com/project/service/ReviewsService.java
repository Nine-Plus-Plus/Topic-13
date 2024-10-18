/* 
package com.project.service;

import com.project.dto.ReviewsDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Reviews;
import com.project.repository.ReviewsRepository;
import com.project.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewsService {

    @Autowired
    private ReviewsRepository reviewsRepository;

    @Autowired
    private UsersRepository usersRepository;

    public Response createReview(ReviewsDTO reviewsDTO) {
        Response response = new Response();
        try {
            Reviews review = new Reviews();
            review.setComment(reviewsDTO.getComment());
            review.setRating(reviewsDTO.getRating());
            review.setDateCreated(LocalDateTime.now());
            review.setAvailableStatus(AvailableStatus.ACTIVE);
            review.setUser(usersRepository.findById(reviewsDTO.getUser().getId())
                    .orElseThrow(() -> new OurException("User not found")));
            reviewsRepository.save(review);

            ReviewsDTO dto = mapToDTO(review);
            response.setReviewsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Review created successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during review creation: " + e.getMessage());
        }
        return response;
    }

    public Response getAllReviews() {
        Response response = new Response();
        try {
            List<Reviews> reviewsList = reviewsRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<ReviewsDTO> reviewsDTOList = reviewsList.stream().map(this::mapToDTO).collect(Collectors.toList());
            response.setReviewsDTOList(reviewsDTOList);
            response.setStatusCode(200);
            response.setMessage("Reviews retrieved successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving reviews: " + e.getMessage());
        }
        return response;
    }

    public Response getReviewById(Long id) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            ReviewsDTO dto = mapToDTO(review);
            response.setReviewsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Review retrieved successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during retrieving review: " + e.getMessage());
        }
        return response;
    }

    public Response updateReview(Long id, ReviewsDTO reviewsDTO) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            if (reviewsDTO.getComment() != null) {
                review.setComment(reviewsDTO.getComment());
            }
            if (reviewsDTO.getRating() != 0) {
                review.setRating(reviewsDTO.getRating());
            }
            review.setDateCreated(LocalDateTime.now());
            reviewsRepository.save(review);

            ReviewsDTO dto = mapToDTO(review);
            response.setReviewsDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Review updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during review update: " + e.getMessage());
        }
        return response;
    }

    public Response deleteReview(Long id) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            review.setAvailableStatus(AvailableStatus.DELETED);
            reviewsRepository.save(review);
            response.setStatusCode(200);
            response.setMessage("Review deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during review deletion: " + e.getMessage());
        }
        return response;
    }

    private ReviewsDTO mapToDTO(Reviews review) {
        ReviewsDTO dto = new ReviewsDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setDateCreated(review.getDateCreated());
        dto.setAvailableStatus(review.getAvailableStatus());
        dto.setUser(mapToUsersDTO(review.getUser()));
        return dto;
    }

    private UsersDTO mapToUsersDTO(Users user) {
        UsersDTO dto = new UsersDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        // other fields...
        return dto;
    }
    
}
*/