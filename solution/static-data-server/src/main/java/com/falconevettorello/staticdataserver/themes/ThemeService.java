package com.falconevettorello.staticdataserver.themes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ThemeService {
    @Autowired
    private ThemeRepository themeRepository;
}
