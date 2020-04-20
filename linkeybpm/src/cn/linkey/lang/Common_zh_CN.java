package cn.linkey.lang;

import java.util.ListResourceBundle;

public class Common_zh_CN extends ListResourceBundle {

    private final Object myData[][] = { 
            {"yes", "是" }, 
            {"no", "否" },
            {"items", "项" },
            {"NoUploadFile", "系统不允许上传本格式的文件，请换成其他格式!" },
            {"UploadButtonText", "linkey/bpm/images/icons/upload.gif" },
            {"AttachmentInfo", "本附件由({0})环节的{1}在{2}添加" },
            {"AttachmentDele", "删除附件" }, 
            {"AppDocumentSaved", "文档保存成功!" },
            {"AppDocumentSaveError", "出错拉,文档数据未保存!" }, 
            {"Search", "搜索" },
            {"AppDocumentSaved","文档成功保存!"},
            {"AppFormExeButton","操作成功执行!"},
            {"SearchPrompt", "输入关键字后回车..." },
            {"DeleteDocMsg", "成功删除({0})个文档!" },
            {"CopyDocMsg", "成功拷贝({0})个文档!" } ,
            {"ActionError","出错拉！本次操作取消,请联系管理员查看错误日记!"},
            {"AppUnAuthorization","错误:您未授权访问本应用,如有问题请联系管理员!"},
            {"ElementUnAuthorization","错误:您未授权访问本页面,如有问题请联系管理员!"},
            {"AppNotFindOrStop","设计不存在或者本应用未发布!"},
            {"AddMyRemarkInfo","已成功加入常用办理意见中!"},
            {"ErrorNoReadAcl","错误:您未被授权查看本表单数据,如有问题请联系管理员!"},
            {"ErrorNoEditAcl","错误:您未被授权修改本表单数据,如有问题请联系管理员!"}
        };

    public Object[][] getContents() {
        return myData;
    }
}
