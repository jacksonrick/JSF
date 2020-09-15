package com.jsf.database.mapper;

import com.jsf.database.model.custom.IdText;
import com.jsf.database.model.manage.Config;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Config Interface
 *
 * @author jfxu
 */
public interface ConfigMapper {

    List<Config> findAll();

    String findByKey(String key);

    List<Config> findByKeys(@Param("keys") String[] keys);

    int updateBatch(@Param("configs") List<IdText> configs);

}