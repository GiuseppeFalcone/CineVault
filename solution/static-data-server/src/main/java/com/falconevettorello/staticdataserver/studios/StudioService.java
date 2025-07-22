package com.falconevettorello.staticdataserver.studios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudioService {
    @Autowired
    private StudioRepository studioRepository;
}
