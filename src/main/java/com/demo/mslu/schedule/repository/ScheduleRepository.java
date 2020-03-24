package com.demo.mslu.schedule.repository;

import com.demo.mslu.schedule.model.Schedule;
import com.demo.mslu.schedule.model.constant.Week;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Aliaksandr Miron
 */
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    Optional<Schedule> findByWeek(Week week);
}
