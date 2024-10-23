package com.project.service;

import com.project.dto.ReviewsDTO;
import com.project.dto.UsersDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Reviews;
import com.project.model.Users;
import com.project.repository.ReviewsRepository;
import com.project.repository.UsersRepository;
import com.project.ultis.Converter;

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

    public Response createReview(ReviewsDTO createRequest) {
        Response response = new Response();
        try {
            // Fetch the full UsersDTO objects based on the provided IDs
            Users user = usersRepository.findById(createRequest.getUser_id().getId())
                    .orElseThrow(() -> new OurException("User not found"));
            Users userReceive = usersRepository.findById(createRequest.getUser_receive_id().getId())
                    .orElseThrow(() -> new OurException("User Receive not found"));

            Reviews review = mapToEntity(createRequest);
            review.setUser(user);
            review.setUserReceive(userReceive);
            review.setDateCreated(LocalDateTime.now());
            reviewsRepository.save(review);

            ReviewsDTO dto = convertReviewToReviewDTO(review);
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

    private Reviews mapToEntity(ReviewsDTO dto) {
        Reviews review = new Reviews();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setAvailableStatus(dto.getAvailableStatus() != null ? dto.getAvailableStatus() : AvailableStatus.ACTIVE);
        review.setDateCreated(dto.getDateCreated());
        return review;
    }

    private ReviewsDTO convertReviewToReviewDTO(Reviews review) {
        ReviewsDTO reviewsDTO = new ReviewsDTO();
        reviewsDTO.setId(review.getId());
        reviewsDTO.setComment(review.getComment());
        reviewsDTO.setRating(review.getRating());
        reviewsDTO.setDateCreated(review.getDateCreated());
        reviewsDTO.setAvailableStatus(review.getAvailableStatus());

        if (review.getUser() != null) {
            UsersDTO userDTO = Converter.convertUserToUserDTO(review.getUser());
            reviewsDTO.setUser_id(userDTO);
        }

        if (review.getUserReceive() != null) {
            UsersDTO userReceiveDTO = Converter.convertUserToUserDTO(review.getUserReceive());
            reviewsDTO.setUser_receive_id(userReceiveDTO);
        }

        return reviewsDTO;
    }

    private ReviewsDTO mapToDTO(Reviews review) {
        ReviewsDTO dto = new ReviewsDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setDateCreated(review.getDateCreated());
        dto.setAvailableStatus(review.getAvailableStatus());
        UsersDTO userDTO = new UsersDTO();
        userDTO.setId(review.getUser().getId());
        dto.setUser_id(userDTO);
        UsersDTO userReceiveDTO = new UsersDTO();
        userReceiveDTO.setId(review.getUserReceive().getId());
        dto.setUser_receive_id(userReceiveDTO);
        return dto;
    }

    public Response getReviewsByUserId(Long userId) {
        Response response = new Response();
        try {
            List<Reviews> reviews = reviewsRepository.findByUserId(userId);
            List<ReviewsDTO> reviewsDTOs = reviews.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setStatusCode(200);
            response.setReviewsDTOList(reviewsDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during fetching reviews: " + e.getMessage());
        }
        return response;
    }

    public Response getReviewsByUserReceiveId(Long userReceiveId) {
        Response response = new Response();
        try {
            List<Reviews> reviews = reviewsRepository.findByUserReceiveId(userReceiveId);
            List<ReviewsDTO> reviewsDTOs = reviews.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setStatusCode(200);
            response.setReviewsDTOList(reviewsDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during fetching reviews: " + e.getMessage());
        }
        return response;
    }

    public Response getReviewById(Long id) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            ReviewsDTO reviewDTO = mapToDTO(review);
            response.setStatusCode(200);
            response.setReviewsDTO(reviewDTO);
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during fetching review: " + e.getMessage());
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

    public Response updateReview(Long id, ReviewsDTO updateRequest) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            review.setComment(updateRequest.getComment());
            review.setRating(updateRequest.getRating());
            review.setAvailableStatus(updateRequest.getAvailableStatus());
            review.setDateCreated(updateRequest.getDateCreated());
            Users userReceive = new Users();
            userReceive.setId(updateRequest.getUser_receive_id().getId());
            review.setUserReceive(userReceive);
            reviewsRepository.save(review);
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

    public Response getAllReviews() {
        Response response = new Response();
        try {
            List<Reviews> reviews = reviewsRepository.findAll();
            List<ReviewsDTO> reviewsDTOs = reviews.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
            response.setStatusCode(200);
            response.setReviewsDTOList(reviewsDTOs);
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during fetching reviews: " + e.getMessage());
        }
        return response;
    }
}