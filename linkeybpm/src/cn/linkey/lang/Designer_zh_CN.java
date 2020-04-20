package cn.linkey.lang;

import java.util.ListResourceBundle;

public class Designer_zh_CN extends ListResourceBundle {

    private final Object myData[][] = {
            {"DesignerHome", "应用列表" },
            {"ClearEvent", "事件成功清除！" },
            {"Save", "保存(ctr+s)" },
            {"Clear", "清除" },
            {"Preview", "预览" },
            {"Close", "关闭" },
            {"Refresh", "刷新" },
            {"Cancel", "取消" },
            {"SaveEventAndCompile", "保存并编译" },
            {"SaveToJavaFile", "保存到项目源文件中" },
            {"SaveToJavaFileMsg", "源程序成功保存到{0}" },
            {"RemoveApp", "您确认要删除\\\"{0}\\\"应用吗?删除后不可恢复！" },
            {"NewApp", "新建应用" },
            {"EditApp", "编辑应用" },
            {"DelApp", "删除应用" },
            {"AppList", "应用列表" },
            {"DifferentRuleNum", "设计属性的唯一编号与代码中的类名不一至！" },
            {"DifferentRulePackage","包名必须指定为cn.linkey.rulelib.应用编号，请检测应用编号与包路径是否一至！" },
            {"ViewDelDocRollBack", "文档删除失败，本次操作取消！" },
            {"ViewCopyDocRollBack", "文档拷贝失败，本次操作取消！" },
            {"ViewBtnDocRollBack", "本次操作出错，数据已全部回滚！" },
            {"RuleEventType", "Form表单事件|1,View视图事件|2,Data数据源事件|3,Page页面事件|4,定时规则|5,过虑器规则|6" },
            {"RuleCompileType","实时运行(开发阶段选择)|0,编译后运行(正式环境选择)|1" },
            {"SearchReplaceResultMsg","共成功替换{0}个设计元素{1}" },
            {"ImportXmlFileMsg","共成功导入({0})个文档，跳过({1})过文档,不合要求的文档({2})！"},
            {"NewNavTreeItemError","新增子菜单时出错，请联系管理员查看错误日记！"},
            {"NavTreeRootName","根文件夹"},
            {"NavTreeEditError","修改菜单名称时出错，请联系管理员查看错误日记！"},
            {"SaveEditorGridData","共成功新增({0})个文档，修改({1})个文档，删除({2})个文档！"},
            {"SaveEditorGridDataError","错误：数据更新有错误，所有操作取消！"},
            {"NoDesignerPermission","提示：您没有设计权限，不能修改本设计！"}
    };

    public Object[][] getContents() {
        return myData;
    }
}
