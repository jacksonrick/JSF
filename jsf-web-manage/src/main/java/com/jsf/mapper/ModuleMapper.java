package com.jsf.mapper;

import com.jsf.database.model.manage.Module;
import com.jsf.database.model.custom.BaseVo;

import java.util.List;

/**
 * ModuleMapper Interface
 * @author rick
 * @version
 */
public interface ModuleMapper {

	List<Module> findByCondition(BaseVo baseVo);

	Integer findIdByAction(String action);

	int update(Module bean);

	int insert(Module bean);

	int delete(Integer id);

}
