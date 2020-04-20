var reloadtreeflag = 0;
var wordtabflag = "0", wordevent = "0", wordid = "";
var Docs = {};
var defaulturl = "";
var defaultid = "form";
var defaulttext = "流程视图";
var west;
Docs = {};

/***前进后退功能实现*/
var hisCurrentCode = "";
var hisEndCode = "";
var hisCurrentIndex = 0;
var hisMaxIndex = 50;
var hisData = new Array();
for (var i = 0; i < hisMaxIndex; i++) {
    hisData[i] = "";
}

//初始化第一个状态
function initHisData() {
    hisData[0] = flowframe.document.all.div1.innerHTML;
}

//增加一个历史记录
function putHisData(htmlCode) {
    if (htmlCode == undefined) {
        if (hisCurrentCode == hisEndCode) {
            return;
        } //说明没有变化的情况下不用存历史记录
        hisCurrentIndex++;
        hisData[hisCurrentIndex] = hisEndCode;
    }
    else {
        hisCurrentIndex++;
        hisData[hisCurrentIndex] = htmlCode;
    }
    if (hisCurrentIndex > hisMaxIndex) {
        //超出最大记录数时需要删除旧的记录数据
        for (var j = 0; j < hisData.length; j++) {
            hisData[j] = hisData[j + 1];
        }
        hisCurrentIndex = hisMaxIndex;
    }
    //flowframe.ShowErrorInfo("put="+hisCurrentIndex);
}
function undo() {
    hisCurrentIndex--;
    if (hisCurrentIndex < 0) {
        hisCurrentIndex = 0;
    }
    var htmlCode = hisData[hisCurrentIndex];
    if (flowframe.document.all.div1.innerHTML != htmlCode) {
        flowframe.document.all.div1.innerHTML = htmlCode;
        //flowframe.ShowErrorInfo("undo="+hisCurrentIndex);
        fixNode();
    }
}


function redo() {
    //flowframe.ShowErrorInfo("redo="+hisCurrentIndex);
    var htmlCode = hisData[hisCurrentIndex + 1];
    if (htmlCode == "" || htmlCode == undefined) {
        return;
    }
    if (flowframe.document.all.div1.innerHTML != htmlCode) {
        flowframe.document.all.div1.innerHTML = htmlCode;
        fixNode();
    }
    hisCurrentIndex++;
}

function fixNode() {
    var AllObjNum = flowframe.document.all.length;
    var ObjArray = new Array();
    var j = 0;
    for (i = 0; i < AllObjNum; i++) {
        var obj = flowframe.document.all(i);
        if (obj.tagName == "polyline") {
            ObjArray[j] = obj;
            j = j + 1;
        }
    }
    for (i = 0; i < ObjArray.length; i++) {
        var obj = ObjArray[i];
        if (obj.oldpoints != "") {
            obj.points.value = obj.oldpoints;
        }
    }
}
/*结束*/

var mainPanel;
var ProcessType = GetUrlArg("ProcessType");
var TreeLoader = new Ext.tree.TreeLoader({
    dataUrl: 'rule?wf_num=R_S002_B012',
    requestMethod: 'POST'
});
var TreeRoot = new Ext.tree.AsyncTreeNode({
    text: '所有节点列表',
    id: 'root',
    Nodeid: 'root',
    singleExpand: true
});
TreeLoader.on("beforeload", function (TreeLoader, node) {
    TreeLoader.baseParams.Processid = processid;
    TreeLoader.baseParams.Nodeid = node.id;
});

if ((typeof Range !== "undefined") && !Range.prototype.createContextualFragment) {
    Range.prototype.createContextualFragment = function (html) {
        var frag = document.createDocumentFragment(),
            div = document.createElement("div");
        frag.appendChild(div);
        div.outerHTML = html;
        return frag;
    };
}

westPanel = function () {
    westPanel.superclass.constructor.call(this, {
        id: 'api-tree',
        region: 'west',
        split: true,
        width: 180,
        minSize: 100,
        maxSize: 200,
        collapsible: true,
        margins: '0 0 0 0',
        cmargins: '0 0 0 0',
        rootVisible: true,
        lines: true,
        autoScroll: true,
        animCollapse: false,
        animate: false,
        title: "流程属性",
        loader: TreeLoader,
        root: TreeRoot,
        collapseFirst: false
    });

};

Ext.extend(westPanel, Ext.tree.TreePanel, {
    selectTreeNode: function (id) {
        this.selectPath('/root/' + id);
    },
    getnode: function (id) {
        this.getNodeById(id)
    }
});

