package com.jsf.database.mapper;

import com.jsf.database.model.User;
import com.jsf.database.model.custom.BaseVo;
import com.jsf.database.model.custom.IdText;
import com.jsf.database.model.excel.UserExcel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * UserMapper Interface
 *
 * @author rick
 */
public interface UserMapper {

    List<User> findByCondition(BaseVo baseVo);

    User findById(Long id);

    User findSimpleById(Long id);

    User findByPhone(String phone);

    Long findIdByPhone(String phone);

    int findCountByIdAndPwd(@Param("userId") Long userId, @Param("password") String password);

    int findCountByKey(@Param("key") String key, @Param("val") String val);

    User findByNameAndPwd(@Param("account") String account, @Param("password") String password);

    Object findFieleByUserId(@Param("userId") Long userId, @Param("field") String field);

    List<IdText> findUserLikePhone(String phone);

    List<UserExcel> findForExcel(BaseVo baseVo);


    int insert(User bean);

    int update(User bean);

    int delete(Long id);

    int deleteBatch(@Param("ids") Long[] ids);

}
