package com.project.service;

import com.project.dto.ReviewsDTO;
import com.project.dto.UsersDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Reviews;
import com.project.model.Role;
import com.project.model.Users;
import com.project.repository.ReviewsRepository;
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

    public Response createReview(ReviewsDTO createRequest) {
        Response response = new Response();
        try {
            Reviews review = mapToEntity(createRequest);
            review.setDateCreated(LocalDateTime.now());
            reviewsRepository.save(review);
            response.setStatusCode(200);
            response.setMessage("Review created successfully");
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during review creation: " + e.getMessage());
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

    public Response updateReview(Long id, ReviewsDTO updateRequest) {
        Response response = new Response();
        try {
            Reviews review = reviewsRepository.findById(id)
                    .orElseThrow(() -> new OurException("Review not found"));
            review.setComment(updateRequest.getComment());
            review.setRating(updateRequest.getRating());
            review.setAvailableStatus(updateRequest.getAvailableStatus());
            review.setDateCreated(updateRequest.getDateCreated());
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

    private ReviewsDTO mapToDTO(Reviews review) {
        ReviewsDTO dto = new ReviewsDTO();
        dto.setId(review.getId());
        dto.setComment(review.getComment());
        dto.setRating(review.getRating());
        dto.setDateCreated(review.getDateCreated());
        dto.setAvailableStatus(review.getAvailableStatus());
        dto.setUser(Converter.convertUserToUserDTO(review.getUser()));
        return dto;
    }

    private Reviews mapToEntity(ReviewsDTO dto) {
        Reviews review = new Reviews();
        review.setComment(dto.getComment());
        review.setRating(dto.getRating());
        review.setAvailableStatus(dto.getAvailableStatus() != null ? dto.getAvailableStatus() : AvailableStatus.ACTIVE);
        review.setDateCreated(dto.getDateCreated());
        Users user = new Users();
        user.setId(dto.getUser().getId());
        review.setUser(user);
        return review;
    }

    private Users convertUserDTOToUser(UsersDTO userDTO) {
        Users user = new Users();
        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setFullName(userDTO.getFullName());
        user.setBirthDate(userDTO.getBirthDate());
        user.setAvatar(userDTO.getAvatar());
        user.setAddress(userDTO.getAddress());
        user.setPhone(userDTO.getPhone());
        user.setGender(userDTO.getGender());
        user.setDateUpdated(userDTO.getDateUpdated());
        user.setDateCreated(userDTO.getDateCreated());
        
        Role role = new Role();
        role.setId(userDTO.getRole().getId());
        role.setRoleName(userDTO.getRole().getRoleName());
        user.setRole(role);

        user.setAvailableStatus(userDTO.getAvailableStatus());
        return user;
    }
}