var mainPanel = new Ext.Panel({
    border: true,
    region: 'center',
    margins: '0 0 0 0',
    html: "<iframe name='flowframe' id='flowframe' src='rule?wf_num=R_S002_B005&Processid=" + top.processid + "' width=100% height=100% ></iframe>"
});

var btmPanel = new Ext.Panel({
    border: false,
    region: 'south',
    height: 25,
    split: false,
    margins: '0 0 0 0',
    contentEl: 'south'
});

var hd = new Ext.Panel({
    border: false,
    region: 'north',
    height: 58,
    margins: '0 0 0 0',
    cls: 'docs-header'
});

Ext.onReady(function () {
	

    west = new westPanel();

    west.on('click', function (node, e) {
        e.stopEvent();
        if (node.id == "root") {
            return false;
        }
        focusnode(node.id);
    });

    west.on('contextmenu', function (node, event) {
        var nodeid = node.id;
        var menu = new Ext.menu.Menu({
            id: 'Compose',
            items: [
                new Ext.menu.Item({
                    text: '删除属性',
                    handler: function () {
                        DeleteNode(nodeid, node.text);
                    }
                })
            ]
        });
        event.preventDefault();
        menu.showAt(event.getXY());
    });

    var viewport = new Ext.Viewport({
        layout: 'border',
        items: [hd, west, mainPanel, btmPanel]
    });
    Ext.Viewport.doLayout(viewport);

    var MulSelXYMenu = new Ext.menu.Menu({
        id: 'MulSelMenu',
        items: [
            {
                text: '垂直自动对齐',
                id: 'XDC',
                icon: 'linkey/bpm/images/icons/page-next.gif',
                cls: 'x-btn-text-icon bmenu',
                handler: function () {
                	flowframe.checkAttr();
                    flowframe.MulSel_GropToSameX();
                    flowframe.ToMoveLine();
                }
            },
            {
                text: '水平自动对齐',
                id: 'YDC',
                icon: 'linkey/bpm/images/icons/page-next.gif',
                cls: 'x-btn-text-icon bmenu',
                handler: function () {
                	flowframe.checkAttr();
                    flowframe.MulSel_GropToSameY();
                    flowframe.ToMoveLine();
                }
            },
            {
                text: '垂直自动间距',
                id: 'YJZ',
                icon: 'linkey/bpm/images/icons/page-next.gif',
                cls: 'x-btn-text-icon bmenu',
                handler: function () {
                	flowframe.checkAttr();
                    flowframe.MulSel_AutoYJZ();
                    flowframe.ToMoveLine();
                    
                }
            },
            {
                text: '水平自动间距',
                id: 'XJZ',
                icon: 'linkey/bpm/images/icons/page-next.gif',
                cls: 'x-btn-text-icon bmenu',
                handler: function () {
                	flowframe.checkAttr();
                    flowframe.MulSel_AutoXJZ(false);
                    flowframe.ToMoveLine();
                }
            }
        ]
    });

    var tb = new Ext.Toolbar();
    tb.render('toolbar');
    tb.add('-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/save.gif',
            text: '保存流程',
            handler: function () {
                flowframe.save();
            }
        }, '-', {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/save.gif',
            text: '保存为图片',
            handler: function () {
                location.href = "r?wf_num=R_S002_B022&Processid=" + processid;
            }
        }, '-', {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/undo.gif',
            text: '撒消',
            handler: undo
        }, {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/redo.gif',
            text: '重做',
            handler: redo
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/grid.png',
            text: '显示网格',
            handler: function () {
                flowframe.ShowGrid();
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/page-prev.gif',
            text: '左移(L)',
            handler: function () {
            	flowframe.checkAttr();
                flowframe.MoveCenter('left');
                flowframe.ToMoveLine();
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/ns-collapse.gif',
            text: '上移',
            handler: function () {
            	flowframe.checkAttr();
                flowframe.MoveCenter('top');
                flowframe.ToMoveLine();
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/ns-expand.gif',
            text: '下移',
            handler: function () {
            	flowframe.checkAttr();
                flowframe.MoveCenter('bottom');
                flowframe.ToMoveLine();
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/page-next.gif',
            text: '右移',
            handler: function () {
            	flowframe.checkAttr();
                flowframe.MoveCenter('right');
                flowframe.ToMoveLine();
            }
        }, '-',
        {
            menu: MulSelXYMenu,
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/task.gif',
            text: '自动对齐'
        }, '-', {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/square.gif',
            text: '横向泳道',
            handler: function () {
                flowframe.mySetType('AddXNode');
            }
        }, '-', {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/class.gif',
            text: '纵向泳道',
            handler: function () {
                flowframe.mySetType('AddYNode');
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/text.gif',
            text: '文字',
            handler: function () {
                flowframe.mySetType('AddText');
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/images/icons/del.gif',
            text: '关闭窗口',
            handler: function () {
                top.close();
            }
        }
    ); //tb end

    var imgtb = new Ext.Toolbar();
    imgtb.render('imgtoolbar');
    imgtb.add('-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/mouse.gif',
            text: '移动',
            handler: function () {
                flowframe.mySetType('Move');
            }
        }, {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/mouse.gif',
            text: '多选',
            handler: function () {
                flowframe.mySetType('MulSel');
            }
        }, {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/editdx.gif',
            text: '修改大小',
            handler: function () {
                flowframe.mySetType('EditArea');
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/line.gif',
            text: '直线',
            handler: function () {
                flowframe.mySetType('PolyLine', 'Line');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/lineleftcur.gif',
            text: '左右折线',
            handler: function () {
                flowframe.mySetType('PolyLine', 'Bian');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/linerightcur.gif',
            text: '上下折线',
            handler: function () {
                flowframe.mySetType('PolyLine', 'Bottom');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/line90_1.gif',
            text: '上下直角',
            handler: function () {
                flowframe.mySetType('PolyLine', 'zhexian902');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/line90_2.gif',
            text: '左右直角',
            handler: function () {
                flowframe.mySetType('PolyLine', 'zhexian90');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/linecurv.gif',
            text: '曲线',
            handler: function () {
                flowframe.mySetType('PolyLine', 'zhexian');
            }
        }, '-',
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/activity.gif',
            text: '人工活动',
            handler: function () {
                window.frames["flowframe"].mySetType('AddActivity');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/subprocess.gif',
            text: '内部子流程',
            handler: function () {
                flowframe.mySetType('AddSubProcess');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/subprocess.gif',
            text: '外部子流程',
            handler: function () {
                flowframe.mySetType('AddOutProcess');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/autoactivity.gif',
            text: '自动活动',
            handler: function () {
                flowframe.mySetType('AddAutoActivity');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/edge.gif',
            text: '网关',
            handler: function () {
                flowframe.mySetType('AddEdge');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/start.gif',
            text: '开始',
            handler: function () {
                flowframe.mySetType('AddStart');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/end.gif',
            text: '结束',
            handler: function () {
                flowframe.mySetType('AddEnd');
            }
        },
        {
            cls: 'x-btn-text-icon bmenu',
            icon: 'linkey/bpm/ext/wf/images/event.gif',
            text: '事件',
            handler: function () {
                flowframe.mySetType('AddEvent');
            }
        }, '-'
    );

    west.expandPath('/root');
    
});
var url = location.href;
if (url.indexOf("rule?wf_num=R_S002_B001") == -1) {
    docuemnt.body.innerHTML = "";
}
function DeleteNode(Nodeid, text) {
    if (!confirm("您确认要删除\"" + text + "\" 吗?")) {
        return false;
    }
    ConfirmDelete(Nodeid, text);
}

function ConfirmDelete(Nodeid) {
    top.Ext.getBody().mask('Waiting', 'x-mask-loading');
    var url = "rule?wf_num=R_S002_B011";
    Ext.Ajax.request({
        url: url,
        success: function (response, action) {
            var rs = Ext.util.JSON.decode(response.responseText);
            ReloadTree();
            top.Ext.getBody().unmask();
        },
        failure: function () {
            alert('URL Error!');
            Ext.getBody().unmask();
        },
        params: {Action: "DeleteNode", Processid: processid, Nodeid: Nodeid}
    });
}

function focusnode(Nodeid) {
    flowframe.CancelPrvNextNode();
    for (var i = 0; i < flowframe.document.all.length; i++) {
        var obj = flowframe.document.all(i);
        if (obj.getAttribute("Nodeid") == Nodeid) {
            flowframe.FocusFlowNode(obj);
            return;
        }
    }
    alert("节点已经在图形上被删除!");
    ConfirmDelete(Nodeid);
}

function ReloadTree() {
    reloadtreeflag = 1;
    TreeLoader.load(TreeRoot);
    west.expandPath('/root');
}

function OpenDocument(DocURL, lnum, rnum) {
    var swidth = screen.availWidth;
    var sheight = screen.availHeight;
    if (!lnum) {
        lnum = 14;
    }
    if (!rnum) {
        rnum = 50;
    }
    var wwidth = swidth - lnum;
    var wheight = sheight - rnum;
    var wleft = (swidth / 2 - 0) - wwidth / 2 - 5;
    var wtop = (sheight / 2 - 0) - wheight / 2 - 25;
    window.open(DocURL, '', 'Width=' + wwidth + 'px,Height=' + wheight + 'px,Left=' + wleft + ',Top=' + wtop + ',status=no,resizable=yes,scrollbars=no,resezie=no');
}

