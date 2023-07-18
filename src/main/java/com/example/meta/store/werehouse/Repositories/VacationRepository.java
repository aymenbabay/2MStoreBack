package com.example.meta.store.werehouse.Repositories;

import java.util.List;

import com.example.meta.store.Base.Repository.BaseRepository;
import com.example.meta.store.werehouse.Entities.Vacation;

public interface VacationRepository extends BaseRepository<Vacation, Long> {

	List<Vacation> findByCompanyIdAndWorkerId(Long companyId, Long workerId);

}
