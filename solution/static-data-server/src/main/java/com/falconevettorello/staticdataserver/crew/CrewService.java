package com.falconevettorello.staticdataserver.crew;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrewService {
    @Autowired
    private CrewRepository crewRepository;

public List<CrewDTO> convertToCrewDTO(List<Crew> crew) {
    return crew.stream()
            .map(c -> new CrewDTO(c.getCrewName(), c.getCrewRole()))
            .toList();
    }
    
}
