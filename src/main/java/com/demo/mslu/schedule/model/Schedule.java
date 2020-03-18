package com.demo.mslu.schedule.model;

import com.demo.mslu.schedule.model.constant.Week;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * @author Aliaksandr Miron
 */
@Data
@Table
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {

    @Id
    @Column
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column
    private Week week;

    @Column
    private String body;
}
