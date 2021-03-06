package com.jsf.mapper;

import com.jsf.database.model.manage.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * RoleMapper
 * @author rick
 * @version
 */
public interface RoleMapper {

	List<Role> findAll();

	int insert(Role bean);

	int update(Role bean);

    int delete(Integer id);

	/**
	 * 检查权限
	 * @return 1 | 0
	 */
	@Deprecated
	int checkRights(@Param("roleId") Integer roleId, @Param("action") String action);

}
