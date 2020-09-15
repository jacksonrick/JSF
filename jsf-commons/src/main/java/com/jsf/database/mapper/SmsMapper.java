package com.jsf.database.mapper;

import com.jsf.database.model.Sms;
import com.jsf.database.model.custom.BaseVo;

import java.util.List;

/**
 * SmsMapper Interface
 * @date 2017年06月14日 上午 09:32:08
 * @author jfxu
 */
public interface SmsMapper {

	List<Sms> findByCondition(BaseVo baseVo);

    Sms check(String phone);

    int findCountByPhone(String phone);

    int insert(Sms bean);

    int update(Long id);

}