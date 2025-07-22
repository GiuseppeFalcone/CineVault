package com.falconevettorello.staticdataserver.actors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActorService {
    @Autowired
    private ActorRepository actorRepository;

    public List<ActorDTO> convertToActorDTO(List<Actor> actors) {
        return (actors != null) ? (actors.stream()
                .map(actor -> new ActorDTO(actor.getName(), actor.getRole()))
                .toList()) : List.of(); 
    }
}
