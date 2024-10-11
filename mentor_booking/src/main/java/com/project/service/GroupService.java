
package com.project.service;

import com.project.dto.GroupDTO;
import com.project.dto.Response;
import com.project.enums.AvailableStatus;
import com.project.exception.OurException;
import com.project.model.Group;
import com.project.model.Projects;
import com.project.model.Students;
import com.project.model.Class;
import com.project.repository.ClassRepository;
import com.project.repository.GroupRepository;
import com.project.repository.ProjectsRepository;
import com.project.repository.StudentsRepository;
import com.project.ultis.Converter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Thịnh Đạt
 */
@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    
    @Autowired
    private StudentsRepository studentsRepository;
    
    @Autowired
    private ProjectsRepository projectsRepository;
    
    @Autowired
    private ClassRepository classRepository;
    
    public Response createGroup(GroupDTO inputRequest){
        Response response = new Response();
        try{
            if (groupRepository.findGroup
        (inputRequest.getGroupName(),inputRequest.getId(), AvailableStatus.ACTIVE).isPresent())
                throw new OurException("Group name have existed");
            Group group = new Group();
            group.setGroupName(inputRequest.getGroupName());
            group.setDateCreated(LocalDate.now());
            group.setDateUpdated(LocalDate.now());
            
            Students student = studentsRepository.findById(inputRequest.getStudents().get(0).getId()).get();
            List<Students> studentsList = new ArrayList<>();
            studentsList.add(student);
            group.setStudents(studentsList);
            
            Projects project = projectsRepository.findById(inputRequest.getProject().getId()).get();
            group.setProject(project);
            
            Class aClass = classRepository.findById(inputRequest.getClassDTO().getId()).get();
            group.setAClass(aClass);
            
            group.setAvailableStatus(AvailableStatus.ACTIVE);
            groupRepository.save(group);
            
            if (group.getId()>0){
                GroupDTO dto = Converter.convertGroupToGroupDTO(group);
                response.setGroupDTO(dto);
                response.setStatusCode(200);
                response.setMessage("Group created successfully");
            }
            
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred during group creation: " +e.getMessage());
        }
        return response;
    }
    
    public Response getAllGroups(){
        Response response = new Response();
        try{
            List<Group> groupList = groupRepository.findByAvailableStatus(AvailableStatus.ACTIVE);
            List<GroupDTO> groupListDTO = null;
            if (groupList != null) {
                groupListDTO = groupList.stream()
                        .map(Converter::convertGroupToGroupDTO)
                        .collect(Collectors.toList());
                response.setGroupDTOList(groupListDTO);
                response.setStatusCode(200);
                response.setMessage("Groups fetched successfully");
            } else {
                response.setGroupDTOList(groupListDTO);
                throw new OurException("Cannot find any group");
            }
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occured during get all groups "+e.getMessage());
        }
        return response;
    }
    
    public Response getGroupById(Long id){
        Response response = new Response();
        try{
            Group findGroup = groupRepository.findByIdAndAvailableStatus(id, AvailableStatus.ACTIVE);
            GroupDTO dto = Converter.convertGroupToGroupDTO(findGroup);
            response.setGroupDTO(dto);
            if (findGroup != null) {
                response.setStatusCode(200);
                response.setMessage("Successfully");
            } else {
                throw new OurException("Cannot find group");
            }
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occured during get group "+e.getMessage());
        }
        return response;
    }
    
    public Response updateGroup(Long id, Group newGroup){
        Response response = new Response();
        try{
            Group presentGroup = groupRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find group with id: " + id));

            presentGroup.setGroupName(newGroup.getGroupName());
            presentGroup.setDateUpdated(LocalDate.now());
            
            groupRepository.save(presentGroup);

            GroupDTO dto = Converter.convertGroupToGroupDTO(presentGroup);
            response.setGroupDTO(dto);
            response.setStatusCode(200);
            response.setMessage("Group updated successfully");
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred while updating class: " + e.getMessage());
        }
        return response;
    }
    
    public Response deleteGroup(Long id){
        Response response = new Response();
        try{
            Group deleteGroup = groupRepository.findById(id)
                    .orElseThrow(() -> new OurException("Cannot find group with id: " + id));
            deleteGroup.setAvailableStatus(AvailableStatus.DELETED);
            groupRepository.save(deleteGroup);

            response.setStatusCode(200);
            response.setMessage("Group deleted successfully");
        }catch(OurException e){
            response.setStatusCode(400);
            response.setMessage(e.getMessage());
        }catch(Exception e){
            response.setStatusCode(500);
            response.setMessage("Error occurred while deleting group: " + e.getMessage());
        }
        return response;
    }
}
