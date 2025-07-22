package com.falconevettorello.staticdataserver.crew;

public class CrewDTO {
    private String crewName;
    private String crewRole;

    public CrewDTO() {}

    public CrewDTO(String crewName, String crewRole) {
        this.crewName = crewName;
        this.crewRole = crewRole;
    }

    public String getCrewName() {
        return crewName;
    }

    public void setCrewName(String crewName) {
        this.crewName = crewName;
    }

    public String getCrewRole() {
        return crewRole;
    }

    public void setCrewRole(String crewRole) {
        this.crewRole = crewRole;
    }
}
