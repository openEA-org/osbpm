var fdName="";

//用户选择
function SelUserForMobile(srcFdName){
	$('#win').window({ width:350,height:600,modal:true,minimizable:false,collapsible:false,title:lang.mb_SelUserForMobile_msg01});
	var htmlCode='<div style="margin:5px"> <a href="#" id="btnselok" onclick="selectok();return false;">'+lang.mb_SelUserForMobile_msg02+'</a> <a href="#" id="btnclose" onclick="parent.$(\'#win\').window(\'close\');return false;">'+lang.mb_SelUserForMobile_msg03+'</a>';
	htmlCode+='<hr size=1 style="color:#ccc;width:100%;margin:5px 0px 5px 0px"> ';
	htmlCode+='<input id="UserList" style="display:none"><span id="UserNameList" style="display:none"></span><span id="UserNameAction" style="color:#0033CC"></span>';
	htmlCode+='<hr size=1 style="color:#ccc;width:100%;margin:5px 0px 5px 0px"> ';
	htmlCode+='</div><ul id="tree"></ul>';
    $('#win').html(htmlCode);
	$('#tree').tree({url:'rule?wf_num=R_S023_B012'});
	$('#btnselok').linkbutton({iconCls: 'icon-ok'});
	$('#btnclose').linkbutton({iconCls: 'icon-remove'});

	//初始化js代码 
	fdName=srcFdName;//加入全局变量中
    var userid=window.$("#"+srcFdName).val();
    $("#UserList").val(userid);
    $.post("r?wf_num=R_S023_B013",{userid:userid},function(rs){
        var rs=eval('('+rs+')');
        $("#UserNameList").text(rs.UserName);
        formatUserName();
    })
}

//手写意见功能
function DrawMsgForMobile(){
    $('#win').window({ width:280,height:400,modal:true,minimizable:false,maximizable:false,resizable:false,collapsible:false,title:"手写板"});
    var htmlCode='</div><iframe src="page?wf_num=P_S005_015" style="width:100%;height:99%;" scrolling="no" frameborder="0"></iframe>';
    $('#win').html(htmlCode);
    $('#tree').tree({url:'rule?wf_num=R_S023_B012'});
    $('#btnselok').linkbutton({iconCls: 'icon-ok'});
    $('#btnclose').linkbutton({iconCls: 'icon-remove'});
}

//确定选择
function selectok(){
    window.$("#"+fdName).val($("#UserList").val());
    
    //如果是流程处理单的用户选择则需要增加删除功能
    if(fdName.substring(0,3)=="WF_"){
        var nodeid=fdName.substring(3);
        var userArray=$("#UserList").val().split(",");
        var userNameArray=$("#UserNameList").text().split(",");
        var htmlCode="";
        for(var x=0;x<userArray.length;x++){
            var userid=userArray[x];
            var userName=userNameArray[x];
            if(userName!=""){
                htmlCode+="<a  id=\"U_"+nodeid+"_"+userid+"\" onclick=\"MobileDeleteNodeUser('"+nodeid+"','"+userid+"');return false;\" href='' class=\"fieldShow\" ><img src='linkey/bpm/images/icons/vwicn203.gif'>"+userName+"</a> ";
            }
        }
        window.$("#"+fdName+"_show").html(htmlCode);   
    }else{
        //普通的用户选择字段
        var showobj=window.$("#"+fdName+"_show");
        if(showobj.length>0){
            showobj.text($("#UserNameList").text());
        }
    }
    parent.$('#win').window('close');
}

function selectUser(userid,userName,obj){
    if(obj.checked==true){
        //英文userid
        var userList= $("#UserList").val();
        var tmpUserList=","+userList+",";
        if(tmpUserList.indexOf(","+userid+",")!=-1){return;} //看是否已存在
        
        if(userList==""){
             $("#UserList").val(userid);
        }else{
            $("#UserList").val(userList+","+userid);
        }  
        
        //中文名称
        if($("#UserNameList").text()==""){
             $("#UserNameList").append(userName);
        }else{
            $("#UserNameList").append(","+userName);
        }
    }else{
        //删除usertext
        var userStr="";
        var userNameArray=$("#UserNameList").text().split(",");
        for(var i=0;i<userNameArray.length;i++){
            if(userNameArray[i]!=userName){
                if(userStr!=""){userStr+=",";}
                userStr+=userNameArray[i];
            }
        }
        $("#UserNameList").text(userStr);
        
        //删除userid
        userStr="";
        var userListArray=$("#UserList").val().split(",");
        for(var i=0;i<userListArray.length;i++){
            if(userListArray[i]!=userid){
                if(userStr!=""){userStr+=",";}
                userStr+=userListArray[i];
            }
        }
        $("#UserList").val(userStr);
        
    }
    formatUserName();
    //$('#'+userid).linkbutton({iconCls: 'icon-remove',plain:true,size:'small'});
}

//格式化中文名为可删除样式
function formatUserName(){
    var userText=$("#UserNameList").text();
    if(userText==""){$("#UserNameAction").html(lang.mb_formatUserName_msg03);return;}
    var userArray=userText.split(",");
    var userHtml="";
    var i=0;
    for(i=0;i<userArray.length;i++){
        userHtml+="<span  id='userAction_"+i+"' style='cursor:pointer' onclick=\"deleteUserItemAction('"+userArray[i]+"',"+i+")\"><img src='linkey/bpm/images/icons/vwicn203.gif'>"+userArray[i]+"</span> ";
    }
    $("#UserNameAction").html(userHtml);
}

//点击用户名时删除用户
function deleteUserItemAction(userName,index){
    $("#userAction_"+index).html("");
    deleteUser(userName);
}

//根据用户名删除userid和username
function deleteUser(userName){
    //删除usertext
    var j=0;
    var userStr="";
    var userNameArray=$("#UserNameList").text().split(",");
    for(var i=0;i<userNameArray.length;i++){
        if(userNameArray[i]!=userName){
            if(userStr!=""){userStr+=",";}
            userStr+=userNameArray[i];
        }else if(userNameArray[i]==userName){
           j=i; //标识删除了第几个
        }
    }
    $("#UserNameList").text(userStr);
    
    //删除userid
    userStr="";
    var userListArray=$("#UserList").val().split(",");
    for(var i=0;i<userListArray.length;i++){
        if(i!=j){
            if(userStr!=""){userStr+=",";}
            userStr+=userListArray[i];
        }
    }
    $("#UserList").val(userStr);
}

//表单手写按钮位置
$(document).ready(function() {
    var selW = $("#WF_SelectRemark").width();
    $("#WF_SelectRemark").css("width",selW-50);
});


