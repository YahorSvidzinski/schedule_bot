package com.demo.mslu.schedule.repository;

import com.demo.mslu.schedule.model.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Aliaksandr Miron
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
}
