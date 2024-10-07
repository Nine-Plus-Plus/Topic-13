
package com.project.service;

import com.project.dto.Response;
import com.project.dto.TopicDTO;
import com.project.exception.OurException;
import com.project.model.Mentors;
import com.project.model.Projects;
import com.project.model.Semester;
import com.project.model.Topic;
import com.project.repository.TopicRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class TopicService {
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    @Lazy
    private ModelMapper modelMapper;
    
    public Response createTopic(TopicDTO createRequest) {
        Response response = new Response();

        try {
            if (topicRepository.findByTopicName(createRequest.getTopicName()).isPresent()) {
                throw new OurException("Topic has already existed");
            }

            Topic topic = new Topic();
            topic.setTopicName(createRequest.getTopicName());
            topic.setContext(createRequest.getContext());
            topic.setProblems(createRequest.getProblems());
            topic.setActor(createRequest.getActor());
            topic.setRequirement(createRequest.getRequirement());
            topic.setNonFunctionRequirement(createRequest.getNonFunctionRequirement());
            topic.setDateCreated(LocalDateTime.now());
            topic.setDateUpdated(LocalDateTime.now());
            topic.setProject(modelMapper.map(createRequest.getProjectDTO(), Projects.class));
            topic.setMentor(modelMapper.map(createRequest.getMentorsDTO(),Mentors.class));
            topic.setSemester(modelMapper.map(createRequest.getSemesterDTO(),Semester.class));
            topicRepository.save(topic);

            if (topic.getId() > 0) {
                TopicDTO dto = modelMapper.map(topic, TopicDTO.class);
                dto.setProjectDTO(createRequest.getProjectDTO());
                dto.setMentorsDTO(createRequest.getMentorsDTO());
                dto.setSemesterDTO(createRequest.getSemesterDTO());
                response.setTopicDTO(dto);
                response.setStatusCode(201);
                response.setMessage("Topic added successfully");
            }

        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred during topic creation: " + e.getMessage());
        }

        return response;
    }
    
    public Response getAllTopics(){
        Response response = new Response();
        try {
            List<Topic> topicLists = topicRepository.findAll();
            List<TopicDTO> topicListDTO = null;
            if (topicLists != null) {
                topicListDTO = Arrays.asList(modelMapper.map(topicLists, TopicDTO[].class));
            }
            response.setTopicDTOList(topicListDTO);
            response.setStatusCode(200);
            response.setMessage("Topics fetched successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.getMessage();
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get all topics " + e.getMessage());
        }
        return response;
    }
    
    public Response getTopicById(Long id){
        Response response = new Response();
        try {
            Topic findTopic = topicRepository.findById(id).orElse(null);
            if (findTopic != null) {
                TopicDTO dto = modelMapper.map(findTopic, TopicDTO.class);
                
                response.setTopicDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Successfully");
            }else throw new OurException("Cannot find topic");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occured during get topic " + e.getMessage());
        }
        return response;
    }
    
    public Response updateTopic(Long id, Topic newTopic){
        Response response = new Response();
        try {
            Topic presentTopic = topicRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find topic with id: "+id));
            if (topicRepository.findByTopicName(newTopic.getTopicName()).isPresent() 
                    && newTopic.getTopicName().equals(presentTopic.getTopicName()) == false) {
                throw new OurException("Semester has already existed");
            }
            
            presentTopic.setTopicName(newTopic.getTopicName());
            presentTopic.setContext(newTopic.getContext());
            presentTopic.setProblems(newTopic.getProblems());
            presentTopic.setActor(newTopic.getActor());
            presentTopic.setRequirement(newTopic.getRequirement());
            presentTopic.setNonFunctionRequirement(newTopic.getNonFunctionRequirement());
            presentTopic.setDateUpdated(LocalDateTime.now());
            presentTopic.setProject(newTopic.getProject());
            presentTopic.setMentor(newTopic.getMentor());
            presentTopic.setSemester(newTopic.getSemester());
            
            topicRepository.save(presentTopic);
            
            TopicDTO dto = modelMapper.map(presentTopic, TopicDTO.class);
            response.setTopicDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Topic updated successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating topic: " + e.getMessage());
        }
        return response;
    }
    
    public Response deleteTopic(Long id) {
        Response response = new Response();
        try {
            Topic deleteTopic = topicRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find topic with id: " + id));
            topicRepository.delete(deleteTopic);
            
            response.setStatusCode(200);
            response.setMessage("Topic deleted successfully");
        } catch (OurException e) {
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting topic: " + id);
        }
        return response;
    }
}
