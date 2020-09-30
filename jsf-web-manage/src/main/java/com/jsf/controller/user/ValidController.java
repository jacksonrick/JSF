package com.jsf.controller.user;

import com.jsf.utils.entity.ResMsg;
import com.jsf.utils.validator.EmptyPattern;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: xujunfei
 * Date: 2020-09-29
 * Time: 16:44
 */
@RestController
@Validated
public class ValidController {

    /**
     * 普通方法参数验证
     * 必须要在类上加@Validated注解，但是不能和以下两个方法混用
     *
     * @param nickname
     * @param phone
     * @return
     */
    @GetMapping("/valid/get")
    public ResMsg validGet(@NotEmpty(message = "昵称不能为空") String nickname,
                           @EmptyPattern(regexp = "^1[3|4|5|7|8][0-9]\\d{8}$", message = "手机号格式错误") String phone) {
        return ResMsg.successdata(nickname + "--" + phone);
    }

    /**
     * POST参数验证，类不能有@Validated注解
     *
     * @param item
     * @param br
     * @return
     */
    @PostMapping("/valid/post1")
    public ResMsg validPost1(@Valid Item item, BindingResult br) {
        if (br.hasErrors()) {
            return ResMsg.fail(br.getFieldError().getDefaultMessage());
        }
        return ResMsg.successdata(item);
    }

    /**
     * JSON参数验证，类不能有@Validated注解
     * 支持嵌套验证，嵌套的字段上必须有@Valid注解
     *
     * @param item
     * @param br
     * @return
     */
    @PostMapping("/valid/post2")
    public ResMsg validPost2(@Valid @RequestBody Item item, BindingResult br) {
        if (br.hasErrors()) {
            return ResMsg.fail(br.getFieldError().getDefaultMessage());
        }
        return ResMsg.successdata(item);
    }


    static class Item {
        @NotEmpty(message = "昵称不能为空")
        @Pattern(regexp = "^[A-Za-z0-9]+$", message = "昵称格式错误，只能是英文和数字")
        @Length(min = 2, max = 16, message = "昵称在2-16个字符")
        private String nickname;

        @EmptyPattern(regexp = "^1[3|4|5|7|8][0-9]\\d{8}$", message = "手机号格式错误")
        private String phone;

        @Min(value = 0)
        private Integer age;

        // *嵌套验证必须用@Valid，而不是@Validated
        @Valid
        @NotNull(message = "属性不能为空")
        @Size(min = 1, message = "最少一个属性")
        private List<Args> agrs;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public List<Args> getAgrs() {
            return agrs;
        }

        public void setAgrs(List<Args> agrs) {
            this.agrs = agrs;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "nickname='" + nickname + '\'' +
                    ", phone='" + phone + '\'' +
                    ", age=" + age +
                    ", agrs=" + agrs +
                    '}';
        }
    }

    static class Args {
        @NotEmpty(message = "属性名不能为空")
        private String name;

        @NotNull(message = "属性值不能为空")
        @Min(value = 0, message = "不能小于0")
        private Integer val;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getVal() {
            return val;
        }

        public void setVal(Integer val) {
            this.val = val;
        }

        @Override
        public String toString() {
            return "Args{" +
                    "name='" + name + '\'' +
                    ", val=" + val +
                    '}';
        }
    }
}
