package com.demo.mslu.schedule.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ZoneType {
    STUDY_GROUP_ZONE("studyGroupZone"),
    STUDY_WEEK_ZONE("studyWeekZone"),
    BUTTON_ZONE("buttonZone");

    private String value;
}
