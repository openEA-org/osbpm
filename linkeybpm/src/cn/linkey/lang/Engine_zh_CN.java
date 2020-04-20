package cn.linkey.lang;

import java.util.ListResourceBundle;

public class Engine_zh_CN extends ListResourceBundle {

    private final Object myData[][] = {
        {"Error_SaveMainDoc", "文档存盘失败,如有问题请联系管理员!" },
        {"Error_EngineRun", "出错拉，所有操作被取消,您可以尝试再次进行提交操作或联系管理员解决!" },
        {"Error_EngineOpen", "出错拉，流程表单打开错误,如有问题请联系系统管理员解决!" },
        {"EngineDebugMsg","\\n(警告:当前流程处于调试回滚状态,本次操作已取消!)"},
        {"NoApprovalDocument", "文档已被其他用户处理或者您没有处理权限!" },
        {"NoStartDocument", "您无权启动本文档!" },
        {"NoAccessDocument", "您无权访问本文档!" },
        {"DocumentLockedMsg","<div style='padding-left:5px;color:#ff0000'>提示:[{0}]锁定了本文档，本文档为只读状态!</div>" },
        {"ProcessArchived", "文档提交成功并已归档" },
        {"GoToFirstNode", "文档已成功退回给({0})进行处理!" },
        {"RunMsgNodeAndUser", "文档成功给环节({0})用户({1})进行处理!" },
        {"RunMsgOnlyUser", "文档成功提交给用户({0})进行处理!" },
        {"RunMsgOnlyOtherUser", "文档已成功提交，等待用户({0})进行处理!" },
        {"RunMsgSuccess", "文档已成功提交，等待其他用户进行处理!" },
        {"RunMsgOnlyNode", "文档成功提交给环节({0})进行处理!" },
        {"ReturnToPrevUser","文档成功返回给({0})进行处理!"},
        {"BackToReturnUser","文档成功返回给{0}进行处理!"},
        {"ReturnToAnyNode","文档成功回退给环节({0})用户({1})进行处理!"},
        {"GoToNextParallelUser","文档成功提交等待({0})进行会签!"},
        {"GoToNextSerialUser","文档成功提交给({0})进行处理!"},
        {"Undo", "文档已成功回收到您的待办中!" },
        {"UndoError", "文档收回失败，其他用户已处理了本文档!" },
        {"GoToOthers", "文档成功转交给({0})进行处理!" },
        {"StartUser", "已成功提交给({0})进行处理!" },
        {"EndUser", "已成功结束({0})的审批权限!" },
        {"EndNode", "已成功结束({0})节点!" },
        {"GoToArchived","流程已成功归档!"},
        {"EndCopyTo", "您已成功标记为已阅!" }, { "SaveDocOnly", "文档成功保存!" },
        {"EndBusinessName","已归档"},
        {"ActionException","错误:系统运行异常,请联系系统管理员!"},
        {"ApprovalForm_TitleInfo", "请填写以下信息后再提交(当前节点:{0}):" },
        {"ApprovalForm_NodeInfo", "请选择后继节点" },
        {"ApprovalForm_NodeInfo_Mobile", "下一节点" },
        {"ApprovalForm_UserInfo", "请选择\"{0}\"的参与者" },
        {"ApprovalForm_OwnerLimitTime", "限定用户完成时间为:" },
        {"ApprovalForm_ReassignmentBackOption", "完成后返回给我" },
        {"ApprovalForm_OtherUserInfo", "转他人处理" },
        {"ApprovalForm_CopyToInfo", "抄送" },
        {"ApprovalForm_SendSmsInfo", "手机短信通知" },
        {"ApprovalForm_RemarkName", "办理意见" },
        {"ApprovalForm_SelectCommonRemark", "---选择常用处理意见----" },
        {"ApprovalForm_AddRemarkTitle", "加入到常用处理意见中" },
        {"ActionException","错误:系统运行错误，请联系系统管理员!"}
    };

    public Object[][] getContents() {
        return myData;
    }
}
