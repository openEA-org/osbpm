//扩展验证
$.extend($.fn.validatebox.defaults.rules, {
    phone : {// 验证固定电话
        validator : function(value) {
            return /^((\(\d{2,3}\))|(\d{3}\-))?(\(0\d{2,3}\)|0\d{2,3}-)?[1-9]\d{6,7}(\-\d{1,4})?$/i.test(value);
        },
        message : lang.ui_extend_msg01
    },
    CheckboxNotNull : { // 复选框必填
        validator : function(value, param) {
            var chkval = false;
            $('input[name="' + param[0] + '"]:checked').each(function() {
                chkval = true;
            });
            if (chkval == true) {
                $('input[name="' + param[0] + '"]').each(function() {
                    $(this).removeClass("validatebox-invalid");
                });
            }
            return chkval;
        },
        message : lang.ui_extend_msg02
    },
    RadioNotNull : { // 单选按扭必填
        validator : function(value, param) {
            var chkval = false;
            $('input[name="' + param[0] + '"]:checked').each(function() {
                chkval = true;
            });
            if (chkval == true) {
                $('input[name="' + param[0] + '"]').each(function() {
                    $(this).removeClass("validatebox-invalid");
                });
            }
            return chkval;
        },
        message : lang.ui_extend_msg03
    },
    integer : {
        validator : function(value) {
            return /^[+]?[1-9]+\d*$/i.test(value);
        },
        message : lang.ui_extend_msg04
    },
    intOrFloat : {
        validator : function(value) {
            return /^\d+(\.\d+)?$/i.test(value);
        },
        message : lang.ui_extend_msg05
    },
    equalTo : {
        validator : function(value, param) {
            return $("#" + param[0]).val() == value;
        },
        message : lang.ui_extend_msg06
    },
    english : {
        validator : function(value) {
            return /^[A-Za-z]+$/i.test(value);
        },
        message : lang.ui_extend_msg07
    },
    MobilePhone : {// 验证手机电话
        validator : function(value) {
            return /^[1][3,4,5,6,7,8][0-9]{9}$/i.test(value);
        },
        message : "请输入正确的手机号码"
    },
    moreThan : {//判断校验框的数字(value)是否大于设定的数值(param)
        validator : function(value, param) {
            if(/^\d+(\.\d+)?$/i.test(value) && /^\d+(\.\d+)?$/i.test(param)){
                value=parseFloat(value);
                param=parseFloat(param);
                if( value > param){
                    return true;
                } else{
                  //"数值要大于"+param;  
                }
            }else{
                //请输入数字
            }
        },
        message : "格式或数值不符合要求!"
    },
    isIdCard : { //验证身份证
        validator : function(value) {
            return /^[1-9]\d{5}(18|19|([23]\d))\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/i.test(value);
        },
        message: "请输入正确的身份证号码！"
    }


});

function validReadFieldIsNull() {
    // 验证只读模式的必填字段属性，只检测input类型的具有exttype类型的暂不检测
    var r = true;
    $("[data-options*='required:true']").each(function() {
        if ($(this).css("display") == "none" && $(this).attr("exttype") == undefined) {
            var jsonStr = "{" + $(this).attr("data-options") + "}";
            var data = eval('(' + jsonStr + ")");
            if (data.required == true && $(this).val() == "") {
                r = false;
                var missingMessage = data.missingMessage;
                if (missingMessage != "" && missingMessage != undefined) {
                    alert(data.missingMessage);
                }
                else {
                    alert($(this).attr("id") + lang.ui_extend_msg08);
                }
            }
        }
    });
    return r;
}

/******easy ui 函数结束*****/
